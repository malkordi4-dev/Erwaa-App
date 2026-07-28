package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProviderServicesActivity extends AppCompatActivity {

    private RecyclerView rvServices;
    private MaterialButton btnAddNewService;
    private ProviderServicesAdapter adapter;
    private List<ServiceModel> serviceList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_services);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rvServices = findViewById(R.id.rvServices);
        btnAddNewService = findViewById(R.id.btnAddNewService);

        rvServices.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProviderServicesAdapter(serviceList);
        rvServices.setAdapter(adapter);
        
        loadProviderServices();

        btnAddNewService.setOnClickListener(v -> {
            Toast.makeText(this, "إضافة خدمة جديدة قيد التطوير", Toast.LENGTH_SHORT).show();
        });

        setupBottomNavigation();
    }

    private void loadProviderServices() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("services")
                .whereEqualTo("provider_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    serviceList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ServiceModel service = document.toObject(ServiceModel.class);
                        // If ServiceModel doesn't have an ID field that matches document ID, we might need to set it
                        // service.setId(document.getId()); 
                        serviceList.add(service);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("ProviderServices", "Error loading services", e);
                    Toast.makeText(this, "فشل تحميل الخدمات", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupBottomNavigation() {
        View navDashboard = findViewById(R.id.navProviderDashboard);
        View navOrders = findViewById(R.id.navProviderOrders);
        View navServices = findViewById(R.id.navProviderServices);
        View navHistory = findViewById(R.id.navProviderHistory);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                startActivity(new Intent(this, ProviderDashboardActivity.class));
                finish();
            });
        }

        if (navOrders != null) {
            navOrders.setOnClickListener(v -> {
                startActivity(new Intent(this, OrdersManagementActivity.class));
                finish();
            });
        }

        if (navHistory != null) {
            navHistory.setOnClickListener(v -> {
                startActivity(new Intent(this, TripsHistoryActivity.class));
                finish();
            });
        }
    }
}
