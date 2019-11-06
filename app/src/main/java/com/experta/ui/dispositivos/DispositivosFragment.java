package com.experta.ui.dispositivos;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.experta.qrScanner.SimpleScannerActivity;
import com.experta.R;
import com.experta.com.experta.model.Device;
import com.experta.services.ToastService;
import com.experta.ui.BottomNavActivity;
import com.experta.ui.adapters.DeviceAdapter;
import com.experta.utilities.NetworkUtils;

import java.io.IOException;

public class DispositivosFragment extends Fragment {

    public static final String LOGTAG = DispositivosFragment.class.getSimpleName();

    private Device[] devices = new Device[] {};
    private DeviceAdapter adapter;
    private ListView lstDevices;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dispositivos, container, false);
        setHasOptionsMenu(true);

        GetDevicesTask getDevicesTask = new GetDevicesTask();
        getDevicesTask.execute(BottomNavActivity.user.getId());

        adapter = new DeviceAdapter(getContext(), devices);
        lstDevices = root.findViewById(R.id.LstDevices);
        lstDevices.setAdapter(adapter);
        lstDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                String selectedOption = ((Device) a.getItemAtPosition(position)).getAlias();
                ToastService.toast(getContext(), selectedOption, Toast.LENGTH_SHORT);
            }
        });

        return root;
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

            Log.i(LOGTAG, "doInBackground");

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
            Log.i(LOGTAG, "onPostExecute");

            setDevices(devices);
        }
    }

    public void setDevices(Device[] devices) {
        this.devices = devices;
        Log.i(LOGTAG, "Devices: ");
        for (Device device : devices) {
            Log.i(LOGTAG, " " + device.toString());
        }

        // Lo hacemos asi porque porque adapter.notifyDataSetChanged() no anda
        adapter = new DeviceAdapter(getContext(), devices != null? devices : new Device[]{});
        lstDevices.setAdapter(adapter);
    }
}
