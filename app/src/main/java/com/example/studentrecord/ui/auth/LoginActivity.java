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

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentrecord.R;
import com.example.studentrecord.ui.admin.AdminDashboardActivity;
import com.example.studentrecord.ui.staff.StaffDashboardActivity;
import com.example.studentrecord.ui.student.StudentDashboardActivity;
import com.example.studentrecord.util.SupabaseConfig;

import io.github.jan.supabase.auth.exception.AuthRestException;
import io.github.jan.supabase.auth.user.User;
import io.github.jan.supabase.postgrest.from;
import io.github.jan.supabase.postgrest.result.PostgrestResult;

import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup, tvForgotPassword;
    private ProgressBar progressBar;

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

        // Use Supabase authentication
        SupabaseConfig.getAuth().signInWith(email, pass)
            .onSuccess(result -> {
                User user = SupabaseConfig.getAuth().getCurrentUser();
                if (user != null) {
                    // Check if email is confirmed
                    if (!user.isEmailConfirmed()) {
                        Toast.makeText(this, "Please verify your email before logging in. Check your inbox for the verification link.", Toast.LENGTH_LONG).show();
                        SupabaseConfig.getAuth().signOut();
                        resetLoginState();
                        return;
                    }
                    fetchRoleAndRedirect(user.getId());
                } else {
                    Toast.makeText(this, "Login failed: User not found", Toast.LENGTH_LONG).show();
                    resetLoginState();
                }
            })
            .onFailure(exception -> {
                Log.e(TAG, "Login failed", exception);
                String errorMessage = "Authentication failed";
                if (exception instanceof AuthRestException) {
                    errorMessage = ((AuthRestException) exception).getErrorDescription();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                resetLoginState();
            });
    }

    private void fetchRoleAndRedirect(String uid) {
        SupabaseConfig.getPostgrest().from("users")
            .select("role")
            .eq("id", uid)
            .single()
            .onSuccess(result -> {
                try {
                    String role = result.getData().getString("role");
                    Log.d(TAG, "Supabase role=" + role + " for uid=" + uid);
                    
                    if (role == null || role.trim().isEmpty()) {
                        Toast.makeText(this, "Role not set for this user.", Toast.LENGTH_LONG).show();
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
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing role", e);
                    Toast.makeText(this, "Error parsing user role", Toast.LENGTH_LONG).show();
                    resetLoginState();
                }
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to read user doc", exception);
                Toast.makeText(this, "Failed to read user profile: " + exception.getMessage(), Toast.LENGTH_LONG).show();
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

        SupabaseConfig.getAuth().resetPasswordForEmail(email)
            .onSuccess(result -> {
                Toast.makeText(this, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
            })
            .onFailure(exception -> {
                String errorMessage = "Failed to send password reset email";
                if (exception instanceof AuthRestException) {
                    errorMessage = ((AuthRestException) exception).getErrorDescription();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            })
            .onFinally(() -> {
                progressBar.setVisibility(View.GONE);
                tvForgotPassword.setEnabled(true);
            });
    }
}
