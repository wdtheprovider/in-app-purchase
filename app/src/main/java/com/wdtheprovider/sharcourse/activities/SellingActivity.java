package com.wdtheprovider.sharcourse.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.wdtheprovider.sharcourse.R;
import com.wdtheprovider.sharcourse.adapters.itemAdapter;

import java.util.ArrayList;
import java.util.List;

public class SellingActivity extends AppCompatActivity {

    BillingClient billingClient;
    TextView clicks;
    Button btn_5,btn_15,btn_50;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling);

        sharedPreferences = this.getSharedPreferences("PREFS", MODE_PRIVATE);

        initViews();


        billingClient = BillingClient.newBuilder(getApplicationContext())
                .setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                            for (Purchase purchase : list) {
                                verifyPayment(purchase);
                            }
                        }

                    }
                })
                .enablePendingPurchases()
                .build();

        connectGooglePlayBilling();

    }

    @SuppressLint("SetTextI18n")
    private void initViews() {

        clicks = findViewById(R.id.clicks);

        btn_5 = findViewById(R.id.btn_10);
        btn_15 = findViewById(R.id.btn_20);
        btn_50 = findViewById(R.id.btn_30);

        clicks.setText("You have "+loadData("clicks")+ " click(s)");
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
                    getProducts();
                }

            }
        });
    }


    void getProducts() {
        List<String> products = new ArrayList<>();
        products.add("clicks_5");
        products.add("clicks_10");
        products.add("clicks_50");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(products).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {


                    for (SkuDetails skuDetails : list) {
                        if (skuDetails.getSku().equals("clicks_5")) {
                            btn_5.setText("Add 5 clicks ("+skuDetails.getPrice()+")");
                            btn_5.setOnClickListener(view -> {
                                launchPurchaseFlow(skuDetails);
                            });
                        } else if (skuDetails.getSku().equals("clicks_10")) {
                            btn_15.setText("Add 15 clicks ("+skuDetails.getPrice()+")");
                            btn_15.setOnClickListener(view -> {
                                launchPurchaseFlow(skuDetails);
                            });
                        } else if (skuDetails.getSku().equals("clicks_50")) {
                            btn_50.setText("Add 50 clicks ("+skuDetails.getPrice()+")");
                            btn_50.setOnClickListener(view -> {
                                launchPurchaseFlow(skuDetails);
                            });
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
        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    void verifyPayment(Purchase purchase) {


        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    if (purchase.getSkus().get(0).equals("clicks_5")) {
                        updateClicks(5);
                    } else if (purchase.getSkus().get(0).equals("clicks_10")) {
                        updateClicks(15);
                    } else if (purchase.getSkus().get(0).equals("clicks_50")) {
                        updateClicks(50);
                    }

                }

            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }

    @SuppressLint("SetTextI18n")
    void updateClicks(int v) {
        Log.d("in-app","you just purchased "+v);

        //Saving the clicks in sharedPrefs
        saveData("clicks",String.valueOf(v));
    }


    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    protected void onResume() {
        super.onResume();
        clicks.setText("You have "+loadData("clicks")+ " click(s)");

        billingClient.queryPurchasesAsync(
                BillingClient.SkuType.INAPP,
                new PurchasesResponseListener() {
                    @Override
                    public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (Purchase purchase : list) {
                                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                    verifyPayment(purchase);
                                }
                            }
                        }
                    }
                }
        );
    }


    @SuppressLint("SetTextI18n")
    public  void saveData(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        clicks.setText("You have "+loadData("clicks")+ " click(s)");
    }

    public String loadData(String key) {
        return sharedPreferences.getString(key, "0");
    }
}