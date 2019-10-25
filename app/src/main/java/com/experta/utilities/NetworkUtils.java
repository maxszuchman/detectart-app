package com.experta.utilities;

import android.util.Log;

import com.experta.com.experta.model.Device;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    public static final String LOGTAG = NetworkUtils.class.getSimpleName();

    public static final String SERVER_BASE_URL = "https://still-shelf-00010.herokuapp.com/";
    public static final String USERS = "/users/";
    public static final String DEVICES = "/devices";

    public static Device[] getDeviceListFromServer(String userEmail) throws IOException {

        String jsonString = null;

        URL url = new URL(SERVER_BASE_URL + USERS + userEmail + DEVICES);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                jsonString = scanner.next();
            }

        } finally {
            urlConnection.disconnect();
        }

        Log.i(LOGTAG, jsonString);

        return jsonStringAsDeviceArray(jsonString);
    }

    private static Device[] jsonStringAsDeviceArray(String jsonString) {

        if (jsonString == null || jsonString.isEmpty()) {
            return new Device[] {};
        }

        Device[] devices = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            devices = mapper.readValue(jsonString, Device[].class);
        } catch (IOException e) {
            // JSON MALFORMADO
            e.printStackTrace();
        }

        return devices;
    }
}
