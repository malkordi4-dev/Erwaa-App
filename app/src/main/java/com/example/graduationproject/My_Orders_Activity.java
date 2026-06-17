package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class My_Orders_Activity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<OrderModel> allOrders = new ArrayList<>();
    private MaterialButton btnActiveOrders, btnHistoryOrders;
    private boolean showingActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // Initialize Views
        rvOrders = findViewById(R.id.rvOrders);
        btnActiveOrders = findViewById(R.id.btnActiveOrders);
        btnHistoryOrders = findViewById(R.id.btnHistoryOrders);

        // Setup RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, new ArrayList<>());
        rvOrders.setAdapter(adapter);

        // Tab Switching Logic
        btnActiveOrders.setOnClickListener(v -> {
            showingActive = true;
            updateTabUI();
            filterOrders();
        });

        btnHistoryOrders.setOnClickListener(v -> {
            showingActive = false;
            updateTabUI();
            filterOrders();
        });

        // Bottom Navigation
        setupBottomNavigation();

        fetchOrders();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapExplorerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        findViewById(R.id.navWallet).setOnClickListener(v -> {
             startActivity(new Intent(this, WalletActivity.class));
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
             startActivity(new Intent(this, HomeActivity.class));
        });
    }

    private void updateTabUI() {
        if (showingActive) {
            btnActiveOrders.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnActiveOrders.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#0069B4")));
            btnActiveOrders.setTextColor(Color.parseColor("#0069B4"));

            btnHistoryOrders.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8FAFC")));
            btnHistoryOrders.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E2E8F0")));
            btnHistoryOrders.setTextColor(Color.parseColor("#94A3B8"));
        } else {
            btnHistoryOrders.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btnHistoryOrders.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#0069B4")));
            btnHistoryOrders.setTextColor(Color.parseColor("#0069B4"));

            btnActiveOrders.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F8FAFC")));
            btnActiveOrders.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E2E8F0")));
            btnActiveOrders.setTextColor(Color.parseColor("#94A3B8"));
        }
    }

    private void fetchOrders() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);
        // Fixed: added the third argument "created_at.desc" to match the SupabaseApi.getOrders definition
        api.getOrders("eq." + userId, "*", "created_at.desc").enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allOrders = response.body();
                    filterOrders();
                } else {
                    Log.e("MyOrders", "Error code: " + response.code());
                    Toast.makeText(My_Orders_Activity.this, "فشل في تحميل الطلبات", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                Log.e("MyOrders", "Failure", t);
                Toast.makeText(My_Orders_Activity.this, "خطأ في الاتصال", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOrders() {
        List<OrderModel> filtered = new ArrayList<>();
        for (OrderModel order : allOrders) {
            String status = order.getStatus();
            boolean isActive = status.equals("pending") || status.equals("accepted") || status.equals("on_way");
            
            if (showingActive && isActive) filtered.add(order);
            else if (!showingActive && !isActive) filtered.add(order);
        }
        adapter.updateList(filtered);
    }
}
