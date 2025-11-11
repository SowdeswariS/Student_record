package com.example.studentrecord.util;

import android.content.Context;
import android.util.Log;

import com.example.studentrecord.model.User;
import com.example.studentrecord.model.Notice;
import com.example.studentrecord.model.Submission;

import io.github.jan.supabase.postgrest.from;
import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.storage.Storage;
import io.github.jan.supabase.storage.upload;
import io.github.jan.supabase.auth.Auth;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import kotlinx.coroutines.flow.Flow;

public class SupabaseHelper {
    private static final String TAG = "SupabaseHelper";
    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    // Auth helpers
    public static Auth getAuth() {
        return SupabaseConfig.getAuth();
    }

    public static boolean isUserLoggedIn() {
        return SupabaseConfig.getAuth().getCurrentUser() != null;
    }

    public static String getCurrentUserId() {
        return SupabaseConfig.getAuth().getCurrentUser() != null ? 
               SupabaseConfig.getAuth().getCurrentUser().getId() : null;
    }

    public static void logout() {
        SupabaseConfig.getAuth().signOut();
    }

    // Database helpers
    public static Postgrest getDatabase() {
        return SupabaseConfig.getPostgrest();
    }

    // User operations
    public static void createUser(User user) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());
        userData.put("name", user.getName());
        userData.put("created_at", System.currentTimeMillis());

        SupabaseConfig.getPostgrest().from("users")
            .insert(userData)
            .onSuccess(result -> {
                Log.d(TAG, "User created successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to create user", exception);
            });
    }

    public static void getUser(String userId) {
        SupabaseConfig.getPostgrest().from("users")
            .select("*")
            .eq("id", userId)
            .single()
            .onSuccess(result -> {
                Log.d(TAG, "User retrieved successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to get user", exception);
            });
    }

    public static void updateUser(String userId, Map<String, Object> updates) {
        updates.put("updated_at", System.currentTimeMillis());
        
        SupabaseConfig.getPostgrest().from("users")
            .update(updates)
            .eq("id", userId)
            .onSuccess(result -> {
                Log.d(TAG, "User updated successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to update user", exception);
            });
    }

    public static void getAllUsers() {
        SupabaseConfig.getPostgrest().from("users")
            .select("*")
            .onSuccess(result -> {
                Log.d(TAG, "Users retrieved successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to get users", exception);
            });
    }

    // Notice operations
    public static void createNotice(Notice notice) {
        Map<String, Object> noticeData = new HashMap<>();
        noticeData.put("id", notice.getId());
        noticeData.put("title", notice.getTitle());
        noticeData.put("content", notice.getContent());
        noticeData.put("author_id", notice.getAuthorId());
        noticeData.put("created_at", System.currentTimeMillis());

        SupabaseConfig.getPostgrest().from("notices")
            .insert(noticeData)
            .onSuccess(result -> {
                Log.d(TAG, "Notice created successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to create notice", exception);
            });
    }

    public static void getAllNotices() {
        SupabaseConfig.getPostgrest().from("notices")
            .select("*")
            .order("created_at", true)
            .onSuccess(result -> {
                Log.d(TAG, "Notices retrieved successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to get notices", exception);
            });
    }

    // Submission operations
    public static void createSubmission(Submission submission) {
        Map<String, Object> submissionData = new HashMap<>();
        submissionData.put("id", submission.getId());
        submissionData.put("student_id", submission.getStudentId());
        submissionData.put("title", submission.getTitle());
        submissionData.put("description", submission.getDescription());
        submissionData.put("file_url", submission.getFileUrl());
        submissionData.put("status", submission.getStatus());
        submissionData.put("created_at", System.currentTimeMillis());

        SupabaseConfig.getPostgrest().from("submissions")
            .insert(submissionData)
            .onSuccess(result -> {
                Log.d(TAG, "Submission created successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to create submission", exception);
            });
    }

    public static void getStudentSubmissions(String studentId) {
        SupabaseConfig.getPostgrest().from("submissions")
            .select("*")
            .eq("student_id", studentId)
            .order("created_at", true)
            .onSuccess(result -> {
                Log.d(TAG, "Student submissions retrieved successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to get student submissions", exception);
            });
    }

    // Storage helpers
    public static Storage getStorage() {
        return SupabaseConfig.getStorage();
    }

    public static void uploadFile(String bucket, String path, byte[] data, String contentType) {
        SupabaseConfig.getStorage().from(bucket)
            .upload(path, data, contentType)
            .onSuccess(result -> {
                Log.d(TAG, "File uploaded successfully");
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to upload file", exception);
            });
    }

    public static String getPublicUrl(String bucket, String path) {
        return SupabaseConfig.getStorage().from(bucket).getPublicUrl(path);
    }

    // Real-time helpers
    public static void subscribeToTable(String tableName) {
        SupabaseConfig.getRealtime().channel(tableName)
            .onPostgresChange("INSERT", tableName, payload -> {
                Log.d(TAG, "New record in " + tableName + ": " + payload);
            })
            .onPostgresChange("UPDATE", tableName, payload -> {
                Log.d(TAG, "Updated record in " + tableName + ": " + payload);
            })
            .onPostgresChange("DELETE", tableName, payload -> {
                Log.d(TAG, "Deleted record in " + tableName + ": " + payload);
            })
            .subscribe();
    }
}
