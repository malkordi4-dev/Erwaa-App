package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class Water_Delivered_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_delivered);

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
            // Start Rate_Service_Activity and pass order data
            Intent intent = new Intent(Water_Delivered_Activity.this, Rate_Service_Activity.class);
            
            // Passing dummy/placeholder data - in a real scenario, these would come from an Order object
            intent.putExtra("order_id", "WA-8821");
            intent.putExtra("station_name", "محطة مياه النصر المركزية");
            intent.putExtra("order_number", "#WA-8821");
            intent.putExtra("order_date", "24 مايو، 10:30 صباحاً");
            intent.putExtra("quantity", "1000 لتر");
            intent.putExtra("price", "30.00 ₪");
            
            startActivity(intent);
            finish();
        });
    }
}
