package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrdersManagementActivity extends AppCompatActivity {

    private RecyclerView rvProviderOrders;
    private ProviderOrderAdapter adapter;
    private List<OrderModel> orderList = new ArrayList<>();
    private String userId;
    
    private TextView tvTabNew, tvTabInProgress, tvTabCompleted;
    private View indicatorNew, indicatorInProgress, indicatorCompleted;
    private String currentTab = "new";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_management);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }

        initViews();
        setupBottomNavigation();
        loadOrders("pending"); // Default tab is "new" (pending)
    }

    private void initViews() {
        rvProviderOrders = findViewById(R.id.rvProviderOrders);
        rvProviderOrders.setLayoutManager(new LinearLayoutManager(this));
        
        tvTabNew = findViewById(R.id.tvTabNew);
        tvTabInProgress = findViewById(R.id.tvTabInProgress);
        tvTabCompleted = findViewById(R.id.tvTabCompleted);
        
        indicatorNew = findViewById(R.id.indicatorNew);
        indicatorInProgress = findViewById(R.id.indicatorInProgress);
        indicatorCompleted = findViewById(R.id.indicatorCompleted);

        findViewById(R.id.tabNewOrders).setOnClickListener(v -> switchTab("new"));
        findViewById(R.id.tabInProgressOrders).setOnClickListener(v -> switchTab("in_progress"));
        findViewById(R.id.tabCompletedOrders).setOnClickListener(v -> switchTab("completed"));
    }

    private void switchTab(String tab) {
        currentTab = tab;
        resetTabsUI();
        
        if ("new".equals(tab)) {
            tvTabNew.setTextColor(Color.parseColor("#0069B4"));
            indicatorNew.setBackgroundColor(Color.parseColor("#0069B4"));
            indicatorNew.getLayoutParams().height = (int) (3 * getResources().getDisplayMetrics().density);
            loadOrders("pending");
        } else if ("in_progress".equals(tab)) {
            tvTabInProgress.setTextColor(Color.parseColor("#0069B4"));
            indicatorInProgress.setBackgroundColor(Color.parseColor("#0069B4"));
            indicatorInProgress.getLayoutParams().height = (int) (3 * getResources().getDisplayMetrics().density);
            loadOrders("accepted,on_way");
        } else {
            tvTabCompleted.setTextColor(Color.parseColor("#0069B4"));
            indicatorCompleted.setBackgroundColor(Color.parseColor("#0069B4"));
            indicatorCompleted.getLayoutParams().height = (int) (3 * getResources().getDisplayMetrics().density);
            loadOrders("delivered,cancelled");
        }
        indicatorNew.requestLayout();
        indicatorInProgress.requestLayout();
        indicatorCompleted.requestLayout();
    }

    private void resetTabsUI() {
        tvTabNew.setTextColor(Color.parseColor("#64748B"));
        tvTabInProgress.setTextColor(Color.parseColor("#64748B"));
        tvTabCompleted.setTextColor(Color.parseColor("#64748B"));
        
        indicatorNew.setBackgroundColor(Color.parseColor("#E2E8F0"));
        indicatorInProgress.setBackgroundColor(Color.parseColor("#E2E8F0"));
        indicatorCompleted.setBackgroundColor(Color.parseColor("#E2E8F0"));
        
        indicatorNew.getLayoutParams().height = (int) (1 * getResources().getDisplayMetrics().density);
        indicatorInProgress.getLayoutParams().height = (int) (1 * getResources().getDisplayMetrics().density);
        indicatorCompleted.getLayoutParams().height = (int) (1 * getResources().getDisplayMetrics().density);
    }

    private void loadOrders(String statusFilter) {
        if (userId == null) return;

        List<String> statuses = Arrays.asList(statusFilter.split(","));

        db.collection("orders")
                .whereEqualTo("provider_id", userId)
                .whereIn("status", statuses)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        orderList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderModel order = document.toObject(OrderModel.class);
                            order.setId(document.getId());
                            orderList.add(order);
                        }
                        adapter = new ProviderOrderAdapter(OrdersManagementActivity.this, orderList, currentTab);
                        rvProviderOrders.setAdapter(adapter);
                    } else {
                        Log.e("OrdersMgmt", "Error getting orders", task.getException());
                        Toast.makeText(this, "فشل جلب الطلبات", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navProviderDashboard).setOnClickListener(v -> {
            startActivity(new Intent(this, ProviderDashboardActivity.class));
            finish();
        });
        
        findViewById(R.id.navProviderServices).setOnClickListener(v -> {
            startActivity(new Intent(this, ProviderServicesActivity.class));
            finish();
        });

        findViewById(R.id.navProviderHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, TripsHistoryActivity.class));
            finish();
        });
    }
}
