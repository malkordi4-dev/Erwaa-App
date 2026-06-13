package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Payment_Method_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        ImageView btnBack = findViewById(R.id.btnBack);
        MaterialButton btnConfirmPaymentAction = findViewById(R.id.btnConfirmPaymentAction);

        btnBack.setOnClickListener(v -> finish());

        btnConfirmPaymentAction.setOnClickListener(v -> {
            Toast.makeText(this, "تمت عملية الدفع بنجاح", Toast.LENGTH_LONG).show();
            // Typically navigate to a success screen or back to home/order status
            Intent intent = new Intent(Payment_Method_Activity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
