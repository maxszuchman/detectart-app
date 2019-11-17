package com.experta.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.experta.com.experta.model.Sensor;

public class COActivity extends Activity {

    public final String LOGTAG = COActivity.class.getSimpleName();;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOGTAG, "onCreate()");

        Intent intent = new Intent(getApplicationContext(), RecommendationActivity.class);
        intent.putExtra(RecommendationActivity.RECOMMENDATION, Sensor.CO.getRecommendation());
        startActivity(intent);
        finish();
    }
}
