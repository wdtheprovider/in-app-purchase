package com.wdtheprovider.sharcourse.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.utils.Prefs;

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
        checkProducts();
        handler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 3000);

    }


    void checkProducts() {

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener((billingResult, list) -> {
        }).build();

        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult1, list) -> {
                        Log.d("TestRed", "" + list);
                        if (list.size() == 0) {
                            Log.d("TestRed", "No product");
                            prefs.setRemoveAd(0);
                        } else {
                            for (Purchase purchase : list) {
                                if (purchase.getSkus().get(0).equals("remove_ads_id")) {
                                    Log.d("TestRed", "" + purchase.getSkus() + " Product found");
                                    prefs.setRemoveAd(1);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

}