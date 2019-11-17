package com.experta.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.experta.R;

public class RecommendationActivity extends AppCompatActivity {

    public static final String RECOMMENDATION = "RECOMMENDATION";

    private TextView recommendationTV;
    private String recommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        Intent intent = getIntent();
        if (intent.hasExtra(RECOMMENDATION)) {
            recommendation = intent.getStringExtra(RECOMMENDATION);
        } else {
            finish();
        }

        recommendationTV = findViewById(R.id.recommendationsTV);
        recommendationTV.setText(recommendation);
    }
}
