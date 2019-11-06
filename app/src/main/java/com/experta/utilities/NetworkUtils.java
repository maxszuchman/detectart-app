package com.experta.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.experta.com.experta.model.Contact;
import com.experta.com.experta.model.Device;
import com.experta.com.experta.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {

    public static final String LOGTAG = NetworkUtils.class.getSimpleName();

    public static final String SERVER_BASE_URL = "https://still-shelf-00010.herokuapp.com";
    public static final String USERS = "/users/";
    public static final String DEVICES = "/devices";
    public static final String CONTACTS = "/contacts";

    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean isInternetAvailable(Context ctx) {

        ConnectivityManager connectivityManager;
        Context context = ctx.getApplicationContext();
        boolean connected = false;

        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }

    public static boolean createUser(User user) {

        boolean success = false;
        Response response = null;

        try {
            URL url = new URL(SERVER_BASE_URL + USERS);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), user.jsonString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).method("POST", body).build();

            response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null && response.isSuccessful()) {
            success = true;
        }

        response.body().close();
        return success;
    }

    public static User getUserByEmail(String userEmail) {
        User user = null;

        try {
            URL url = new URL(SERVER_BASE_URL + USERS + userEmail);
            String json = getDataFromServer(url);
            user = jsonStringAsUser(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }

    private static User jsonStringAsUser(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return new User();
        }

        User user = null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            user = mapper.readValue(jsonString, User.class);
        } catch (IOException e) {
            // JSON MALFORMADO
            e.printStackTrace();
        }

        return user;
    }

    public static boolean doesUserExist(String userEmail) {

        boolean success = false;
        Response response = null;

        try {
            URL url = new URL(SERVER_BASE_URL + USERS + userEmail);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null && response.isSuccessful()) {
            success = true;
        }

        response.body().close();
        return success;
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

    public static boolean addContactForUser(User user, Contact contact) {

        boolean success = false;
        Response response = null;

        try {
            URL url = new URL(SERVER_BASE_URL + USERS + user.getId() + CONTACTS);

            Log.i(LOGTAG, contact.jsonString());

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), contact.jsonString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).method("POST", body).build();

            response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null && response.isSuccessful()) {
            success = true;
        }

        response.body().close();
        return success;
    }

    public static boolean deleteContactByUser(User user, Contact contact) {

        boolean success = false;
        Response response = null;

        try {
            URL url = new URL(SERVER_BASE_URL + USERS + user.getId() + CONTACTS + "/" + contact.getId());

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), contact.jsonString());

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).method("DELETE", body).build();

            response = client.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null && response.isSuccessful()) {
            success =  true;
        }

        response.body().close();
        return success;
    }

    public static boolean addDeviceByUser(User user, String deviceMacAddress, String deviceAlias, String deviceModel) {

        boolean success = false;
        Response response = null;

        try {

            URL url = new URL(SERVER_BASE_URL + USERS + user.getId() + DEVICES);

            String json = createDeviceJson(deviceMacAddress, deviceAlias, deviceModel);
            Log.i(LOGTAG, json);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).method("POST", body).build();

            response = client.newCall(request).execute();

        }  catch (IOException e) {

            e.printStackTrace();
        }

        if (response != null && response.isSuccessful()) {
            success = true;
        }

        response.body().close();
        return success;
    }

    private static String createDeviceJson(String deviceMacAddress, String deviceAlias, String deviceModel)
        throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("macAddress", deviceMacAddress);
        json.put("alias", deviceAlias);
        json.put("model", deviceModel);
        json.put("latitude", 0.0);
        json.put("longitude", 0.0);
        json.put("accuracy", 0.0);

        return mapper.writeValueAsString(json);
    }
}
