package com.nujat.yumorder.home_page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.nujat.yumorder.MainActivity;
import com.nujat.yumorder.R;
import com.nujat.yumorder.login_page.LoginPage;

import java.util.HashMap;
import java.util.Map;


public class HomePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        findViewById(R.id.continueButton).setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, MainActivity.class);
            startActivity(intent);
        });
        try {
            initConfig();
        } catch (Exception e) {
            Log.d("Media", String.valueOf(e));
        }
    }
    private void initConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "doe159xiw");
        config.put("api_key", "948418582526433");
        config.put("api_secret", "1PKhn1Q7Y2u45ZXKE_v_k4P_bj0");
//        config.put("secure", true);
        MediaManager.init(this, config);

    }
}
