package com.nujat.yumorder.registration_page;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.nujat.yumorder.R;
import com.nujat.yumorder.login_page.LoginPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationPage extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp, btnLogin;

    private FirebaseFirestore db;  // Firestore instance
    private FirebaseAuth auth;  // FirebaseAuth instance

    // Patterns for validation
    private static final Pattern namePattern = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);

        // Initialize Firebase components
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnLogin = findViewById(R.id.btn_login);

        // Navigate to login page
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
            startActivity(intent);
        });

        // Handle Sign Up button click
        btnSignUp.setOnClickListener(v -> validateAndRegister());
    }

    private void validateAndRegister() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate input
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required!");
            etFullName.requestFocus();
            return;
        }
        if (!namePattern.matcher(fullName).matches()) {
            etFullName.setError("Enter a valid name (letters and spaces only)!");
            etFullName.requestFocus();
            return;
        }
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
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required!");
            etPassword.requestFocus();
            return;
        }
        if (!passwordPattern.matcher(password).matches()) {
            etPassword.setError("Password must contain at least one digit, one lowercase, one uppercase, one special character, and be 6 or more characters long!");
            etPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirm your password!");
            etConfirmPassword.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match!");
            etConfirmPassword.requestFocus();
            return;
        }

        // Disable the sign-up button during registration attempt
        btnSignUp.setEnabled(false);

        // Register the user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User is registered successfully
                        FirebaseUser user = auth.getCurrentUser();

                        // Save user data to Firestore
                        saveDataToFirestore(fullName, email, user.getUid());
                    } else {
                        // Handle registration failure
                        btnSignUp.setEnabled(true); // Re-enable the sign-up button
                        Toast.makeText(RegistrationPage.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveDataToFirestore(String fullName, String email, String uid) {
        // Create a map with the user's data
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);

        // Save the data in Firestore under the 'users' collection
        db.collection("users")
                .document(uid)  // Use UID as document ID
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    btnSignUp.setEnabled(true);  // Re-enable the button
                    Toast.makeText(RegistrationPage.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    // Redirect to the login page or another page
                    Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSignUp.setEnabled(true);  // Re-enable the button
                    Toast.makeText(RegistrationPage.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
