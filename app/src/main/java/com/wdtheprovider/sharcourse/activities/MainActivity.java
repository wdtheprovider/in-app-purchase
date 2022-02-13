package com.wdtheprovider.sharcourse.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView clicks;
    Button btn_store, btn_clear,btn_remove_ad;
    BillingClient billingClient;

    AdView mAdView;

    Prefs prefs;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        btn_remove_ad = findViewById(R.id.Removed_ad);

        prefs = new Prefs(getApplicationContext());


        if (prefs.getRemoveAd()==0){
            //initializing admob and loading the banner ad.
            MobileAds.initialize(this, initializationStatus -> {
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }


        initViews();

        billingClient = BillingClient.newBuilder(getApplicationContext())
                .setListener((billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                        for (Purchase purchase : list) {
                            verifyPayment(purchase);
                        }
                    }

                })
                .enablePendingPurchases()
                .build();

        // call connectGooglePlayBilling()
        connectGooglePlayBilling();


        btn_store.setOnClickListener(view -> {
            startActivity(new Intent(this, SellingActivity.class));
        });

        btn_clear.setOnClickListener(view -> {
            prefs.removeKey("clicks");
            Toast.makeText(MainActivity.this, "Clicks cleared", Toast.LENGTH_SHORT).show();
            clicks.setText("You have " + prefs.getInt("clicks",0) + " click(s)");
        });

    }


    @SuppressLint("SetTextI18n")
    private void initViews() {

        clicks = findViewById(R.id.clicks);
        btn_store = findViewById(R.id.btn_store);
        btn_clear = findViewById(R.id.btn_clear);

        clicks.setText("You have " + prefs.getInt("clicks",0) + " click(s)");
    }



    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        clicks.setText("You have " + prefs.getInt("clicks",0) + " click(s)");
        Toast.makeText(MainActivity.this, " Ad- "+prefs.getRemoveAd(), Toast.LENGTH_SHORT).show();
    }




    void connectGooglePlayBilling() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d("RemovedAd", "Connected " + 0);
                    getProducts();
                }

            }
        });

    }


    void getProducts() {

        List<String> skuList = new ArrayList<>();

        //replace these with your product IDs from google play console
        skuList.add("remove_ads_id");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {

                            Log.d("remove",skuDetailsList+"");

                            for (SkuDetails skuDetails: skuDetailsList){
                                if (skuDetails.getSku().equals("remove_ads_id")){
                                    btn_remove_ad.setVisibility(View.VISIBLE);
                                    btn_remove_ad.setOnClickListener(view -> {
                                        launchPurchaseFlow(skuDetails);
                                    });
                                }
                                else {
                                    btn_remove_ad.setVisibility(View.GONE);
                                }
                            }


                        }

                    }
                });

    }

    void launchPurchaseFlow(SkuDetails skuDetails) {

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
    }


    void verifyPayment(Purchase purchase) {


        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // 1 - True
                        // 0 - False
                        prefs.setRemoveAd(1);
                    }

                });
            }
        }


    }



}