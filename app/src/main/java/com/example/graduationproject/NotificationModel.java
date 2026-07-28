package com.example.graduationproject;

import com.google.firebase.firestore.Exclude;
import java.util.Date;

public class NotificationModel {
    @Exclude
    private String id;
    private String userId;
    private String title;
    private String message;
    private String type; // 'new_order', 'rating', 'payment', etc.
    private boolean read;
    private Date created_at;

    public NotificationModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Date getCreated_at() { return created_at; }
    public void setCreated_at(Date created_at) { this.created_at = created_at; }
}
