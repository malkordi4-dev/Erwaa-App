package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class Order_Accepted_Activity extends AppCompatActivity {

    private String orderId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_accepted);

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("order_id");

        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnNavigateToPayment = findViewById(R.id.btnNavigateToPayment);
        Button btnCancelCurrentOrder = findViewById(R.id.btnCancelCurrentOrder);

        btnBack.setOnClickListener(v -> finish());

        btnNavigateToPayment.setOnClickListener(v -> {
            Intent intent = new Intent(Order_Accepted_Activity.this, Payment_Method_Activity.class);
            intent.putExtra("order_id", orderId);
            startActivity(intent);
        });

        btnCancelCurrentOrder.setOnClickListener(v -> {
            if (orderId != null) {
                cancelOrder();
            } else {
                finish();
            }
        });
    }

    private void cancelOrder() {
        db.collection("orders").document(orderId)
                .update("status", "cancelled")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "تم إلغاء الطلب", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "فشل في إلغاء الطلب", Toast.LENGTH_SHORT).show();
                });
    }
}
