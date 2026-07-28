package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class Order_Details_Activity extends AppCompatActivity {

    private TextView tvOrderId, tvStatusBadge, tvCustomerName, tvAddress, tvUnit, tvQuantity, tvTotalPrice;
    private MaterialButton btnCancelOrder, btnTrackOrder;
    private FirebaseFirestore db;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        db = FirebaseFirestore.getInstance();

        // ربط العناصر
        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatusBadge = findViewById(R.id.tvStatusBadge);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvAddress = findViewById(R.id.tvAddress);
        tvUnit = findViewById(R.id.tvUnit);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCancelOrder = findViewById(R.id.btnConfirmArrival); // زر إلغاء الطلب في الـ XML
        btnTrackOrder = findViewById(R.id.btnCompleteTask);    // زر تتبع الطلب في الـ XML
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        orderId = getIntent().getStringExtra("order_id");

        if (orderId != null) {
            fetchOrderDetails();
        } else {
            Toast.makeText(this, "معرف الطلب غير موجود", Toast.LENGTH_SHORT).show();
        }

        btnCancelOrder.setOnClickListener(v -> {
            if (orderId != null) {
                cancelOrder();
            }
        });

        btnTrackOrder.setOnClickListener(v -> {
            Intent trackIntent = new Intent(this, Track_Driver_Activity.class);
            trackIntent.putExtra("order_id", orderId);
            startActivity(trackIntent);
        });
    }

    private void fetchOrderDetails() {
        db.collection("orders").document(orderId).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("OrderDetails", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                String status = snapshot.getString("status");
                String providerName = snapshot.getString("provider_name");
                String address = snapshot.getString("address_details");
                String unit = snapshot.getString("unit");
                Long quantity = snapshot.getLong("quantity");
                Double totalPrice = snapshot.getDouble("total_price");

                tvOrderId.setText("#" + (orderId.length() > 8 ? orderId.substring(0, 8) : orderId));
                tvCustomerName.setText(providerName != null ? providerName : "مزود الخدمة");
                tvAddress.setText(address != null ? "📍 " + address : "📍 العنوان غير محدد");
                tvUnit.setText(unit != null ? unit : "لتر");
                tvQuantity.setText(quantity != null ? String.valueOf(quantity) : "---");
                tvTotalPrice.setText(String.format("%.2f ₪", totalPrice != null ? totalPrice : 0.0));

                updateStatusUI(status);
            }
        });
    }

    private void cancelOrder() {
        db.collection("orders").document(orderId)
                .update("status", "cancelled")
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "تم إلغاء الطلب بنجاح", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "فشل في إلغاء الطلب", Toast.LENGTH_SHORT).show());
    }

    private void updateStatusUI(String status) {
        if (status == null) return;
        switch (status) {
            case "pending":
                tvStatusBadge.setText("● قيد الانتظار");
                btnTrackOrder.setVisibility(View.GONE);
                btnCancelOrder.setVisibility(View.VISIBLE);
                break;
            case "accepted":
            case "on_way":
                tvStatusBadge.setText("● في الطريق");
                btnTrackOrder.setVisibility(View.VISIBLE);
                btnCancelOrder.setVisibility(View.VISIBLE);
                break;
            case "delivered":
                tvStatusBadge.setText("● تم التوصيل");
                btnTrackOrder.setVisibility(View.GONE);
                btnCancelOrder.setVisibility(View.GONE);
                break;
            case "cancelled":
                tvStatusBadge.setText("● تم الإلغاء");
                btnTrackOrder.setVisibility(View.GONE);
                btnCancelOrder.setVisibility(View.GONE);
                break;
        }
    }
}
