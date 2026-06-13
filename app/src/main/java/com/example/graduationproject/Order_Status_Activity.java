package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Order_Status_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnCancelOrder = findViewById(R.id.btnCancelOrder);
        TextView tvStatusDescription = findViewById(R.id.tvStatusDescription);

        btnBack.setOnClickListener(v -> finish());

        btnCancelOrder.setOnClickListener(v -> {
            Toast.makeText(this, "تم إلغاء الطلب", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Simulating order acceptance after 5 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing()) {
                Intent intent = new Intent(Order_Status_Activity.this, Order_Accepted_Activity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}
