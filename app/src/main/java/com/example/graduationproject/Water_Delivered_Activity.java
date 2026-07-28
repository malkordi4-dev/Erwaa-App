package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class Water_Delivered_Activity extends AppCompatActivity {

    private String orderId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_delivered);

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("order_id");

        ImageView btnMenu = findViewById(R.id.btnMenuDelivered);
        ImageView btnNotifications = findViewById(R.id.btnNotificationsDelivered);
        MaterialButton btnConfirmDelivery = findViewById(R.id.btnConfirmDelivery);

        btnMenu.setOnClickListener(v -> {
            // Handle menu click
        });

        btnNotifications.setOnClickListener(v -> {
            // Handle notifications click
        });

        btnConfirmDelivery.setOnClickListener(v -> {
            if (orderId == null) {
                Toast.makeText(this, "خطأ في بيانات الطلب", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Fetch order details to pass to Rating activity
            db.collection("orders").document(orderId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Intent intent = new Intent(Water_Delivered_Activity.this, Rate_Service_Activity.class);
                    intent.putExtra("order_uuid", orderId);
                    intent.putExtra("provider_id", documentSnapshot.getString("provider_id"));
                    intent.putExtra("station_name", documentSnapshot.getString("provider_name"));
                    intent.putExtra("order_number", "#" + (orderId.length() > 8 ? orderId.substring(0, 8) : orderId));
                    
                    // Format quantity and price for display
                    Long quantity = documentSnapshot.getLong("quantity");
                    String unit = documentSnapshot.getString("unit");
                    Double price = documentSnapshot.getDouble("total_price");
                    
                    intent.putExtra("quantity", (quantity != null ? quantity : "---") + " " + (unit != null ? unit : ""));
                    intent.putExtra("price", String.format("%.2f ₪", price != null ? price : 0.0));
                    
                    // You might want to format the creation date here as well
                    intent.putExtra("order_date", "تم التوصيل بنجاح");

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "الطلب غير موجود", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "فشل تحميل بيانات الطلب", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
