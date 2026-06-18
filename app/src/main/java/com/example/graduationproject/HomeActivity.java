package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        
        // جلب اسم المستخدم من التفضيلات (تم تخزينه أثناء Login/Register)
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String userName = prefs.getString("full_name", "المستخدم");
        tvWelcome.setText("مرحباً بك، " + userName);

        CardView btnOpenMap = findViewById(R.id.btnOpenMap);
        btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MapExplorerActivity.class);
            startActivity(intent);
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // مسح بيانات الجلسة عند تسجيل الخروج
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        setupBottomNavigation();
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

        findViewById(R.id.navOrders).setOnClickListener(v -> {
             startActivity(new Intent(this, My_Orders_Activity.class));
        });

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            // Current Page
        });
    }
}
