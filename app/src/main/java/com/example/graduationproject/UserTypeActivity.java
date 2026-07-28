package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class UserTypeActivity extends AppCompatActivity {

    // 0: لم يتم الاختيار، 1: مواطن، 2: مقدم خدمة، 3: مبادر/منظم
    private int selectedType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        ImageView btnBack = findViewById(R.id.btnBack);
        MaterialCardView btnNext = findViewById(R.id.btnNext);
        TextView btnNextText = (TextView) btnNext.getChildAt(0);

        MaterialCardView cardClient = findViewById(R.id.cardClient);
        MaterialCardView cardProvider = findViewById(R.id.cardProvider);
        MaterialCardView cardInitiative = findViewById(R.id.cardInitiative);

        // 1. اختيار حساب مواطن (طالب خدمة)
        cardClient.setOnClickListener(v -> {
            selectedType = 1;
            cardClient.setCardBackgroundColor(Color.parseColor("#F0F9FF"));
            cardProvider.setCardBackgroundColor(Color.WHITE);
            cardInitiative.setCardBackgroundColor(Color.WHITE);

            btnNext.setCardBackgroundColor(Color.parseColor("#0069B4"));
            btnNextText.setTextColor(Color.WHITE);
        });

        // 2. اختيار حساب مقدم خدمة
        cardProvider.setOnClickListener(v -> {
            selectedType = 2;
            cardProvider.setCardBackgroundColor(Color.parseColor("#F0F9FF"));
            cardClient.setCardBackgroundColor(Color.WHITE);
            cardInitiative.setCardBackgroundColor(Color.WHITE);

            btnNext.setCardBackgroundColor(Color.parseColor("#0069B4"));
            btnNextText.setTextColor(Color.WHITE);
        });

        // 3. اختيار حساب المبادرين
        cardInitiative.setOnClickListener(v -> {
            selectedType = 3;
            cardInitiative.setCardBackgroundColor(Color.parseColor("#F0F9FF"));
            cardClient.setCardBackgroundColor(Color.WHITE);
            cardProvider.setCardBackgroundColor(Color.WHITE);

            btnNext.setCardBackgroundColor(Color.parseColor("#0069B4"));
            btnNextText.setTextColor(Color.WHITE);
        });

        btnBack.setOnClickListener(v -> finish());

        // إدارة التنقل بناءً على نوع الحساب المختار
        btnNext.setOnClickListener(v -> {
            if (selectedType == 1) {
                Intent intent = new Intent(UserTypeActivity.this, RegisterActivity.class);
                startActivity(intent);
            } else if (selectedType == 2) {
                Intent intent = new Intent(UserTypeActivity.this, RegisterProviderActivity.class);
                startActivity(intent);
            } else if (selectedType == 3) {
                Intent intent = new Intent(UserTypeActivity.this, activity_initiative_register.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "يرجى اختيار نوع الحساب للمتابعة", Toast.LENGTH_SHORT).show();
            }
        });
    }
}