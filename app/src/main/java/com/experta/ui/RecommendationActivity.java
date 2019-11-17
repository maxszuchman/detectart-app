package com.experta.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.experta.R;

public class RecommendationActivity extends AppCompatActivity {

    public final String LOGTAG = RecommendationActivity.class.getSimpleName();

    public static final String RECOMMENDATION = "RECOMMENDATION";

    private TextView recommendationTV;
    private String recommendation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOGTAG, "onCreate()");

        setContentView(R.layout.activity_recommendation);

        Intent intent = getIntent();
        if (intent.hasExtra(RECOMMENDATION)) {
            recommendation = intent.getStringExtra(RECOMMENDATION);
        }
//            Log.i(LOGTAG, "finish()");
//            finish();
//        }

        recommendationTV = findViewById(R.id.recommendationsTV);
        recommendationTV.setText(recommendation);
    }
}
