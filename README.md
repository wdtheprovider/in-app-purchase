# Welcome to in-app-purchase (Android Studio Java*)

In this repository i'm going to show you how to integrate In-App Purchase of Google Play Billing version 4+ in 6 steps.

Demo <br>

<span> <img src="https://github.com/wdtheprovider/in-app-purchase/blob/master/app/src/main/res/drawable/screen_1.jpg" width="290" height="600">
<img src="https://github.com/wdtheprovider/in-app-purchase/blob/master/app/src/main/res/drawable/screen_2.jpg" width="290" height="600">
<img src="https://github.com/wdtheprovider/in-app-purchase/blob/master/app/src/main/res/drawable/screen_3.jpg" width="290" height="600">
</span>

Pre-requisite
- Google Play Console Account
- Published App on Play Store
- Tester Device with GMS

YouTube Video: Part-1 | Intro Demo: https://youtu.be/ihL0jW5cFtM
<br>YouTube Video: Part-2 | Configure Testing Device: https://youtu.be/j6wWVMj-fi8
<br>YouTube Video: Part-3 | Integrating The Methods to purchase the products: Subscribe for the video to be released.<br>

```
Configure Your Testing device by adding the gmail account to internal testing testers 
and License testing (Watch the YouTube video for clarity: https://youtu.be/j6wWVMj-fi8 )


Setup the in-app purchase products in Google Play Console account
i have already created mine which are 
Product ID: clicks_5
Product ID: clicks_10
Product ID: clicks_50

```



The following methods (These are the methods you need for the IAP System to work, you can copy and paste)

void connectGooglePlayBilling(){}<br>
void getProducts(){}<br>
void launchPurchaseFlow(){}<br>
void verifyPayment(Purchase purchases){}<br>
void giveCoins(){}<br>

Step 0: //Add the Google Play Billing Library dependency<br>
Step 1: //Initialize a BillingClient with PurchasesUpdatedListener<br>
Step 2: //Establish a connection to Google Play<br>
Step 3: //Show products available to buy<br>
Step 4: //Launch the purchase flow<br>
Step 5: //Processing purchases / Verify Payment<br>
Step 6: //Handling pending transactions<br>


First we will look at consumable items/products 

more info. 

For consumables, the consumeAsync() method fulfills the acknowledgement requirement and indicates that your app has granted entitlement to the user. This method also enables your app to make the one-time product available for purchase again.
<br> Learn More: https://developer.android.com/google/play/billing/integrate


Step 0: //Add the Google Play Billing Library dependency<br>
```
//Add the Google Play Billing Library dependency to your app's build.gradle file as shown:

dependencies {
    def billing_version = "4.0.0"

    implementation "com.android.billingclient:billing:$billing_version"
}

And Open Manifest File and add this permission
<uses-permission android:name="com.android.vending.BILLING" />

```
Step 1: //Initialize a BillingClient with PurchasesUpdatedListener<br>

```
  //Initialize a BillingClient with PurchasesUpdatedListener onCreate method

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
                
      // call connectGooglePlayBilling()
      connectGooglePlayBilling();
                
```
Step 2: //Establish a connection to Google Play<br>

```
void connectGooglePlayBilling() {

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Connected " + 0);
                    getProducts();
                }

            }
        });

    }
    
```
Step 3: //Show products available to buy<br>

```
     void getProducts() {

        List<String> skuList = new ArrayList<>();
        
        //replace these with your product IDs from google play console
        skuList.add("clicks_5");
        skuList.add("clicks_10");
        skuList.add("clicks_50");

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

                            for (SkuDetails skuDetails : skuDetailsList) {
                                if (skuDetails.getSku().equals("clicks_5")) {
                                    clicks_5.setText("Add 5 clicks (" + skuDetails.getPrice() + ")");
                                    clicks_5.setOnClickListener(view -> {
                                        launchPurchaseFlow(skuDetails);
                                    });
                                } else if (skuDetails.getSku().equals("clicks_10")) {
                                    clicks_15.setText("Add 15 clicks (" + skuDetails.getPrice() + ")");
                                    clicks_15.setOnClickListener(view -> {
                                        launchPurchaseFlow(skuDetails);
                                    });
                                } else if (skuDetails.getSku().equals("clicks_50")) {
                                    clicks_50.setText("Add 50 clicks (" + skuDetails.getPrice() + ")");
                                    clicks_50.setOnClickListener(view -> {
                                        launchPurchaseFlow(skuDetails);
                                    });
                                }

                            }
                        }

                    }
                });

    }
    
```
Step 4: //Launch the purchase flow<br>

```
  void launchPurchaseFlow(SkuDetails skuDetails) {

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        billingClient.launchBillingFlow(PremiumActivity.this, billingFlowParams);
    }
    
```
Step 5: //Processing purchases / Verify Payment<br>

```
 void verifyPayment(Purchase purchase) {


        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    Log.d(TAG, purchase.getSkus().get(0) + " sku");

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
    
```

Step 6: //Handling pending transactions<br>

```
 protected void onResume() {
        super.onResume();
        you_have_tv.setText("You have " + adsPref.getClicks() + " click(s)");

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
```



        

