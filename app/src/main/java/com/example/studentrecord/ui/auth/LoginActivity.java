package com.example.studentrecord.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentrecord.R;
import com.example.studentrecord.ui.admin.AdminDashboardActivity;
import com.example.studentrecord.ui.staff.StaffDashboardActivity;
import com.example.studentrecord.ui.student.StudentDashboardActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivityVerbose";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup, tvForgotPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

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

            login(email, pass);
        });

        tvSignup.setOnClickListener(v -> {
            // Start your SignupActivity (if you have one)
            Intent i = new Intent(this, SignupActivity.class);
            startActivity(i);
        });

        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if(email.isEmpty()){
                Toast.makeText(this, "Please enter your email address first", Toast.LENGTH_SHORT).show();
                return;
            }
            sendPasswordResetEmail(email);
        });
    }

    private void login(String email, String pass) {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            // Check if email is verified
                            if(!user.isEmailVerified()){
                                Toast.makeText(this, "Please verify your email before logging in. Check your inbox for the verification link.", Toast.LENGTH_LONG).show();
                                mAuth.signOut(); // Sign out the user if email not verified
                                resetLoginState();
                                return;
                            }
                            fetchRoleAndRedirect(user.getUid());
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        resetLoginState();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "signIn onFailure", e);
                        Toast.makeText(LoginActivity.this, "Sign-in failure: " + e.getClass().getSimpleName() + " - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        resetLoginState();
                    }
                });
    }

    private void fetchRoleAndRedirect(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Log.w(TAG, "No Firestore user doc for uid=" + uid);
                        Toast.makeText(this, "User profile not found in Firestore. Create users/{uid} with field 'role'.", Toast.LENGTH_LONG).show();
                        resetLoginState();
                        return;
                    }
                    String role = doc.getString("role");
                    Log.d(TAG, "Firestore role=" + role + " for uid=" + uid);
                    if (role == null || role.trim().isEmpty()) {
                        Toast.makeText(this, "Role not set for this user in Firestore.", Toast.LENGTH_LONG).show();
                        resetLoginState();
                        return;
                    }
                    role = role.trim().toLowerCase();
                    switch (role) {
                        case "admin":
                            startActivity(new Intent(this, AdminDashboardActivity.class));
                            finish();
                            break;
                        case "staff":
                            startActivity(new Intent(this, StaffDashboardActivity.class));
                            finish();
                            break;
                        case "student":
                            startActivity(new Intent(this, StudentDashboardActivity.class));
                            finish();
                            break;
                        default:
                            Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_LONG).show();
                            resetLoginState();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to read user doc", e);
                    Toast.makeText(this, "Failed to read user profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    resetLoginState();
                });
    }

    private void resetLoginState() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Login");
        progressBar.setVisibility(View.GONE);
    }

    private void sendPasswordResetEmail(String email) {
        progressBar.setVisibility(View.VISIBLE);
        tvForgotPassword.setEnabled(false);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    tvForgotPassword.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Failed to send password reset email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
