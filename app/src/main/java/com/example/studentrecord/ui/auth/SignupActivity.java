package com.example.studentrecord.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentrecord.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Spinner spinnerRole;
    private Button btnSignup;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("role", role);
                        user.put("email", email);

                        db.collection("users").document(uid)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Send email verification
                                    mAuth.getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(verifyTask -> {
                                                progressBar.setVisibility(ProgressBar.GONE);
                                                btnSignup.setEnabled(true);
                                                if (verifyTask.isSuccessful()) {
                                                    Toast.makeText(this, "Account created. Please verify your email.", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(this, "Account created but failed to send verification email.", Toast.LENGTH_LONG).show();
                                                }
                                                // Redirect to login
                                                Intent i = new Intent(this, LoginActivity.class);
                                                startActivity(i);
                                                finish();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(ProgressBar.GONE);
                                    btnSignup.setEnabled(true);
                                    Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        progressBar.setVisibility(ProgressBar.GONE);
                        btnSignup.setEnabled(true);
                        Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}



