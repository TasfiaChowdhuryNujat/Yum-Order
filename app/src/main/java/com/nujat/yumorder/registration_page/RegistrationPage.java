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

import java.util.regex.Pattern;

public class RegistrationPage extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp,btnLogin;

    // Patterns for validation
    private static final Pattern namePattern = Pattern.compile("^[a-zA-Z ]+$");
    private static final Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);

        // Initialize views
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                startActivity(intent);
            }
        });
        // Set up the sign-up button listener
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndRegister();
            }
        });
    }

    private void validateAndRegister() {
        // Get input values
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate full name
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
            etPassword.setError("Password must contain at least one digit, one lowercase, one uppercase, one special character, and be 6 or more characters long!");
            etPassword.requestFocus();
            return;
        }

        // Validate confirm password
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

        // If all validations pass
        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();

        // Perform further actions like storing data in the database
    }
}
