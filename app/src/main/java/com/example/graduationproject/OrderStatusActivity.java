package com.example.graduationproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderStatusActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvAcceptanceMainDesc, tvServiceTypeName, tvServiceQuantityValue, tvPaymentStatusLabel, tvOrderSerialAndDate;
    private Button btnNavigateToPayment, btnCancelCurrentOrder;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        btnBack = findViewById(R.id.btnBack);
        tvAcceptanceMainDesc = findViewById(R.id.tvAcceptanceMainDesc);
        tvServiceTypeName = findViewById(R.id.tvServiceTypeName);
        tvServiceQuantityValue = findViewById(R.id.tvServiceQuantityValue);
        tvPaymentStatusLabel = findViewById(R.id.tvPaymentStatusLabel);
        tvOrderSerialAndDate = findViewById(R.id.tvOrderSerialAndDate);
        btnNavigateToPayment = findViewById(R.id.btnNavigateToPayment);
        btnCancelCurrentOrder = findViewById(R.id.btnCancelCurrentOrder);

        btnBack.setOnClickListener(v -> finish());

        btnNavigateToPayment.setOnClickListener(v -> {
            Toast.makeText(this, "جاري فتح بوابة الدفع الإلكتروني وتأكيد الحجز...", Toast.LENGTH_SHORT).show();
        });

        btnCancelCurrentOrder.setOnClickListener(v -> {
            Toast.makeText(this, "تم إرسال طلب إلغاء الحجز بنجاح", Toast.LENGTH_SHORT).show();
        });
    }
}