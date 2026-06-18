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

        rvOrders = findViewById(R.id.rvOrders);
        btnActiveOrders = findViewById(R.id.btnActiveOrders);
        btnHistoryOrders = findViewById(R.id.btnHistoryOrders);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, new ArrayList<>());
        rvOrders.setAdapter(adapter);

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

        setupBottomNavigation();
        fetchOrders();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MapExplorerActivity.class));
            finish();
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
        });
        // زر الطلبات مفعل حالياً
    }

    private void updateTabUI() {
        if (showingActive) {
            btnActiveOrders.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0069B4")));
            btnActiveOrders.setTextColor(Color.WHITE);
            btnHistoryOrders.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnHistoryOrders.setTextColor(Color.GRAY);
        } else {
            btnHistoryOrders.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0069B4")));
            btnHistoryOrders.setTextColor(Color.WHITE);
            btnActiveOrders.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnActiveOrders.setTextColor(Color.GRAY);
        }
    }

    private void fetchOrders() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        // ملاحظة: تأكد من تخزين user_id عند تسجيل الدخول، أو استخدم anon key للتجربة
        String userId = prefs.getString("user_id", "default_user"); 

        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);
        // جلب كافة الطلبات لهذا المستخدم
        api.getOrders("eq." + userId, "*", "created_at.desc").enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allOrders = response.body();
                    filterOrders();
                } else {
                    Toast.makeText(My_Orders_Activity.this, "لا يوجد طلبات حالياً", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                Toast.makeText(My_Orders_Activity.this, "خطأ في الاتصال بالسيرفر", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOrders() {
        List<OrderModel> filtered = new ArrayList<>();
        for (OrderModel order : allOrders) {
            String status = order.getStatus();
            boolean isActive = status != null && (status.equals("pending") || status.equals("accepted") || status.equals("on_way"));
            
            if (showingActive && isActive) filtered.add(order);
            else if (!showingActive && !isActive) filtered.add(order);
        }
        adapter.updateList(filtered);
    }
}
