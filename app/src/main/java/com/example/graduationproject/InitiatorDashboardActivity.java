package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InitiatorDashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardDebug";

    private ImageView btnProfile;
    private TextView btnViewAll;
    private MaterialCardView btnCreateInitiative;
    private LinearLayout btnExploreMap;
    private RecyclerView rvInitiatives;
    private InitiativesAdapter initiativesAdapter;
    private List<InitiativeModel> initiativeList;

    private TextView tvActiveInitiativesCount;
    private TextView tvTotalWaterDistributed;
    private TextView tvTotalFamiliesBenefited;

    private LinearLayout navHome, navMap, navAdd, navProfile;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiator_dashboard);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupBottomNavigation();
        setupRecyclerView();
        fetchInitiativesFromFirebase();
        setupClickListeners();
    }

    private void initViews() {
        btnProfile = findViewById(R.id.btnNavNotification);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnCreateInitiative = findViewById(R.id.btnCreateInitiative);
        btnExploreMap = findViewById(R.id.btnExploreMap);
        rvInitiatives = findViewById(R.id.rv_initiatives);

        tvActiveInitiativesCount = findViewById(R.id.tvActiveInitiativesCount);
        tvTotalWaterDistributed = findViewById(R.id.tvTotalWaterDistributed);
        tvTotalFamiliesBenefited = findViewById(R.id.tvTotalFamiliesBenefited);

        navHome = findViewById(R.id.navHome);
        navMap = findViewById(R.id.navMap);
        navAdd = findViewById(R.id.navAdd);
        navProfile = findViewById(R.id.navProfile);
    }

    // ميزة التفاعل والتنقل للشريط السفلي الموحد
    private void setupBottomNavigation() {
        if (navHome != null) {
            navHome.setAlpha(1.0f);
            navHome.setOnClickListener(v -> {
                // تحديث البيانات حياً عند ضغط زر الرئيسية
                fetchInitiativesFromFirebase();
                Toast.makeText(this, "تم تحديث لوحة التحكم", Toast.LENGTH_SHORT).show();
            });
        }

        // الانتقال لشاشة الخريطة الحقيقية (NeedMapActivity)
        if (navMap != null) {
            navMap.setOnClickListener(v -> {
                Intent intent = new Intent(InitiatorDashboardActivity.this, NeedMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        // الانتقال لشاشة إضافة مبادرة جديدة (+)
        if (navAdd != null) {
            navAdd.setOnClickListener(v -> {
                Intent intent = new Intent(InitiatorDashboardActivity.this, CreateInitiativeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        //  الانتقال لملف المبادرين المعتمد (ProfileInitiatorsActivity) عند الضغط على التبويب السفلي
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(InitiatorDashboardActivity.this, ProfileInitiatorsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void setupRecyclerView() {
        if (rvInitiatives == null) return;

        initiativeList = new ArrayList<>();

        initiativesAdapter = new InitiativesAdapter(initiativeList, new InitiativesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InitiativeModel initiative) {
                Intent intent = new Intent(InitiatorDashboardActivity.this, InitiativeDetailsActivity.class);
                intent.putExtra("initiative_id", initiative.getId());
                startActivity(intent);
            }

            @Override
            public void onTrackProgressClick(InitiativeModel initiative) {
                Intent intent = new Intent(InitiatorDashboardActivity.this, InitiativeDetailsActivity.class);
                intent.putExtra("initiative_id", initiative.getId());
                startActivity(intent);
            }
        });

        rvInitiatives.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvInitiatives.setAdapter(initiativesAdapter);
        rvInitiatives.setHasFixedSize(true);
    }

    // جلب المبادرات بشكل فوري وآمن من Firestore
    private void fetchInitiativesFromFirebase() {
        db.collection("initiatives")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    initiativeList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String id = document.getId();
                            String title = document.getString("title");
                            String location = document.getString("location");

                            // تحويل الأرقام بأمان تام لتفادي الـ Crash إذا اختلفت أنواع البيانات بالفايربيس
                            int targetLiters = 0;
                            if (document.get("targetLiters") != null) {
                                targetLiters = ((Number) document.get("targetLiters")).intValue();
                            }

                            int currentLiters = 0;
                            if (document.get("currentLiters") != null) {
                                currentLiters = ((Number) document.get("currentLiters")).intValue();
                            }

                            String status = document.getString("status");

                            if (title != null) {
                                initiativeList.add(new InitiativeModel(id, title, location, targetLiters, currentLiters, status));
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "خطأ أثناء قراءة المبادرة الحالية: " + e.getMessage());
                        }
                    }

                    // تحديث قائمة العرض والإحصائيات
                    initiativesAdapter.notifyDataSetChanged();
                    calculateAndUpdateStats();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(InitiatorDashboardActivity.this, "⚠️ فشل تحديث المبادرات من السيرفر", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "فشل Firestore: " + e.getLocalizedMessage());
                });
    }

    // معالجة وحساب الإحصائيات باللغتين لضمان دقة العمليات
    private void calculateAndUpdateStats() {
        if (initiativeList == null || initiativeList.isEmpty()) {
            updateStatsViews(0, 0, 0);
            return;
        }

        int activeCount = 0;
        long totalWaterDistributed = 0;

        for (InitiativeModel initiative : initiativeList) {
            String status = initiative.getStatus();
            if (status != null) {
                // التحقق باللغتين العربية والإنجليزية لضمان رصد المبادرة النشطة
                if (status.equalsIgnoreCase("نشطة") || status.equalsIgnoreCase("مستمرة") ||
                        status.equalsIgnoreCase("active") || status.equalsIgnoreCase("ongoing")) {
                    activeCount++;
                }
            }
            totalWaterDistributed += initiative.getCurrentLiters();
        }

        // عائلة واحدة لكل 250 لتر بشكل تقديري
        long totalFamilies = totalWaterDistributed / 250;

        updateStatsViews(activeCount, totalWaterDistributed, totalFamilies);
    }

    private void updateStatsViews(int activeCount, long waterDistributed, long familiesBenefited) {
        if (tvActiveInitiativesCount != null) {
            tvActiveInitiativesCount.setText(" (" + activeCount + ") ");
        }

        if (tvTotalWaterDistributed != null) {
            if (waterDistributed >= 1000) {
                tvTotalWaterDistributed.setText(String.format("%.1fK لتر", waterDistributed / 1000.0));
            } else {
                tvTotalWaterDistributed.setText(waterDistributed + " لتر");
            }
        }

        if (tvTotalFamiliesBenefited != null) {
            tvTotalFamiliesBenefited.setText(familiesBenefited + " عائلة");
        }
    }

    private void setupClickListeners() {
        //  صورة الحساب الشخصي العلوية تنقل الآن لملف التعريف الخاص بالمبادرين (ProfileInitiatorsActivity)
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileInitiatorsActivity.class);
                startActivity(intent);
            });
        }

        if (btnViewAll != null) {
            btnViewAll.setOnClickListener(v -> {
                Intent intent = new Intent(this, InitiativesListActivity.class);
                startActivity(intent);
            });
        }

        if (btnCreateInitiative != null) {
            btnCreateInitiative.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateInitiativeActivity.class);
                startActivity(intent);
            });
        }

        if (btnExploreMap != null) {
            btnExploreMap.setOnClickListener(v -> {
                Intent intent = new Intent(this, NeedMapActivity.class);
                startActivity(intent);
            });
        }
    }
}