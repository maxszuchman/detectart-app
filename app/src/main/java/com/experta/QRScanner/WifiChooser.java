package com.experta.QRScanner;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.experta.R;
import com.experta.com.experta.model.Contact;
import com.experta.ui.BottomNavActivity;
import com.experta.ui.dialogs.AliasDialog;
import com.experta.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WifiChooser extends AppCompatActivity implements View.OnClickListener {

    // El modelo está hardcodeado, el siguiente paso es tomarlo dinámicamente del dispositivo
    private static final String DEVICE_MODEL = "NodeMCU";

    private final String LOGTAG = this.getClass().getSimpleName();

    private WifiManager wifiManager;

    private Button sendButton;
    private ListView wifiList;
    private ArrayAdapter<String> adaptador;
    private EditText passwordET;
    private String password;

    private String deviceSSID = "", devicePassword = "", deviceAlias = null, deviceMacAddress = null;
    private boolean deviceAttached = false;

    private List<String> ssids;
    private String selectedSSID = "";

    // Recibe el escaneo de redes disponibles
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent intent) {

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                ssids.clear();
                List<ScanResult> scanResults = wifiManager.getScanResults();

                if (scanResults.size() == 0) {
                    toast(getString(R.string.no_hay_redes_disponibles), Toast.LENGTH_SHORT);
                    return;
                }

                List<ScanResult> results = filterOnlyChannelZero(scanResults);

                Log.i(LOGTAG, "Se encontraron " + results.size() + " redes disponibles.");

                for (ScanResult result : results) {
                    Log.i(LOGTAG, "\n" + result.toString());
                    ssids.add(result.SSID);
                }

                Log.i(LOGTAG, "ssids cargados");
                adaptador.notifyDataSetChanged();

                unregisterReceiver(mWifiScanReceiver);
            }
        }

        private List<ScanResult> filterOnlyChannelZero(List<ScanResult> originalResults) {

            List<ScanResult> results = new ArrayList<>();

            for (ScanResult result : originalResults) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (result.frequency < 3000) {
                        results.add(result);
                    }
                } else {

                   return originalResults;

                }
            }

            return results;
        }
    };

    // Recibe la conexión con un nuevo WiFi
    private BroadcastReceiver wifiConnectionBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                WifiInfo currentWifiInfo = wifiManager.getConnectionInfo();
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                // Chequeamos conexión al dispositivo y que se haya elegido una red wifi
                if (currentWifiInfo.getSSID().contains(deviceSSID)
                    && info.isConnected()
                    && !TextUtils.isEmpty(selectedSSID)) {

                    toast(getString(R.string.conectando_al_dispositivo), Toast.LENGTH_SHORT);
                    Log.i(LOGTAG, "Conectado al dispositivo");

                    // Tomamos la mac del dispositivo para vincularla con un usuario en el servidor
                    deviceMacAddress = currentWifiInfo.getMacAddress();

                    sendData();

                // Si hubo un cambio de conexión wifi y no es al dispositivo, suponemos que hubo una
                // reconexión a Internet. Chequeamos que no se hayan mandado datos del dispositivo y enviamos
                } else if (!deviceAttached && deviceMacAddress != null && deviceAlias != null) {

                    new AttachDeviceTask().execute(deviceMacAddress, deviceAlias);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiConnectionBroadcastReceiver, intentFilter);

        ssids = new ArrayList<>();

        scanAvailableWiFiRouters();

        setContentView(R.layout.activity_wifi_chooser);

        sendButton = findViewById(R.id.send_data_button);
        sendButton.setOnClickListener(this);

        wifiList = findViewById(R.id.wifi_networks_lv);
        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, ssids);
        wifiList.setAdapter(adaptador);
        wifiList.setSelector(R.drawable.list_item_selector);
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSSID = ssids.get(position);
            }
        });

        passwordET = findViewById(R.id.wifi_password_et);

        Intent intent = getIntent();
        if (intent.hasExtra(SimpleScannerActivity.SSID_TAG)) {
            deviceSSID = intent.getStringExtra(SimpleScannerActivity.SSID_TAG);
        }

        if (intent.hasExtra(SimpleScannerActivity.PWD_TAG)) {
            devicePassword = intent.getStringExtra(SimpleScannerActivity.PWD_TAG);
        }
    }

    private void scanAvailableWiFiRouters() {
        if (!wifiManager.isWifiEnabled()) {
            // If wifi disabled then enable it
            toast(getString(R.string.habilitando_wifi), Toast.LENGTH_LONG);
            wifiManager.setWifiEnabled(true);
        }

        // Escaneo de redes disponibles, sólo si no lo hizo antes
        if (ssids.size() == 0) {

            registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            if (wifiManager.startScan()) {
                toast(getString(R.string.escaneando_redes), Toast.LENGTH_LONG);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
        }
    }

    @Override
    public void onClick(View v) {

        Log.i(LOGTAG, "onClick()");

        if (TextUtils.isEmpty(selectedSSID)) {
            toast(getString(R.string.seleccione_red), Toast.LENGTH_SHORT);
            return;
        }

        if (TextUtils.isEmpty(passwordET.getText().toString())) {
            toast(getString(R.string.ingrese_contra), Toast.LENGTH_SHORT);
            return;
        }

        // TODO SACAR!!!!
//        Toast.makeText(this, "SSID: " + selectedSSID + "\nPWD: " + password, Toast.LENGTH_LONG).show();
        ///////////////////////////////////

        FragmentManager fragmentManager = getSupportFragmentManager();
        final AliasDialog aliasDialog = new AliasDialog(this);
        aliasDialog.show(fragmentManager, "tagAlerta");

//        sendConnectionDataToDevice();
    }

    public void sendConnectionDataToDevice() {

        Log.i(LOGTAG, "sendConnectionDataToDevice()");

        // Tomamos la información de la conexión actual, para reconectar luego de pasar los datos
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // Conectamos al dispositivo, A PARTIR DE ACÁ SE ENCARGA EL BROADCAST RECEIVER
        connectToWifi(deviceSSID, devicePassword);
    }

    /**
     * Connect to the specified wifi network.
     *
     * @param networkSSID     - The wifi network SSID
     * @param networkPassword - the wifi password
     */
    private void connectToWifi(final String networkSSID, final String networkPassword) {

        toast(getString(R.string.conectando_al_dispositivo), Toast.LENGTH_LONG);

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

    private void sendData() {

        toast(getString(R.string.enviando_datos_al_dispositivo), Toast.LENGTH_LONG);

        URL url = null;

        try {
            url = new URL("http://192.168.4.1/connectionData/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        this.password = passwordET.getText().toString();
        new SendDataTask().execute(url);
    }

    private void toast(String message, int length) {
        Toast toast = Toast.makeText(this, message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public class SendDataTask extends AsyncTask<URL, Void, Boolean> {

        // COMPLETED (26) Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mLoadingIndicator.setVisibility(View.VISIBLE);
            Log.i(LOGTAG, "onPreExecute()");
        }

        @Override
        protected Boolean doInBackground(URL... params) {
            Log.i(LOGTAG, "doInBackground()");

            URL url = params[0];

            RequestBody body = new MultipartBody.Builder()
                                                .setType(MultipartBody.FORM)
                                                .addFormDataPart("ssid", selectedSSID)
                                                .addFormDataPart("password", password)
                                                .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                                         .url(url)
                                         .method("POST", body)
                                         .build();
            try {
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    return true;
                }

            } catch (IOException e) {

            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(LOGTAG, "onPostExecute()");

            if (result) {
                toast(getString(R.string.datos_enviados_correctamente), Toast.LENGTH_SHORT);
            }
            // COMPLETED (27) As soon as the loading is complete, hide the loading indicator
//            mLoadingIndicator.setVisibility(View.INVISIBLE);
//            if (githubSearchResults != null && !githubSearchResults.equals("")) {
//                // COMPLETED (17) Call showJsonDataView if we have valid, non-null results
//                showJsonDataView();
//                mSearchResultsTextView.setText(githubSearchResults);
//            } else {
//                // COMPLETED (16) Call showErrorMessage if the result is null in onPostExecute
//                showErrorMessage();
//            }
        }
    }

    public class AttachDeviceTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            Log.i(LOGTAG, "doInBackground");

            return NetworkUtils.addDeviceByUser(BottomNavActivity.user, params[0], params[1], DEVICE_MODEL);
        }

        @Override
        protected void onPostExecute(Boolean devAttachedCorrectly) {
            Log.i(LOGTAG, "onPostExecute");

            deviceAttached = devAttachedCorrectly;
        }
    }

    public void setDeviceAlias(String deviceAlias) {
        this.deviceAlias = deviceAlias;
    }

}
