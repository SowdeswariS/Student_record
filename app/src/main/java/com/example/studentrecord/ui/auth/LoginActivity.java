package com.example.studentrecord.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // DEBUG: temporary sign-in tester - paste inside onCreate(), run once
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String testEmail = "PUT_EMAIL_HERE";   // <-- replace
        String testPass  = "PUT_PASSWORD_HERE"; // <-- replace

        auth.signInWithEmailAndPassword(testEmail, testPass)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser() != null ? authResult.getUser().getUid() : "null";
                    Toast.makeText(this, "Auth success, uid: " + uid, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Sign-in error: " + e.getClass().getSimpleName() + " - " + e.getMessage(), Toast.LENGTH_LONG).show();
                });


        // Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if(email.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }
            login(email, pass);
        });

        tvSignup.setOnClickListener(v -> {
            // Start your SignupActivity (if you have one)
            Intent i = new Intent(this, SignupActivity.class);
            startActivity(i);
        });
    }

    private void login(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            fetchRoleAndRedirect(user.getUid());
                        } else {
                            Toast.makeText(this, "Login failed: no user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Auth failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Auth error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void fetchRoleAndRedirect(String uid){
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    if(doc.exists()){
                        String role = doc.getString("role");
                        if(role == null){
                            Toast.makeText(this, "No role set for user", Toast.LENGTH_LONG).show();
                            return;
                        }
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
                                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_LONG).show();
                                return;
                        }
                        finish();
                    } else {
                        Toast.makeText(this, "User profile not found. Create profile in Firestore users/{uid}", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener((OnFailureListener) e -> {
                    Log.e(TAG, "Failed to read role", e);
                    Toast.makeText(this, "Failed to fetch role: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
