package com.nujat.yumorder;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.nujat.yumorder.home_page.HomePage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start HomePage activity after MainActivity finishes
        Intent intent = new Intent(MainActivity.this, HomePage.class);
        startActivity(intent);
        finish(); // Optional: Close MainActivity so it won't show again when user presses back
    }
}