package com.example.graduationproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Monthly_Subscription_Activity extends AppCompatActivity {

    private CardView planBasic, planStandard, planFamily;
    private CardView freqWeekly, freqBiWeekly;
    private CardView[] dayCards;
    private TextView[] dayTexts;
    private TextView tvSummaryPrice, tvBottomPrice, tvSelectedTime;
    
    private int selectedPlanPrice = 45;
    private String selectedPlanName = "الباقة الأساسية";
    private String selectedFrequency = "كل أسبوع";
    private String selectedDay = "السبت";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_subscription);

        // Initialize Views
        ImageView btnBack = findViewById(R.id.btnBack);
        planBasic = findViewById(R.id.planBasic);
        planStandard = findViewById(R.id.planStandard);
        planFamily = findViewById(R.id.planFamily);
        
        freqWeekly = findViewById(R.id.freqWeekly);
        freqBiWeekly = findViewById(R.id.freqBiWeekly);
        
        tvSummaryPrice = findViewById(R.id.tvSummaryPrice);
        tvBottomPrice = findViewById(R.id.tvBottomPrice);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        
        CardView btnConfirmSubscription = findViewById(R.id.btnConfirmSubscription);
        CardView btnSelectTime = findViewById(R.id.btnSelectTime);

        // Days Initialization
        dayCards = new CardView[]{
                findViewById(R.id.daySat), findViewById(R.id.daySun), findViewById(R.id.dayMon),
                findViewById(R.id.dayTue), findViewById(R.id.dayWed), findViewById(R.id.dayThu)
        };
        dayTexts = new TextView[]{
                findViewById(R.id.tvSat), findViewById(R.id.tvSun), findViewById(R.id.tvMon),
                findViewById(R.id.tvTue), findViewById(R.id.tvWed), findViewById(R.id.tvThu)
        };

        // Back Button
        btnBack.setOnClickListener(v -> finish());

        // Plan Selection
        planBasic.setOnClickListener(v -> selectPlan(45, "الباقة الأساسية", planBasic));
        planStandard.setOnClickListener(v -> selectPlan(90, "الباقة القياسية", planStandard));
        planFamily.setOnClickListener(v -> selectPlan(160, "الباقة العائلية", planFamily));

        // Frequency Selection
        freqWeekly.setOnClickListener(v -> selectFrequency("كل أسبوع", freqWeekly, freqBiWeekly));
        freqBiWeekly.setOnClickListener(v -> selectFrequency("كل أسبوعين", freqBiWeekly, freqWeekly));

        // Days Selection
        for (int i = 0; i < dayCards.length; i++) {
            final int index = i;
            dayCards[i].setOnClickListener(v -> selectDay(index));
        }

        // Time Selection (Simple Toggle for Demo)
        btnSelectTime.setOnClickListener(v -> {
            if (tvSelectedTime.getText().toString().contains("الصباحية")) {
                tvSelectedTime.setText("🕒  الفترة المسائية (1:00 م - 5:00 م)");
            } else {
                tvSelectedTime.setText("🕒  الفترة الصباحية (8:00 ص - 12:00 م)");
            }
        });

        // Confirm Button
        btnConfirmSubscription.setOnClickListener(v -> {
            String message = "تم تأكيد " + selectedPlanName + " (" + selectedFrequency + ") يوم " + selectedDay;
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void selectPlan(int price, String name, CardView selectedCard) {
        selectedPlanPrice = price;
        selectedPlanName = name;

        // Reset all plans background
        planBasic.setCardBackgroundColor(Color.WHITE);
        planStandard.setCardBackgroundColor(Color.WHITE);
        planFamily.setCardBackgroundColor(Color.WHITE);

        // Highlight selected
        selectedCard.setCardBackgroundColor(Color.parseColor("#E0F2FE"));

        // Update Prices
        tvSummaryPrice.setText(price + ".00 ₪");
        tvBottomPrice.setText(price + ".00 ₪");
    }

    private void selectFrequency(String freq, CardView selected, CardView unselected) {
        selectedFrequency = freq;
        
        selected.setCardBackgroundColor(Color.WHITE);
        ((TextView)selected.getChildAt(0)).setTextColor(Color.parseColor("#0D63B3"));
        
        unselected.setCardBackgroundColor(Color.TRANSPARENT);
        ((TextView)unselected.getChildAt(0)).setTextColor(Color.parseColor("#64748B"));
    }

    private void selectDay(int index) {
        selectedDay = dayTexts[index].getText().toString();
        
        // Reset all days
        for (int i = 0; i < dayCards.length; i++) {
            dayCards[i].setCardBackgroundColor(Color.WHITE);
            dayTexts[i].setTextColor(Color.parseColor("#64748B"));
        }

        // Highlight selected day
        dayCards[index].setCardBackgroundColor(Color.parseColor("#0D63B3"));
        dayTexts[index].setTextColor(Color.WHITE);
    }
}
