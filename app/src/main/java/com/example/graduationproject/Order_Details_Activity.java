package com.example.graduationproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class Order_Details_Activity extends AppCompatActivity {

    private TextView tvOrderId, tvStatusText, tvCustomerName, tvAddress, tvWaterAmount, tvTotalPrice;
    private MaterialButton btnConfirmArrival, btnCompleteTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // Initialize Views
        tvOrderId = findViewById(R.id.tvOrderId);
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        
        // Note: Some IDs are inferred from your XML layout
        ImageView btnBack = findViewById(R.id.btnBack);
        btnConfirmArrival = findViewById(R.id.btnConfirmArrival);
        btnCompleteTask = findViewById(R.id.btnCompleteTask);

        btnBack.setOnClickListener(v -> finish());

        // Get data from Intent
        String orderId = getIntent().getStringExtra("order_id");
        String status = getIntent().getStringExtra("status");
        double price = getIntent().getDoubleExtra("price", 0.0);
        int quantity = getIntent().getIntExtra("quantity", 0);
        String unit = getIntent().getStringExtra("unit");
        String address = getIntent().getStringExtra("address");

        // Set Data to UI
        if (orderId != null) tvOrderId.setText("#" + (orderId.length() > 8 ? orderId.substring(0, 8) : orderId));
        tvTotalPrice.setText(String.format("%.2f ₪", price));
        
        // Find quantity text view (it was nested in a card in your XML)
        // We'll update the most relevant ones
        if (address != null) {
            // If you have a specific ID for address text, use it here. 
            // Based on your XML, it's a TextView with text "📍 الرمال، شارع الوحدة"
            // I'll assume you might want to find it by its hardcoded text or add an ID
        }

        btnConfirmArrival.setOnClickListener(v -> {
            Toast.makeText(this, "تم إرسال إشعار بالوصول للموقع", Toast.LENGTH_SHORT).show();
            btnConfirmArrival.setEnabled(false);
            btnConfirmArrival.setText("تم تأكيد الوصول");
        });

        btnCompleteTask.setOnClickListener(v -> {
            Toast.makeText(this, "تم إتمام الطلب بنجاح. شكراً لك!", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
