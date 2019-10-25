package com.experta.utilities;

import android.util.Log;

import com.experta.com.experta.model.Contact;
import com.experta.com.experta.model.Device;
import com.experta.com.experta.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    public static final String LOGTAG = NetworkUtils.class.getSimpleName();

    public static final String SERVER_BASE_URL = "https://still-shelf-00010.herokuapp.com/";
    public static final String USERS = "/users/";
    public static final String DEVICES = "/devices";
    public static final String CONTACTS = "/contacts";

    public static boolean createUser(User user) {

        Response response;

        try {
            URL url = new URL(SERVER_BASE_URL + USERS);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), user.jsonString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).method("POST", body).build();

            response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        if (response != null && response.isSuccessful()) {
            return true;
        }

        return false;
    }

    public static boolean doesUserExist(String userEmail) {

        Response response;

        try {
            URL url = new URL(SERVER_BASE_URL + USERS + userEmail);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        if (response != null && response.isSuccessful()) {
            return true;
        }

        return false;
    }

    private static String getDataFromServer(URL url) throws IOException {

        String data = "";
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                data = scanner.next();
            }

        } finally {
            urlConnection.disconnect();
        }

        return data;
    }

    public static Device[] getDeviceListFromServer(String userEmail) throws IOException {

        URL url = new URL(SERVER_BASE_URL + USERS + userEmail + DEVICES);
        String jsonString = getDataFromServer(url);

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

    public static Contact[] getContactListFromServer(String userEmail) throws IOException {

        URL url = new URL(SERVER_BASE_URL + USERS + userEmail + CONTACTS);
        String jsonString = getDataFromServer(url);

        Log.i(LOGTAG, jsonString);

        return jsonStringAsContactArray(jsonString);
    }

    private static Contact[] jsonStringAsContactArray(String jsonString) {

        if (jsonString == null || jsonString.isEmpty()) {
            return new Contact[] {};
        }

        Contact[] contacts = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            contacts = mapper.readValue(jsonString, Contact[].class);
        } catch (IOException e) {
            // JSON MALFORMADO
            e.printStackTrace();
        }

        return contacts;
    }

}
