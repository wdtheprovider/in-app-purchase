package com.wdtheprovider.sharcourse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView clicks;
    Button btn_store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();


        btn_store.setOnClickListener(view -> {
            startActivity(new Intent(this,SellingActivity.class));
        });
    }

    private void initViews() {

        clicks=findViewById(R.id.clicks);
        btn_store=findViewById(R.id.btn_store);
    }
}