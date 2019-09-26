package me.dm7.barcodescanner.zxing.sample;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static java.net.Proxy.Type.HTTP;

public class SimpleScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

    public static final String SSID_TAG = "SSID";
    public static final String PWD_TAG = "PWD";

    private ZXingScannerView mScannerView;
    private ViewGroup contentFrame;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner);
        setupToolbar();

        contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(this, "Contents = " + rawResult.getText(), Toast.LENGTH_SHORT).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mScannerView.resumeCameraPreview(SimpleScannerActivity.this);
//            }
//        }, 2000);

        String ssid = "";
        String password = "";

        try {
            JSONObject jsonObject = new JSONObject(rawResult.getText());

            ssid = jsonObject.getString("ssid");
            password = jsonObject.getString("password");
        } catch (JSONException e) {

            Toast.makeText(this, getString(R.string.codigo_qr_incorrecto), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(SimpleScannerActivity.this, WifiChooser.class);
        intent.putExtra(SSID_TAG, ssid);
        intent.putExtra(PWD_TAG, password);

        startActivity(intent);
    }

}
