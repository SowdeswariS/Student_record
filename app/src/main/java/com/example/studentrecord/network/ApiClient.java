package com.example.studentrecord.network;

import com.example.studentrecord.util.SupabaseConfig;

import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.storage.Storage;

public class ApiClient {
    private static ApiClient instance;
    private final Postgrest postgrest;
    private final Storage storage;

    private ApiClient() {
        this.postgrest = SupabaseConfig.getPostgrest();
        this.storage = SupabaseConfig.getStorage();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public Postgrest getDatabase() {
        return postgrest;
    }

    public Storage getStorage() {
        return storage;
    }

    // Database tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_NOTICES = "notices";
    public static final String TABLE_SUBMISSIONS = "submissions";

    // Storage buckets
    public static final String BUCKET_SUBMISSIONS = "submissions";
    public static final String BUCKET_PROFILES = "profiles";
}
