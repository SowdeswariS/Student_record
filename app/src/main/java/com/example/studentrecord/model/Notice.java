package com.example.studentrecord.model;

import java.util.Map;

public class Notice {
    private String id;
    private String title;
    private String content;
    private String authorId;
    private String authorName;
    private long createdAt;
    private long updatedAt;

    public Notice() {
        // Default constructor for Supabase deserialization
    }

    public Notice(String id, String title, String content, String authorId, String authorName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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
        Map<String, Object> noticeMap = new java.util.HashMap<>();
        noticeMap.put("id", id);
        noticeMap.put("title", title);
        noticeMap.put("content", content);
        noticeMap.put("author_id", authorId);
        noticeMap.put("author_name", authorName);
        noticeMap.put("created_at", createdAt);
        noticeMap.put("updated_at", updatedAt);
        return noticeMap;
    }

    // Helper method to create Notice from Map (for Supabase results)
    public static Notice fromMap(Map<String, Object> map) {
        Notice notice = new Notice();
        if (map != null) {
            notice.setId(map.containsKey("id") ? (String) map.get("id") : null);
            notice.setTitle(map.containsKey("title") ? (String) map.get("title") : null);
            notice.setContent(map.containsKey("content") ? (String) map.get("content") : null);
            notice.setAuthorId(map.containsKey("author_id") ? (String) map.get("author_id") : null);
            notice.setAuthorName(map.containsKey("author_name") ? (String) map.get("author_name") : null);
            
            // Handle timestamps
            if (map.containsKey("created_at")) {
                Object createdAtObj = map.get("created_at");
                if (createdAtObj instanceof Long) {
                    notice.setCreatedAt((Long) createdAtObj);
                } else if (createdAtObj instanceof String) {
                    try {
                        notice.setCreatedAt(Long.parseLong((String) createdAtObj));
                    } catch (NumberFormatException e) {
                        notice.setCreatedAt(System.currentTimeMillis());
                    }
                }
            }
            
            if (map.containsKey("updated_at")) {
                Object updatedAtObj = map.get("updated_at");
                if (updatedAtObj instanceof Long) {
                    notice.setUpdatedAt((Long) updatedAtObj);
                } else if (updatedAtObj instanceof String) {
                    try {
                        notice.setUpdatedAt(Long.parseLong((String) updatedAtObj));
                    } catch (NumberFormatException e) {
                        notice.setUpdatedAt(System.currentTimeMillis());
                    }
                }
            }
        }
        return notice;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorName='" + authorName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
