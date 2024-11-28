package com.only.quotes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        //getSupportActionBar().hide();
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(FlashActivity.this, GenerateCardActivity.class);
            startActivity(intent);
            finish();
        }, 1000);
    }
}
