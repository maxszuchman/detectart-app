package com.experta.ui.dispositivos;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.experta.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DispositivosFragment extends Fragment {

    ArrayList<String> dispositivos;
    TextView tDispositivo;
    Handler messageHandler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dispositivos, container, false);
        setHasOptionsMenu(true);

        internetConnection connection = new internetConnection();
        connection.execute();

        dispositivos = new ArrayList<>();


        tDispositivo = (TextView) root.findViewById(R.id.mDispositivo);


        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_dispositivo, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_dispositivo:
                // Llama a la actividad add dispositivo
        }
        return super.onOptionsItemSelected(item);
    }

    private class internetConnection extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://demo8098036.mockable.io/users/1/devices");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                if (connection.getResponseCode() == 200) {
                    displayMsg("conectado");
                    InputStream body_response = connection.getInputStream();
                    InputStreamReader response = new InputStreamReader(body_response, "UTF-8");

                    processJSON(response);
                } else {
                    connection.getResponseMessage();
                }

                connection.disconnect();

            } catch (Exception error) {
                displayMsg(error.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public void processJSON(InputStreamReader streamReaded) {
        JsonReader json = new JsonReader(streamReaded);
        try {
            json.beginObject();
            while (json.hasNext()) {
                displayMsg("proceso json");
                String dispositivo = json.nextName();
                changeUI(dispositivo);
                json.skipValue();

            }
            json.endObject();
        } catch(Exception error) {
            displayMsg(error.getMessage());
        }
    }

    // Métodos para cambios en la ui porque no se pueden realizar en background

    public void displayMsg(final String errorText) {
        Runnable doDisplayError = new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), errorText, Toast.LENGTH_LONG).show();
            }
        };
        messageHandler.post(doDisplayError);
    }

    public void changeUI(String msg) {
        final String str = msg;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tDispositivo.setText(str);
            }
        });
    }
}
