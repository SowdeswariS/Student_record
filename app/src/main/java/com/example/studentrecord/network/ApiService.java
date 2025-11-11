package com.example.studentrecord.network;

import android.util.Log;

import com.example.studentrecord.model.User;
import com.example.studentrecord.model.Notice;
import com.example.studentrecord.model.Submission;

import io.github.jan.supabase.postgrest.from;
import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.storage.Storage;
import io.github.jan.supabase.storage.upload;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiService {
    private static final String TAG = "ApiService";
    private final ApiClient apiClient;

    public ApiService() {
        this.apiClient = ApiClient.getInstance();
    }

    // User operations
    public void createUser(User user, ApiCallback<Void> callback) {
        Map<String, Object> userData = user.toMap();
        
        apiClient.getDatabase().from(ApiClient.TABLE_USERS)
            .insert(userData)
            .onSuccess(result -> {
                Log.d(TAG, "User created successfully");
                callback.onSuccess(null);
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to create user", exception);
                callback.onError(exception.getMessage());
            });
    }

    public void getUser(String userId, ApiCallback<User> callback) {
        apiClient.getDatabase().from(ApiClient.TABLE_USERS)
            .select("*")
            .eq("id", userId)
            .single()
            .onSuccess(result -> {
                try {
                    Map<String, Object> userData = result.getData();
                    User user = User.fromMap(userData);
                    callback.onSuccess(user);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing user data", e);
                    callback.onError("Error parsing user data");
                }
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to get user", exception);
                callback.onError(exception.getMessage());
            });
    }

    public void updateUser(String userId, Map<String, Object> updates, ApiCallback<Void> callback) {
        updates.put("updated_at", System.currentTimeMillis());
        
        apiClient.getDatabase().from(ApiClient.TABLE_USERS)
            .update(updates)
            .eq("id", userId)
            .onSuccess(result -> {
                Log.d(TAG, "User updated successfully");
                callback.onSuccess(null);
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to update user", exception);
                callback.onError(exception.getMessage());
            });
    }

    public void getAllUsers(ApiCallback<List<User>> callback) {
        apiClient.getDatabase().from(ApiClient.TABLE_USERS)
            .select("*")
            .onSuccess(result -> {
                try {
                    List<User> users = new ArrayList<>();
                    JSONArray dataArray = new JSONArray(result.getData().toString());
                    
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userObj = dataArray.getJSONObject(i);
                        Map<String, Object> userMap = new HashMap<>();
                        
                        for (String key : userObj.keySet()) {
                            userMap.put(key, userObj.get(key));
                        }
                        
                        users.add(User.fromMap(userMap));
                    }
                    
                    callback.onSuccess(users);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing users data", e);
                    callback.onError("Error parsing users data");
                }
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to get users", exception);
                callback.onError(exception.getMessage());
            });
    }

    // Notice operations
    public void createNotice(Notice notice, ApiCallback<Void> callback) {
        Map<String, Object> noticeData = notice.toMap();
        
        apiClient.getDatabase().from(ApiClient.TABLE_NOTICES)
            .insert(noticeData)
            .onSuccess(result -> {
                Log.d(TAG, "Notice created successfully");
                callback.onSuccess(null);
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to create notice", exception);
                callback.onError(exception.getMessage());
            });
    }

    public void getAllNotices(ApiCallback<List<Notice>> callback) {
        apiClient.getDatabase().from(ApiClient.TABLE_NOTICES)
            .select("*")
            .order("created_at", descending = true)
            .onSuccess(result -> {
                try {
                    List<Notice> notices = new ArrayList<>();
                    JSONArray dataArray = new JSONArray(result.getData().toString());
                    
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject noticeObj = dataArray.getJSONObject(i);
                        Map<String, Object> noticeMap = new HashMap<>();
                        
                        for (String key : noticeObj.keySet()) {
                            noticeMap.put(key, noticeObj.get(key));
                        }
                        
                        notices.add(Notice.fromMap(noticeMap));
                    }
                    
                    callback.onSuccess(notices);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing notices data", e);
                    callback.onError("Error parsing notices data");
                }
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to get notices", exception);
                callback.onError(exception.getMessage());
            });
    }

    // Submission operations
    public void createSubmission(Submission submission, ApiCallback<Void> callback) {
        Map<String, Object> submissionData = submission.toMap();
        
        apiClient.getDatabase().from(ApiClient.TABLE_SUBMISSIONS)
            .insert(submissionData)
            .onSuccess(result -> {
                Log.d(TAG, "Submission created successfully");
                callback.onSuccess(null);
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to create submission", exception);
                callback.onError(exception.getMessage());
            });
    }

    public void getStudentSubmissions(String studentId, ApiCallback<List<Submission>> callback) {
        apiClient.getDatabase().from(ApiClient.TABLE_SUBMISSIONS)
            .select("*")
            .eq("student_id", studentId)
            .order("created_at", descending = true)
            .onSuccess(result -> {
                try {
                    List<Submission> submissions = new ArrayList<>();
                    JSONArray dataArray = new JSONArray(result.getData().toString());
                    
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject submissionObj = dataArray.getJSONObject(i);
                        Map<String, Object> submissionMap = new HashMap<>();
                        
                        for (String key : submissionObj.keySet()) {
                            submissionMap.put(key, submissionObj.get(key));
                        }
                        
                        submissions.add(Submission.fromMap(submissionMap));
                    }
                    
                    callback.onSuccess(submissions);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing submissions data", e);
                    callback.onError("Error parsing submissions data");
                }
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to get submissions", exception);
                callback.onError(exception.getMessage());
            });
    }

    public void updateSubmission(String submissionId, Map<String, Object> updates, ApiCallback<Void> callback) {
        updates.put("updated_at", System.currentTimeMillis());
        
        apiClient.getDatabase().from(ApiClient.TABLE_SUBMISSIONS)
            .update(updates)
            .eq("id", submissionId)
            .onSuccess(result -> {
                Log.d(TAG, "Submission updated successfully");
                callback.onSuccess(null);
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to update submission", exception);
                callback.onError(exception.getMessage());
            });
    }

    // Storage operations
    public void uploadFile(String bucket, String path, byte[] data, String contentType, ApiCallback<String> callback) {
        apiClient.getStorage().from(bucket)
            .upload(path, data, contentType)
            .onSuccess(result -> {
                String publicUrl = apiClient.getStorage().from(bucket).getPublicUrl(path);
                Log.d(TAG, "File uploaded successfully: " + publicUrl);
                callback.onSuccess(publicUrl);
            })
            .onFailure(exception -> {
                Log.e(TAG, "Failed to upload file", exception);
                callback.onError(exception.getMessage());
            });
    }

    // Callback interface
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}
