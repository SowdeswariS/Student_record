package com.example.studentrecord.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private FirebaseAuth auth;
    private TextView signupRedirectText;
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
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
        progressBar = findViewById(R.id.progressBar);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if(email.isEmpty() || pass.isEmpty()){
                Toast.makeText(this, R.string.error_empty_credentials, Toast.LENGTH_SHORT).show();
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
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    if(doc.exists()){
                        String role = doc.getString("role");
                        if(role == null){
                            Toast.makeText(this, R.string.error_no_role, Toast.LENGTH_LONG).show();
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
                                Toast.makeText(this, getString(R.string.error_unknown_role, role), Toast.LENGTH_LONG).show();
                                return;
                        }
                        finish();
                    } else {
                        Toast.makeText(this, R.string.error_user_profile_not_found, Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener((OnFailureListener) e -> {
                    Log.e(TAG, "Failed to read role", e);
                    Toast.makeText(this, getString(R.string.error_fetch_role_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
