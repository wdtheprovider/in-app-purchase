package com.wdtheprovider.inapppurchase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.utils.Prefs;

public class SplashActivity extends AppCompatActivity {

    Handler handler;
    BillingClient billingClient;
    Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();
        prefs = new Prefs(this);

        handler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 3000);

    }

}