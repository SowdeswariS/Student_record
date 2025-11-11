package com.example.studentrecord.util;

import io.github.jan.supabase.SupabaseClient;
import io.github.jan.supabase.createSupabaseClient;
import io.github.jan.supabase.auth.Auth;
import io.github.jan.supabase.auth.auth;
import io.github.jan.supabase.postgrest.Postgrest;
import io.github.jan.supabase.postgrest.postgrest;
import io.github.jan.supabase.storage.Storage;
import io.github.jan.supabase.storage.storage;
import io.github.jan.supabase.realtime.Realtime;
import io.github.jan.supabase.realtime.realtime;

public class SupabaseConfig {
    private static final String SUPABASE_URL = "YOUR_SUPABASE_URL";
    private static final String SUPABASE_ANON_KEY = "YOUR_SUPABASE_ANON_KEY";
    
    private static SupabaseClient supabaseClient;
    
    public static SupabaseClient getClient() {
        if (supabaseClient == null) {
            supabaseClient = createSupabaseClient(
                SUPABASE_URL,
                SUPABASE_ANON_KEY
            ) {
                install(Auth)
                install(Postgrest)
                install(Storage)
                install(Realtime)
            }
        }
        return supabaseClient;
    }
    
    public static Auth getAuth() {
        return getClient().auth;
    }
    
    public static Postgrest getPostgrest() {
        return getClient().postgrest;
    }
    
    public static Storage getStorage() {
        return getClient().storage;
    }
    
    public static Realtime getRealtime() {
        return getClient().realtime;
    }
}
