package com.example.studentrecord.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.studentrecord.R;
import com.example.studentrecord.ui.dashboard.AdminDashboardActivity;
import com.example.studentrecord.ui.dashboard.StaffDashboardActivity;
import com.example.studentrecord.ui.dashboard.StudentDashboardActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivityVerbose";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        final String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
        final String password = etPassword.getText() == null ? "" : etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "signIn success user=" + (user != null ? user.getUid() : "null"));
                        Toast.makeText(this, "Auth success, uid: " + (user != null ? user.getUid() : "null"), Toast.LENGTH_LONG).show();
                        if (user != null) fetchRoleAndRedirect(user.getUid());
                        else resetLoginState();
                    } else {
                        Exception e = task.getException();
                        String shortMsg = (e == null) ? "Auth failed (unknown)" : e.getClass().getSimpleName() + ": " + e.getMessage();
                        Log.e(TAG, "signIn failed", e);
                        Toast.makeText(this, "Sign-in error: " + shortMsg, Toast.LENGTH_LONG).show();
                        resetLoginState();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override public void onFailure(@NonNull Exception e) {
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
    }
}


