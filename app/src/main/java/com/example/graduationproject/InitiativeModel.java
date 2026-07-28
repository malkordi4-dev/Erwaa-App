package com.example.graduationproject;

public class InitiativeModel {
    private String id;
    private String title;
    private String location;
    private int targetLiters;
    private int currentLiters;
    private String status;

    // 🛑 ضروري جداً لـ Firebase Firestore
    public InitiativeModel() {
    }

    public InitiativeModel(String id, String title, String location, int targetLiters, int currentLiters, String status) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.targetLiters = targetLiters;
        this.currentLiters = currentLiters;
        this.status = status;
    }

    // الـ Getters والـ Setters المعتادة...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getTargetLiters() { return targetLiters; }
    public void setTargetLiters(int targetLiters) { this.targetLiters = targetLiters; }

    public int getCurrentLiters() { return currentLiters; }
    public void setCurrentLiters(int currentLiters) { this.currentLiters = currentLiters; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgressPercentage() {
        if (targetLiters == 0) return 0;
        return (int) (((float) currentLiters / targetLiters) * 100);
    }
}