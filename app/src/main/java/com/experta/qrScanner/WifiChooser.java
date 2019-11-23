package com.experta.qrScanner;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.experta.R;
import com.experta.services.ToastService;
import com.experta.ui.AttachDeviceActivity;
import com.experta.ui.dialogs.AliasDialog;

import java.util.ArrayList;
import java.util.List;

public class WifiChooser extends AppCompatActivity implements View.OnClickListener {

    // El modelo está hardcodeado, el siguiente paso es tomarlo dinámicamente del dispositivo
    public static final String DEVICE_MODEL = "NodeMCU";
    public static final String DEVICE_SSID = "DEVICE_SSID";
    public static final String DEVICE_PASSWORD = "DEVICE_PASSWORD";
    public static final String DEVICE_ALIAS = "DEVICE_ALIAS";
    public static final String AP_SSID = "AP_SSID";
    public static final String AP_PASSWORD = "AP_PASSWORD";
    public static final String FORMER_AP_SSID = "FORMER_AP_SSID";

    private final String LOGTAG = this.getClass().getSimpleName();

    private boolean vincularDipositivo;
    private WifiManager wifiManager;

    private Button sendButton;
    private ListView wifiList;
    private ArrayAdapter<String> adaptador;
    private EditText passwordET;

    private String deviceSSID = "", devicePassword = "", deviceAlias = null;
    private String formerApSSID = null;

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
                    ToastService.toastTop(getApplicationContext(), getString(R.string.no_hay_redes_disponibles), Toast.LENGTH_SHORT);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        ssids = new ArrayList<>();

        scanAvailableWiFiRouters();

        setContentView(R.layout.activity_wifi_chooser);
        getSupportActionBar().setTitle(getString(R.string.red_para_dispositivo));

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

        if (intent.hasExtra(SimpleScannerActivity.VINCULAR_DISPOSITIVO)) {
            vincularDipositivo = intent.getBooleanExtra(SimpleScannerActivity.VINCULAR_DISPOSITIVO, false);
        }
    }

    private void scanAvailableWiFiRouters() {
        // Si no estaba el Wifi prendido, lo prendemos. Caso contrario nos guardamos la red para
        // reconectar después del apareamiento
        if (!wifiManager.isWifiEnabled()) {
            // If wifi is disabled then enable it
            ToastService.toastCenter(getApplicationContext(), getString(R.string.habilitando_wifi), Toast.LENGTH_SHORT);
            wifiManager.setWifiEnabled(true);
        } else {
            formerApSSID = wifiManager.getConnectionInfo().getSSID();
        }

        // Escaneo de redes disponibles, sólo si no lo hizo antes
        if (ssids.size() == 0) {

            registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            if (wifiManager.startScan()) {
                ToastService.toastCenter(getApplicationContext(), getString(R.string.escaneando_redes), Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
        }
    }

    @Override
    public void onClick(View v) {

        Log.i(LOGTAG, "onClick()");

        if (TextUtils.isEmpty(selectedSSID)) {
            ToastService.toastTop(getApplicationContext(), getString(R.string.seleccione_red), Toast.LENGTH_SHORT);
            return;
        }

        if (vincularDipositivo) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            final AliasDialog aliasDialog = new AliasDialog(this);
            aliasDialog.show(fragmentManager, "tagAlias");
        } else {
            sendConnectionDataToDevice();
        }
    }

    public void setDeviceAlias(String deviceAlias) {
        this.deviceAlias = deviceAlias;
    }

    public void sendConnectionDataToDevice() {

        Log.i(LOGTAG, "sendConnectionDataToDevice()");

        // Tomamos la información de la conexión actual, para reconectar luego de pasar los datos
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        Intent intent = new Intent(this, AttachDeviceActivity.class);
        intent.putExtra(DEVICE_SSID, deviceSSID);
        intent.putExtra(DEVICE_PASSWORD, devicePassword);
        intent.putExtra(DEVICE_ALIAS, deviceAlias);
        intent.putExtra(DEVICE_MODEL, DEVICE_MODEL);
        intent.putExtra(AP_SSID, selectedSSID);
        intent.putExtra(AP_PASSWORD, passwordET.getText().toString());
        intent.putExtra(FORMER_AP_SSID, formerApSSID);
        intent.putExtra(SimpleScannerActivity.VINCULAR_DISPOSITIVO, vincularDipositivo);

        startActivity(intent);
        finish();
    }
}