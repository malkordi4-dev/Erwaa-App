package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ProviderDashboardActivity extends AppCompatActivity {

    private SwitchCompat switchWorkStatus;
    private TextView tvActiveOrdersCount, tvTodayEarnings, tvAverageRating, tvWorkStatus;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_dashboard);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getCurrentUser().getUid();
        }

        initViews();
        setupBottomNavigation();
        loadDashboardStats();
    }

    private void initViews() {
        switchWorkStatus = findViewById(R.id.switchWorkStatus);
        tvActiveOrdersCount = findViewById(R.id.tvActiveOrdersCount);
        tvTodayEarnings = findViewById(R.id.tvTodayEarnings);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvWorkStatus = findViewById(R.id.tvWorkStatus);

        // جلب الحالة الحالية من Firestore
        if (userId != null) {
            db.collection("providers").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String status = documentSnapshot.getString("status");
                    boolean isActive = "active".equals(status);
                    switchWorkStatus.setChecked(isActive);
                    updateStatusUI(isActive);
                }
            });
        }

        switchWorkStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateStatusUI(isChecked);
            updateStatusInFirestore(isChecked);
        });

        findViewById(R.id.btnCustomOffer).setOnClickListener(v -> {
            Toast.makeText(this, "إضافة عرض خاص قيد التطوير", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateStatusUI(boolean isActive) {
        if (isActive) {
            tvWorkStatus.setText("متاح للعمل");
            tvWorkStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvWorkStatus.setText("غير متاح حالياً");
            tvWorkStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void updateStatusInFirestore(boolean isActive) {
        if (userId == null) return;
        String status = isActive ? "active" : "offline";
        db.collection("providers").document(userId).update("status", status)
                .addOnFailureListener(e -> Toast.makeText(this, "فشل تحديث الحالة", Toast.LENGTH_SHORT).show());
    }

    private void loadDashboardStats() {
        if (userId == null) return;

        // جلب عدد الطلبات النشطة (التي ليست delivered أو cancelled)
        db.collection("orders")
                .whereEqualTo("provider_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int activeCount = 0;
                    if (queryDocumentSnapshots != null) {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                            String status = doc.getString("status");
                            if (!"delivered".equals(status) && !"cancelled".equals(status)) {
                                activeCount++;
                            }
                        }
                    }
                    tvActiveOrdersCount.setText(String.valueOf(activeCount));
                })
                .addOnFailureListener(e -> Log.e("Dashboard", "Error loading orders", e));
        
        // جلب التقييم
        db.collection("providers").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Double rating = documentSnapshot.getDouble("rating");
                tvAverageRating.setText(String.valueOf(rating != null ? rating : 4.5));
            }
        });
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navProviderDashboard).setOnClickListener(v -> {});
        
        findViewById(R.id.navProviderOrders).setOnClickListener(v -> {
            startActivity(new Intent(this, OrdersManagementActivity.class));
        });

        findViewById(R.id.navProviderServices).setOnClickListener(v -> {
            startActivity(new Intent(this, ProviderServicesActivity.class));
        });

        findViewById(R.id.navProviderHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, TripsHistoryActivity.class));
        });
    }
}
