package com.experta.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.experta.R;
import com.experta.com.experta.model.Device;
import com.experta.com.experta.model.Status;
import com.experta.qrScanner.SimpleScannerActivity;
import com.experta.services.ToastService;
import com.experta.ui.dialogs.AliasDialog;
import com.experta.ui.dispositivos.DispositivosFragment;
import com.experta.utilities.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DeviceInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    public final String LOGTAG = this.getClass().getSimpleName();

    private static final int ZOOM = 17;

    private Device device;
    private GoogleMap map;
    private Button renameDevice, reattachDevice, deleteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        Intent intent = getIntent();
        if (intent.hasExtra(DispositivosFragment.DEVICE_EXTRA)) {
            device = (Device) intent.getSerializableExtra(DispositivosFragment.DEVICE_EXTRA);
        }

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        renameDevice = findViewById(R.id.reNameBtn);
        renameDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                renameDialog();
            }
        });
        reattachDevice = findViewById(R.id.reAttachBtn);
        reattachDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceInfoActivity.this, SimpleScannerActivity.class);
                intent.putExtra(SimpleScannerActivity.VINCULAR_DISPOSITIVO, false);
                startActivity(intent);
                finish();
            }
        });
        deleteDevice = findViewById(R.id.deleteDeviceBtn);
        deleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                areYouSureDialog();
            }
        });
    }

    private void renameDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final AliasDialog aliasDialog = new AliasDialog(this);
        aliasDialog.show(fragmentManager, "tagRename");
    }

    private void areYouSureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceInfoActivity.this);
        builder.setMessage(getString(R.string.esta_seguro))
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // El usuario confirma cerrar sesión
                        new DeleteDeviceTask().execute(device.getMacAddress());
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // El usuario cancela cerrar sesión (se mantiene el fragment anterior)
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.getUiSettings().setMapToolbarEnabled(false);

        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(device.getLatitude(), device.getLongitude())
                                                                            , ZOOM);

        Log.i(LOGTAG, "Device Status: " + device.getGeneralStatus());
        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        switch (device.getGeneralStatus()) {
            case NORMAL:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                break;
            case ALARM:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                break;
            case INACTIVE:
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                break;
        }

        map.addMarker(new MarkerOptions().position(new LatLng(device.getLatitude(), device.getLongitude()))
                                         .title(device.getAlias())
                                         .icon(icon))
           .setDraggable(false);

        map.moveCamera(camUpd1);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void changeAlias(String newAlias) {
        Log.i(LOGTAG, "Changing alias of device " + device.getMacAddress() + " from " + device.getAlias() + " to " + newAlias);

        device.setAlias(newAlias);
        RenameDeviceTask renameDeviceTask = new RenameDeviceTask();
        renameDeviceTask.execute(device);
    }

    public class RenameDeviceTask extends AsyncTask<Device, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Device... params) {
            Log.i(LOGTAG, "RenameDeviceTask doInBackground");

            return NetworkUtils.renameDeviceByUserAndMac(BottomNavActivity.user, params[0]);
        }

        @Override
        protected void onPostExecute(Boolean devRenamedCorrectly) {
            Log.i(LOGTAG, "RenameDeviceTask onPostExecute");

            if (devRenamedCorrectly) {
                ToastService.toastCenter(getApplicationContext(), getString(R.string.dispositivo_renombrado), Toast.LENGTH_SHORT);

                // Esperamos y volvemos a la activity inicial
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);

            } else {
                ToastService.toastCenter(getApplicationContext(), getString(R.string.error_renombrando), Toast.LENGTH_SHORT);
            }
        }
    }

    public class DeleteDeviceTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Log.i(LOGTAG, "DeleteDeviceTask doInBackground");

            return NetworkUtils.deleteDeviceByUserAndMac(BottomNavActivity.user, params[0]);
        }

        @Override
        protected void onPostExecute(Boolean devAttachedCorrectly) {
            Log.i(LOGTAG, "DeleteDeviceTask onPostExecute");

            if (devAttachedCorrectly) {
                ToastService.toastCenter(getApplicationContext(), getString(R.string.dispositivo_eliminado), Toast.LENGTH_SHORT);

                // Esperamos y volvemos a la activity inicial
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);

            } else {
                ToastService.toastCenter(getApplicationContext(), getString(R.string.error_eliminando_dispositivo), Toast.LENGTH_SHORT);
            }
        }
    }
}
