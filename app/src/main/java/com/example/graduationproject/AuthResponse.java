package com.example.graduationproject;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    private String accessToken;
    
    @SerializedName("user")
    private User user;

    public String getAccessToken() {
        return accessToken;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        private String id;
        private String email;

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }
}
