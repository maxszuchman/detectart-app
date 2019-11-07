package com.experta.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.experta.R;
import com.experta.com.experta.model.Device;
import com.experta.com.experta.model.Status;
import com.experta.ui.dispositivos.DispositivosFragment;
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
}
