package com.example.studentrecord.model;

import java.util.Map;

public class User {
    private String id;
    private String email;
    private String name;
    private String role;
    private long createdAt;
    private long updatedAt;

    public User() {
        // Default constructor for Firebase/Supabase deserialization
    }

    public User(String id, String email, String name, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper method to convert to Map for Supabase
    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new java.util.HashMap<>();
        userMap.put("id", id);
        userMap.put("email", email);
        userMap.put("name", name);
        userMap.put("role", role);
        userMap.put("created_at", createdAt);
        userMap.put("updated_at", updatedAt);
        return userMap;
    }

    // Helper method to create User from Map (for Supabase results)
    public static User fromMap(Map<String, Object> map) {
        User user = new User();
        if (map != null) {
            user.setId(map.containsKey("id") ? (String) map.get("id") : null);
            user.setEmail(map.containsKey("email") ? (String) map.get("email") : null);
            user.setName(map.containsKey("name") ? (String) map.get("name") : null);
            user.setRole(map.containsKey("role") ? (String) map.get("role") : null);
            
            // Handle timestamps (might be Long or String)
            if (map.containsKey("created_at")) {
                Object createdAtObj = map.get("created_at");
                if (createdAtObj instanceof Long) {
                    user.setCreatedAt((Long) createdAtObj);
                } else if (createdAtObj instanceof String) {
                    try {
                        user.setCreatedAt(Long.parseLong((String) createdAtObj));
                    } catch (NumberFormatException e) {
                        user.setCreatedAt(System.currentTimeMillis());
                    }
                }
            }
            
            if (map.containsKey("updated_at")) {
                Object updatedAtObj = map.get("updated_at");
                if (updatedAtObj instanceof Long) {
                    user.setUpdatedAt((Long) updatedAtObj);
                } else if (updatedAtObj instanceof String) {
                    try {
                        user.setUpdatedAt(Long.parseLong((String) updatedAtObj));
                    } catch (NumberFormatException e) {
                        user.setUpdatedAt(System.currentTimeMillis());
                    }
                }
            }
        }
        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
