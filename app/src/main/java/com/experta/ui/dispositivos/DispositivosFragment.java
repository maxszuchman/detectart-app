package com.experta.ui.dispositivos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.experta.qrScanner.SimpleScannerActivity;
import com.experta.R;
import com.experta.com.experta.model.Device;
import com.experta.ui.BottomNavActivity;
import com.experta.ui.DeviceInfoActivity;
import com.experta.ui.adapters.DeviceAdapter;
import com.experta.utilities.NetworkUtils;

import java.io.IOException;

public class DispositivosFragment extends Fragment {

    public static final String LOGTAG = DispositivosFragment.class.getSimpleName();

    public static final String DEVICE_EXTRA = "DEVICE_EXTRA";

    private static final int HALF_MINUTE = 30000;

    private Device[] devices = new Device[] {};
    private DeviceAdapter adapter;
    private ListView lstDevices;

    private boolean keepGettingDevices;
    private GetDevicesTask getDevicesTask;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dispositivos, container, false);
        setHasOptionsMenu(true);

        adapter = new DeviceAdapter(getContext(), devices);
        lstDevices = root.findViewById(R.id.LstDevices);
        lstDevices.setAdapter(adapter);
        lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                Device selectedDevice = (Device) a.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), DeviceInfoActivity.class);
                intent.putExtra(DEVICE_EXTRA, selectedDevice);

                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        keepGettingDevices = true;
        getDevicesListEvery(HALF_MINUTE);
    }

    @Override
    public void onPause() {
        super.onPause();
        getDevicesTask.cancel(true);
        keepGettingDevices = false;
    }

    private void getDevicesListEvery(final int time) {

        if (keepGettingDevices) {
            getDevicesTask = new GetDevicesTask();
            getDevicesTask.execute(BottomNavActivity.user.getId());

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    getDevicesListEvery(time);
                }
            }, time);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_dispositivo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_dispositivo) {
            Intent intent = new Intent(getContext(), SimpleScannerActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetDevicesTask extends AsyncTask<String, Void, Device[]> {

        @Override
        protected Device[] doInBackground(String... params) {

            Log.i(LOGTAG, "GetDevicesTask - doInBackground");

            Device[] returnedDevices = null;

            try {
                returnedDevices = NetworkUtils.getDeviceListFromServer(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnedDevices;
        }

        @Override
        protected void onPostExecute(Device[] devices) {
            Log.i(LOGTAG, "GetDevicesTask - onPostExecute");

            setDevices(devices);
        }
    }

    public void setDevices(Device[] devices) {
        this.devices = devices;
        Log.i(LOGTAG, "Devices: ");

        if (devices != null) {
            for (Device device : devices) {
                Log.i(LOGTAG, " " + device.toString());
            }

            // Lo hacemos asi porque porque adapter.notifyDataSetChanged() no anda
            adapter = new DeviceAdapter(getContext(), devices != null? devices : new Device[]{});
            lstDevices.setAdapter(adapter);
        }
    }
}
