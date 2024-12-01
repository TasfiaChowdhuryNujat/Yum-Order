package com.nujat.yumorder;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nujat.yumorder.home_page.HomePage;
import com.nujat.yumorder.login_page.LoginPage;
import com.nujat.yumorder.main_page.MainPage;
import com.nujat.yumorder.admin_page.AdminPage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the user is already logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // User is logged in, check their userType
            String userId = auth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            String userType = document.getString("userType");
                            if ("admin".equals(userType)) {
                                // Navigate to AdminPage
                                Intent intent = new Intent(MainActivity.this, AdminPage.class);
                                startActivity(intent);
                            } else {
                                // Navigate to MainPage
                                Intent intent = new Intent(MainActivity.this, MainPage.class);
                                startActivity(intent);
                            }
                        } else {
                            // Handle potential errors (e.g., no document found or other failure)
                            Intent intent = new Intent(MainActivity.this, LoginPage.class);
                            startActivity(intent);
                        }
                        // Close MainActivity after deciding the navigation
                        finish();
                    });
        } else {
            // User is not logged in, navigate to LoginPage
            Intent intent = new Intent(MainActivity.this, LoginPage.class);
            startActivity(intent);
            finish();
        }
    }
}
