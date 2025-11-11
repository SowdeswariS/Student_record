package com.example.studentrecord.model;

import java.util.Map;

public class Submission {
    private String id;
    private String studentId;
    private String title;
    private String description;
    private String fileUrl;
    private String fileName;
    private String status; // pending, approved, rejected
    private String feedback;
    private String reviewedBy;
    private long createdAt;
    private long updatedAt;

    public Submission() {
        // Default constructor for Supabase deserialization
    }

    public Submission(String id, String studentId, String title, String description, String fileUrl, String fileName) {
        this.id = id;
        this.studentId = studentId;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.status = "pending";
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
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
        Map<String, Object> submissionMap = new java.util.HashMap<>();
        submissionMap.put("id", id);
        submissionMap.put("student_id", studentId);
        submissionMap.put("title", title);
        submissionMap.put("description", description);
        submissionMap.put("file_url", fileUrl);
        submissionMap.put("file_name", fileName);
        submissionMap.put("status", status);
        submissionMap.put("feedback", feedback);
        submissionMap.put("reviewed_by", reviewedBy);
        submissionMap.put("created_at", createdAt);
        submissionMap.put("updated_at", updatedAt);
        return submissionMap;
    }

    // Helper method to create Submission from Map (for Supabase results)
    public static Submission fromMap(Map<String, Object> map) {
        Submission submission = new Submission();
        if (map != null) {
            submission.setId(map.containsKey("id") ? (String) map.get("id") : null);
            submission.setStudentId(map.containsKey("student_id") ? (String) map.get("student_id") : null);
            submission.setTitle(map.containsKey("title") ? (String) map.get("title") : null);
            submission.setDescription(map.containsKey("description") ? (String) map.get("description") : null);
            submission.setFileUrl(map.containsKey("file_url") ? (String) map.get("file_url") : null);
            submission.setFileName(map.containsKey("file_name") ? (String) map.get("file_name") : null);
            submission.setStatus(map.containsKey("status") ? (String) map.get("status") : "pending");
            submission.setFeedback(map.containsKey("feedback") ? (String) map.get("feedback") : null);
            submission.setReviewedBy(map.containsKey("reviewed_by") ? (String) map.get("reviewed_by") : null);
            
            // Handle timestamps
            if (map.containsKey("created_at")) {
                Object createdAtObj = map.get("created_at");
                if (createdAtObj instanceof Long) {
                    submission.setCreatedAt((Long) createdAtObj);
                } else if (createdAtObj instanceof String) {
                    try {
                        submission.setCreatedAt(Long.parseLong((String) createdAtObj));
                    } catch (NumberFormatException e) {
                        submission.setCreatedAt(System.currentTimeMillis());
                    }
                }
            }
            
            if (map.containsKey("updated_at")) {
                Object updatedAtObj = map.get("updated_at");
                if (updatedAtObj instanceof Long) {
                    submission.setUpdatedAt((Long) updatedAtObj);
                } else if (updatedAtObj instanceof String) {
                    try {
                        submission.setUpdatedAt(Long.parseLong((String) updatedAtObj));
                    } catch (NumberFormatException e) {
                        submission.setUpdatedAt(System.currentTimeMillis());
                    }
                }
            }
        }
        return submission;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id='" + id + '\'' +
                ", studentId='" + studentId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", fileName='" + fileName + '\'' +
                ", status='" + status + '\'' +
                ", feedback='" + feedback + '\'' +
                ", reviewedBy='" + reviewedBy + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
