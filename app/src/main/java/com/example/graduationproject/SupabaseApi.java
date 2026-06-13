package com.example.graduationproject;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface SupabaseApi {

    // 1. Sign Up
    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(@Body AuthRequest request);

    // 2. Sign In
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> signIn(@Body AuthRequest request);

    // 3. Verify OTP (for signup or recovery)
    @POST("auth/v1/verify")
    Call<AuthResponse> verifyOtp(@Body Map<String, Object> body);

    // 4. Send Password Reset Email
    @POST("auth/v1/recover")
    Call<Void> sendRecoveryEmail(@Body Map<String, String> body);

    // 5. Update User (Change Password)
    @PUT("auth/v1/user")
    Call<AuthResponse> updateUser(@Header("Authorization") String token, @Body Map<String, Object> body);

    // 6. Insert Profile (PostgREST)
    @POST("rest/v1/profiles")
    Call<Void> createProfile(@Body Map<String, Object> profile);
}
