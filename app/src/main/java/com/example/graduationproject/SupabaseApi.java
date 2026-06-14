package com.example.graduationproject;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface SupabaseApi {

    // 1. Sign Up
    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(@Body AuthRequest request);

    // 2. Sign In
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> signIn(@Body AuthRequest request);

    // 3. Verify OTP
    @POST("auth/v1/verify")
    Call<AuthResponse> verifyOtp(@Body Map<String, Object> body);

    // 4. Send Password Reset Email
    @POST("auth/v1/recover")
    Call<Void> sendRecoveryEmail(@Body Map<String, String> body);

    // 5. Update User
    @PUT("auth/v1/user")
    Call<AuthResponse> updateUser(@Header("Authorization") String token, @Body Map<String, Object> body);

    // 6. Insert Profile
    @POST("rest/v1/profiles")
    Call<Void> createProfile(@Body Map<String, Object> profile);

    // 7. Orders
    @POST("rest/v1/orders")
    Call<Void> createOrder(@Body OrderModel order);

    @GET("rest/v1/orders")
    Call<List<OrderModel>> getOrders(@Query("customer_id") String customerId, @Query("select") String select);
    
    // 8. Services
    @GET("rest/v1/services")
    Call<List<ServiceModel>> getServices(@Query("select") String select);

    // 9. Ratings
    @POST("rest/v1/ratings")
    Call<Void> submitRating(@Body RatingModel rating);
}
