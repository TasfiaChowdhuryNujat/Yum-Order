package com.nujat.yumorder.login_page;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nujat.yumorder.R;
import com.nujat.yumorder.admin_page.AdminPage;
import com.nujat.yumorder.main_page.MainPage;
import com.nujat.yumorder.registration_page.RegistrationPage;

public class LoginPage extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        // Initialize views
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        // Set up listeners
        btnLogin.setOnClickListener(v -> validateAndLogin());

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
            startActivity(intent);
        });
    }

    private void validateAndLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required!");
            etEmail.requestFocus();
            return;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required!");
            etPassword.requestFocus();
            return;
        }

        // Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful, check user's userType in Firestore
                        String userId = auth.getCurrentUser().getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("users").document(userId).get()
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful() && dbTask.getResult() != null) {
                                        DocumentSnapshot document = dbTask.getResult();
                                        String userType = document.getString("userType");

                                        if ("admin".equals(userType)) {
                                            // Navigate to AdminPage if user is admin
                                            Intent intent = new Intent(LoginPage.this, AdminPage.class);
                                            startActivity(intent);
                                        } else {
                                            // Navigate to MainPage if user is not admin
                                            Intent intent = new Intent(LoginPage.this, MainPage.class);
                                            startActivity(intent);
                                        }
                                    } else {
                                        // Handle potential errors (e.g., no document found or other failure)
                                        Toast.makeText(LoginPage.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                                    }
                                    finish(); // Close LoginPage
                                });
                    } else {
                        // Login failed
                        Toast.makeText(LoginPage.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
