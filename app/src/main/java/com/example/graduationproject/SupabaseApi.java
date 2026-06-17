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

    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(@Body AuthRequest request);

    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> signIn(@Body AuthRequest request);

    @POST("rest/v1/orders")
    Call<Void> createOrder(@Body OrderModel order);

    @GET("rest/v1/orders")
    Call<List<OrderModel>> getOrders(
            @Query("customer_id") String customerIdFilter, 
            @Query("select") String select,
            @Query("order") String order // for sorting like "created_at.desc"
    );

    @GET("rest/v1/services")
    Call<List<ServiceModel>> getServices(@Query("select") String select);
}
