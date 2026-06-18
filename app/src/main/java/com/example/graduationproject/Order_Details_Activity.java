package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class Order_Details_Activity extends AppCompatActivity {

    private TextView tvOrderId, tvStatusBadge, tvCustomerName, tvAddress, tvUnit, tvQuantity, tvTotalPrice;
    private MaterialButton btnCancelOrder, btnTrackOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // ربط العناصر
        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvAddress = findViewById(R.id.tvAddress);
        tvUnit = findViewById(R.id.tvUnit);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCancelOrder = findViewById(R.id.btnConfirmArrival); // ID من XML لزر الإلغاء
        btnTrackOrder = findViewById(R.id.btnCompleteTask);    // ID من XML لزر التتبع
        ImageView btnBack = findViewById(R.id.btnBack);

        // العودة للخلف
        btnBack.setOnClickListener(v -> finish());

        // استقبال البيانات من Intent
        Intent intent = getIntent();
        String orderId = intent.getStringExtra("order_id");
        String status = intent.getStringExtra("status");
        double price = intent.getDoubleExtra("price", 0.0);
        int quantity = intent.getIntExtra("quantity", 0);
        String unit = intent.getStringExtra("unit");
        String address = intent.getStringExtra("address");

        // عرض البيانات
        if (orderId != null) tvOrderId.setText("#" + (orderId.length() > 8 ? orderId.substring(0, 8) : orderId));
        tvTotalPrice.setText(String.format("%.2f ₪", price));
        tvQuantity.setText(String.valueOf(quantity));
        tvUnit.setText(unit != null ? unit : "لتر");
        tvAddress.setText(address != null ? "📍 " + address : "📍 العنوان غير محدد");
        
        // تحديث حالة الطلب
        updateStatusUI(status);

        // برمجة الأزرار
        btnCancelOrder.setOnClickListener(v -> {
            Toast.makeText(this, "سيتم مراجعة طلب الإلغاء من قبل الإدارة", Toast.LENGTH_LONG).show();
        });

        btnTrackOrder.setOnClickListener(v -> {
            Intent trackIntent = new Intent(this, Track_Driver_Activity.class);
            startActivity(trackIntent);
        });
    }

    private void updateStatusUI(String status) {
        if (status == null) return;
        switch (status) {
            case "pending":
                tvStatusBadge.setText("● قيد الانتظار");
                btnTrackOrder.setVisibility(View.GONE); // لا يمكن التتبع حتى يقبل السائق
                break;
            case "accepted":
            case "on_way":
                tvStatusBadge.setText("● في الطريق");
                btnTrackOrder.setVisibility(View.VISIBLE);
                break;
            case "delivered":
                tvStatusBadge.setText("● تم التوصيل");
                btnTrackOrder.setVisibility(View.GONE);
                btnCancelOrder.setVisibility(View.GONE);
                break;
        }
    }
}
