package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Order_Accepted_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_accepted);

        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnNavigateToPayment = findViewById(R.id.btnNavigateToPayment);
        Button btnCancelCurrentOrder = findViewById(R.id.btnCancelCurrentOrder);

        btnBack.setOnClickListener(v -> finish());

        btnNavigateToPayment.setOnClickListener(v -> {
            Intent intent = new Intent(Order_Accepted_Activity.this, Payment_Method_Activity.class);
            startActivity(intent);
        });

        btnCancelCurrentOrder.setOnClickListener(v -> {
            // Logic for cancellation
            finish();
        });
    }
}
