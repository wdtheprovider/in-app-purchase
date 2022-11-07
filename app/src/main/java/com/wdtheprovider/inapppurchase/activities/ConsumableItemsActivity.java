package com.wdtheprovider.inapppurchase.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.common.collect.ImmutableList;
import com.wdtheprovider.inapppurchase.R;
import com.wdtheprovider.inapppurchase.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

public class ConsumableItemsActivity extends AppCompatActivity {

    BillingClient billingClient;
    TextView clicks;
    Button btn_5;
    Prefs prefs ;
    List<ProductDetails> productDetailsList;
    Activity activity;
    String TAG = "TestInApp";
    Handler handler;
    ProgressBar progress_circular;
    List<String> productIds;
    List<Integer> coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling);

        initViews();
        activity = this;

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(
                        (billingResult, list) -> {
                            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase: list){
                                    verifyPurchase(purchase);
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        connectGooglePlayBilling();

        btn_5.setOnClickListener(v -> {
            //we are opening product at index zero since we only have one product
            launchPurchaseFlow(productDetailsList.get(0));
        });
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {

        clicks = findViewById(R.id.clicks);
        btn_5 = findViewById(R.id.btn_10);
        progress_circular = findViewById(R.id.progress_circular);

        prefs = new Prefs(this);
        handler = new Handler();

        productIds = new ArrayList<>();
        coins = new ArrayList<>();
        productDetailsList = new ArrayList<>();

        productIds.add("10_coins_id");
        coins.add(10);

        clicks.setText("You have "+prefs.getInt("coins",0)+ " coins(s)");
    }

    void connectGooglePlayBilling() {

        Log.d(TAG,"connectGooglePlayBilling ");

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showProducts();
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")

    void showProducts() {

        Log.d(TAG,"showProducts ");

        ImmutableList<QueryProductDetailsParams.Product> productList = ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("test_coins_111")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, list) -> {
            //Clear the list
            productDetailsList.clear();

            Log.d(TAG,"Size "+list.size());

            //Handler to delay by two seconds to wait for google play to return the list of products.
            handler.postDelayed(() -> {
                //Adding new productList, returned from google play
                productDetailsList.addAll(list);

                //Since we have one product, we use index zero (0) from list
                ProductDetails productDetails = list.get(0);

                //Getting product details
                String price = productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice();
                String productName = productDetails.getName();

                //Updating the UI
                //If the product is not showing then it means that you didn't properly setup your Testing email.
                btn_5.setText(price +"  -  "+productName);

                //Showing the button.
                btn_5.setVisibility(View.VISIBLE);
                progress_circular.setVisibility(View.INVISIBLE);

                }, 2000);
        });
    }


    void launchPurchaseFlow(ProductDetails productDetails) {
        ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                ImmutableList.of(BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build());
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();
        billingClient.launchBillingFlow(activity, billingFlowParams);
    }

    void verifyPurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        ConsumeResponseListener listener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                giveUserCoins(purchase);
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }

    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifyPurchase(purchase);
                            }
                        }
                    }
                }
        );
    }

    @SuppressLint("SetTextI18n")
    void giveUserCoins(Purchase purchase) {
        //set coins
        prefs.setInt("coins",(coins.get(0) * purchase.getQuantity()) + prefs.getInt("coins",0));
        //Update UI
        clicks.setText("You have "+prefs.getInt("coins",0)+ " coins(s)");
    }
}