package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TripsHistoryActivity extends AppCompatActivity {

    private RecyclerView rvTripsHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_history);

        rvTripsHistory = findViewById(R.id.rvTripsHistory);
        rvTripsHistory.setLayoutManager(new LinearLayoutManager(this));

        // Load dummy or real data
        loadTripsHistory();

        setupBottomNavigation();
    }

    private void loadTripsHistory() {
        // Logic to fetch trip history from Supabase
    }

    private void setupBottomNavigation() {
        // Find bottom nav items if they had IDs, but they don't in activity_trips_history.xml
        // Usually, it's better to use a shared BottomNavigationView or common layout include.
        // For now, I'll focus on the logic.
    }
}
