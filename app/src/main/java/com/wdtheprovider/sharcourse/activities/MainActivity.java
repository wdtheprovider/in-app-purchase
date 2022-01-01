package com.wdtheprovider.sharcourse.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wdtheprovider.sharcourse.R;

public class MainActivity extends AppCompatActivity {

    TextView clicks;
    Button btn_store, btn_clear;

    SharedPreferences sharedPreferences;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("PREFS", MODE_PRIVATE);


        initViews();

        btn_store.setOnClickListener(view -> {
            startActivity(new Intent(this, SellingActivity.class));
        });

        btn_clear.setOnClickListener(view -> {
            sharedPreferences.edit().clear().apply();
            Toast.makeText(MainActivity.this, "Clicks cleared", Toast.LENGTH_SHORT).show();
            clicks.setText("You have " + loadData("clicks") + " click(s)");
        });


    }

    @SuppressLint("SetTextI18n")
    private void initViews() {

        clicks = findViewById(R.id.clicks);
        btn_store = findViewById(R.id.btn_store);
        btn_clear = findViewById(R.id.btn_clear);

        clicks.setText("You have " + loadData("clicks") + " click(s)");
    }

    public String loadData(String key) {
        return sharedPreferences.getString(key, "0");
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        clicks.setText("You have " + loadData("clicks") + " click(s)");
    }
}