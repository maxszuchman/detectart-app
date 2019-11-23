package com.experta.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.experta.R;
import com.experta.qrScanner.SimpleScannerActivity;
import com.experta.qrScanner.WifiChooser;
import com.experta.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttachDeviceActivity extends AppCompatActivity {

    public final String LOGTAG = this.getClass().getSimpleName();

    private TextView label;

    private WifiManager wifiManager;

    private boolean connectingToDeviceSSID;
    private boolean vinculateDevice;
    private boolean waitingForInternetConnectionToReturn = false;

    private String deviceSSID, devicePassword, deviceAlias, deviceModel, deviceMacAddress;
    private String apSSID, apPassword;
    private String formerApSSID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiConnectionBroadcastReceiver, intentFilter);

        setContentView(R.layout.activity_attach_device);
        label = findViewById(R.id.stateTV);

        getIntents();
        Log.i(LOGTAG, "Attach Device to User: " + vinculateDevice);

        connectToWifi(deviceSSID, devicePassword);
    }

    private void getIntents() {
        Intent intent = getIntent();
        if (intent.hasExtra(WifiChooser.DEVICE_SSID)) {
            deviceSSID = intent.getStringExtra(WifiChooser.DEVICE_SSID);
        }

        if (intent.hasExtra(WifiChooser.DEVICE_PASSWORD)) {
            devicePassword = intent.getStringExtra(WifiChooser.DEVICE_PASSWORD);
        }

        if (intent.hasExtra(WifiChooser.DEVICE_ALIAS)) {
            deviceAlias = intent.getStringExtra(WifiChooser.DEVICE_ALIAS);
        }

        if (intent.hasExtra(WifiChooser.DEVICE_MODEL)) {
            deviceModel = intent.getStringExtra(WifiChooser.DEVICE_MODEL);
        }

        if (intent.hasExtra(WifiChooser.AP_SSID)) {
            apSSID = intent.getStringExtra(WifiChooser.AP_SSID);
        }

        if (intent.hasExtra(WifiChooser.AP_PASSWORD)) {
            apPassword = intent.getStringExtra(WifiChooser.AP_PASSWORD);
        }

        if (intent.hasExtra(WifiChooser.FORMER_AP_SSID)) {
            formerApSSID = intent.getStringExtra(WifiChooser.FORMER_AP_SSID);
        }

        if (intent.hasExtra(SimpleScannerActivity.VINCULAR_DISPOSITIVO)) {
            vinculateDevice = intent.getBooleanExtra(SimpleScannerActivity.VINCULAR_DISPOSITIVO, false);
        }
    }

    /**
     * Connect to the specified wifi network.
     *
     * @param networkSSID     - The wifi network SSID
     * @param networkPassword - the wifi password
     */
    private void connectToWifi(final String networkSSID, final String networkPassword) {

        connectingToDeviceSSID = true;
        Log.i(LOGTAG, "CONECTANDO A Dispositivo SSID: " + networkSSID + " - Password: " + networkPassword);
        label.setText(R.string.conectando_al_dispositivo);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", networkSSID);
        conf.preSharedKey = String.format("\"%s\"", networkPassword);

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    private void sendDataToDevice() {

        label.setText(R.string.enviando_datos_al_dispositivo);

        URL url = null;

        try {
            url = new URL("http://192.168.4.1/connectionData/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new SendDataToDeviceTask().execute(url);
    }

    // Recibe la conexión con un nuevo WiFi
    private BroadcastReceiver wifiConnectionBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                Log.i(LOGTAG, "CAMBIO EN EL ESTADO DE LA RED.");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                // Si el cambio es por una nueva conexión a wifi
                if (info.getState() == NetworkInfo.State.CONNECTED) {

                    WifiInfo currentWifiInfo = wifiManager.getConnectionInfo();
                    Log.i(LOGTAG, "NUEVA CONEXIÓN A UNA RED: " + currentWifiInfo.getSSID());

                    // Chequeamos conexión al dispositivo
                    // connectingToDeviceSSID es un flag para mandar los datos al dispositivo
                    // sólo si lo queremos hacer, no en cualquier reconexión a red
                    if (currentWifiInfo.getSSID().contains(deviceSSID) && info.isConnected()
                        && connectingToDeviceSSID) {

                        connectingToDeviceSSID = false;

                        // Tomamos la mac del dispositivo para vincularla con un usuario en el servidor
//                        deviceMacAddress = currentWifiInfo.getMacAddress();
                        // TODO Tomar la MAC de la red y no del nombre del SSID
                        deviceMacAddress = deviceSSID.substring(8);

                        Log.i(LOGTAG, "CONECTADO AL DISPOSITIVO, RED: " + currentWifiInfo.getSSID());
                        Log.i(LOGTAG, "Su MAC es: " + deviceMacAddress);

                        // unregisterReceiver(this);

                        // Pequeña espera para asentar la conexión
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                sendDataToDevice();
                            }
                        }, 500);

                    } else if (waitingForInternetConnectionToReturn
                               && info.isConnected()
                               && currentWifiInfo.getSSID().contains(formerApSSID)) {

                        Log.i(LOGTAG, "Volvió internet.");
                        unregisterReceiver(this);

                        // Si simplemente estabamos esperando Internet y se conectó, volvemos, pero esperamos antes
                        // si no, mandamos el post al servidor para vincular el dispositivo al User
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                if (NetworkUtils.isInternetAvailable(getApplicationContext())) {
                                    if (vinculateDevice) {
                                        new AttachDeviceTask().execute(deviceMacAddress, deviceAlias, deviceModel);
                                    } else {
                                        finish();
                                    }
                                } else {
                                    waitForInternetConnection();
                                }

                            }
                        }, 5000);
                    }
                }
            }
        }
    };

    public class SendDataToDeviceTask extends AsyncTask<URL, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(URL... params) {

            URL url = params[0];

            RequestBody body = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("ssid", apSSID)
                                                .addFormDataPart("password", apPassword)
                                                .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                                         .url(url)
                                         .post(body)
                                         .build();

            Call call = client.newCall(request);
            Response response = null;

            Log.i(LOGTAG, "SendDataToDeviceTask doInBackground() - ENVIANDO DATOS AL DISPOSITIVO.");
            Log.i(LOGTAG, request.toString());

            try {
                response = call.execute();

                if (response.isSuccessful()) {
                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();

                Log.i(LOGTAG, "Request:");
                Log.i(LOGTAG, request.toString());
                Log.i(LOGTAG, request.body().toString());

                if (response != null) {
                    Log.i(LOGTAG, "Response:");
                    Log.i(LOGTAG, response.message());
                    Log.i(LOGTAG, response.headers().toString());
                    Log.i(LOGTAG, response.body().toString());
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(LOGTAG, "SendDataToDeviceTask onPostExecute()");

            if (result) {
                Log.i(LOGTAG, "DATOS ENVIADOS AL DISPOSITIVO CORRECTAMENTE.");
                label.setText(R.string.datos_enviados_correctamente);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        waitingForInternetConnectionToReturn = true;
                        waitForInternetConnection();

                    }
                }, 500);

            } else {
                Log.i(LOGTAG, "ERROR ENVIANDO DATOS AL DISPOSITIVO.");
                label.setText(R.string.error_datos_al_disp);
                finish();
            }
        }
    }

    private void waitForInternetConnection() {

        label.setText(R.string.esperando_internet);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", formerApSSID);

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Log.i(LOGTAG, "Intentando reconectar a red " + formerApSSID);
    }

    public class AttachDeviceTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            Log.i(LOGTAG, "AttachDeviceTask doInBackground");

            return NetworkUtils.addDeviceByUser(BottomNavActivity.user, params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(Boolean devAttachedCorrectly) {
            Log.i(LOGTAG, "AttachDeviceTask onPostExecute");

            if (devAttachedCorrectly) {

                label.setText(R.string.disp_apareado_correcto);
            } else {

                label.setText(R.string.error_disp_apareado);
            }

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    finish();
                }
            }, 1000);
        }
    }
}