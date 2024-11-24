package com.nujat.yumorder.home_page;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.nujat.yumorder.R;


public class HomePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

//        findViewById(R.id.someButton).setOnClickListener(v -> {
//            Intent intent = new Intent(HomePage.this, AnotherActivity.class);
//            startActivity(intent);
 //       });
    }
}
