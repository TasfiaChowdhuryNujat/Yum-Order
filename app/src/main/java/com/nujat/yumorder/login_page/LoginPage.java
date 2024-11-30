package com.nujat.yumorder.login_page;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nujat.yumorder.R;
import com.nujat.yumorder.home_page.HomePage;
import com.nujat.yumorder.main_page.MainPage;
import com.nujat.yumorder.registration_page.RegistrationPage;

import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;

    // Patterns for validation
    private static final Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$");

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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndLogin();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
                startActivity(intent);
            }
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
        if (!emailPattern.matcher(email).matches()) {
            etEmail.setError("Enter a valid email!");
            etEmail.requestFocus();
            return;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required!");
            etPassword.requestFocus();
            return;
        }
        if (!passwordPattern.matcher(password).matches()) {
            etPassword.setError("Invalid password format!");
            etPassword.requestFocus();
            return;
        }
        // Firebase Authentication
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to Home Page
                        Intent intent = new Intent(LoginPage.this, MainPage.class);
                        startActivity(intent);
                        finish(); // Optional, to close the LoginPage
                    } else {
                        // Login failed
                        Toast.makeText(LoginPage.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
