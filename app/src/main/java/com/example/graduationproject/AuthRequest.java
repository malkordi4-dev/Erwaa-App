package com.example.graduationproject;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class AuthRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("data")
    private Map<String, Object> data;

    // Constructor for Register (3 arguments)
    public AuthRequest(String email, String password, Map<String, Object> data) {
        this.email = email;
        this.password = password;
        this.data = data;
    }

    // Constructor for Login (2 arguments)
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.data = null;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
