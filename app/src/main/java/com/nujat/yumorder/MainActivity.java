package com.nujat.yumorder;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nujat.yumorder.home_page.HomePage;
import com.nujat.yumorder.login_page.LoginPage;
import com.nujat.yumorder.main_page.MainPage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the user is already logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is logged in, navigate to HomePage
            Intent intent = new Intent(MainActivity.this, MainPage.class);
            startActivity(intent);
        } else {
            // User is not logged in, navigate to LoginPage
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
        }

        // Close MainActivity so it won't show again when user presses back
        finish();
    }
}
