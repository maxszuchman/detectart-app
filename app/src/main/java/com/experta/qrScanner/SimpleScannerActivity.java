package com.experta.qrScanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.experta.R;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

    public static final String SSID_TAG = "SSID";
    public static final String PWD_TAG = "PWD";

    public static final int CAMERA_PERMISSION_CODE = 100;

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

        // Chequeamos que haya permiso de la cámara en tiempo de ejecución
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this
                                              , new String[] { Manifest.permission.CAMERA }
                                              , CAMERA_PERMISSION_CODE);
        }
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(this, getString(R.string.permisos_camara), Toast.LENGTH_LONG).show();
            }

        }
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
        finish();
    }

}
