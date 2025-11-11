package com.example.studentrecord.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentrecord.R;
import com.example.studentrecord.util.SupabaseConfig;

import io.github.jan.supabase.auth.exception.AuthRestException;
import io.github.jan.supabase.auth.user.User;
import io.github.jan.supabase.postgrest.from;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText etEmail, etPassword;
    private Spinner spinnerRole;
    private Button btnSignup;
    private TextView tvLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);

        // Set up spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        btnSignup.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem() != null ? spinnerRole.getSelectedItem().toString() : "";

            // Validation
            if (email.isEmpty()) {
                etEmail.setError("Email is required");
                etEmail.requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Please enter a valid email address");
                etEmail.requestFocus();
                return;
            }
            if (pass.isEmpty()) {
                etPassword.setError("Password is required");
                etPassword.requestFocus();
                return;
            }
            if (pass.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                etPassword.requestFocus();
                return;
            }
            if (role.isEmpty()) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            signup(email, pass, role);
        });

        tvLogin.setOnClickListener(v -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void signup(String email, String pass, String role) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        btnSignup.setEnabled(false);

        // Use Supabase authentication
        SupabaseConfig.getAuth().signUpWith(email, pass)
            .onSuccess(result -> {
                User user = SupabaseConfig.getAuth().getCurrentUser();
                if (user != null) {
                    // Save user data to Supabase database
                    saveUserToDatabase(user.getId(), email, role);
                } else {
                    Toast.makeText(this, "Signup failed: User not created", Toast.LENGTH_LONG).show();
                    resetSignupState();
                }
            })
            .onFailure(exception -> {
                Log.e(TAG, "Signup failed", exception);
                String errorMessage = "Signup failed";
                if (exception instanceof AuthRestException) {
                    errorMessage = ((AuthRestException) exception).getErrorDescription();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                resetSignupState();
            });
    }

    private void saveUserToDatabase(String uid, String email, String role) {
        // Create user data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", uid);
        userData.put("email", email);
        userData.put("role", role.toLowerCase());
        userData.put("created_at", System.currentTimeMillis());

        // Save to Supabase users table
        SupabaseConfig.getPostgrest().from("users")
            .insert(userData)
            .onSuccess(result -> {
                // Send email verification
                SupabaseConfig.getAuth().sendEmailVerification()
                    .onSuccess(verifyResult -> {
                        Toast.makeText(this, "Account created. Please verify your email.", Toast.LENGTH_LONG).show();
                    })
                    .onFailure(verifyException -> {
                        Log.w(TAG, "Failed to send verification email", verifyException);
                        Toast.makeText(this, "Account created but failed to send verification email.", Toast.LENGTH_LONG).show();
                    })
                    .onFinally(() -> {
                        // Redirect to login
                        progressBar.setVisibility(ProgressBar.GONE);
                        btnSignup.setEnabled(true);
                        Intent i = new Intent(this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    });
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to save user data", exception);
                Toast.makeText(this, "Failed to save user data: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                resetSignupState();
            });
    }

    private void resetSignupState() {
        progressBar.setVisibility(ProgressBar.GONE);
        btnSignup.setEnabled(true);
    }
}
