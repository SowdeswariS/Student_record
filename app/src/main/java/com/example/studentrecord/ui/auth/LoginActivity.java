package com.example.studentrecord.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import com.example.studentrecord.R;
import com.example.studentrecord.ui.admin.AdminDashboardActivity;
import com.example.studentrecord.ui.staff.StaffDashboardActivity;
import com.example.studentrecord.ui.student.StudentDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup, tvForgotPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        // Firebase
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

    private void login(String email, String pass){
        // Show progress bar and disable button
        progressBar.setVisibility(ProgressBar.VISIBLE);
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    // Hide progress bar and re-enable button
                    progressBar.setVisibility(ProgressBar.GONE);
                    btnLogin.setEnabled(true);
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            // Check if email is verified
                            if(!user.isEmailVerified()){
                                Toast.makeText(this, "Please verify your email before logging in. Check your inbox for the verification link.", Toast.LENGTH_LONG).show();
                                mAuth.signOut(); // Sign out the user if email not verified
                                return;
                            }
                            fetchRoleAndRedirect(user.getUid());
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.error_auth_failed) + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchRoleAndRedirect(String uid){
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        String role = documentSnapshot.getString("role");
                        if(role == null){
                            Toast.makeText(this, getString(R.string.error_no_role), Toast.LENGTH_LONG).show();
                            return;
                        }
                        redirectBasedOnRole(role);
                    } else {
                        // Create default profile
                        createDefaultUserProfile(uid);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to read role", e);
                    Toast.makeText(this, getString(R.string.error_fetch_role_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void createDefaultUserProfile(String uid) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("role", "student");
            userData.put("email", user.getEmail());
            db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "User profile created. Logging in as student.", Toast.LENGTH_SHORT).show();
                        redirectBasedOnRole("student");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to create user profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void redirectBasedOnRole(String role) {
        switch(role){
            case "admin":
                startActivity(new Intent(this, AdminDashboardActivity.class));
                break;
            case "staff":
                startActivity(new Intent(this, StaffDashboardActivity.class));
                break;
            case "student":
                startActivity(new Intent(this, StudentDashboardActivity.class));
                break;
            default:
                Toast.makeText(this, String.format(getString(R.string.error_unknown_role), role), Toast.LENGTH_LONG).show();
                return;
        }
        finish();
    }

    private void sendPasswordResetEmail(String email) {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvForgotPassword.setEnabled(false);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    tvForgotPassword.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Failed to send password reset email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
