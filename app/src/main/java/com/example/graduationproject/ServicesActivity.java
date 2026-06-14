package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

public class ServicesActivity extends AppCompatActivity {
    CardView cardSingleOrder, cardMonthlySubscription, cardGroupInitiative;
    TextView tvSingleTitle, tvSingleDesc, tvMonthlyTitle, tvGroupTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        // ربط البطاقات
        cardSingleOrder = findViewById(R.id.cardSingleOrder);
        cardMonthlySubscription = findViewById(R.id.cardMonthlySubscription);
        cardGroupInitiative = findViewById(R.id.cardGroupInitiative);

        // ربط النصوص (لتغيير لونها عند الاختيار)
        tvSingleTitle = findViewById(R.id.tvSingleOrderTitle);
        tvSingleDesc = findViewById(R.id.tvSingleOrderDesc);
        tvMonthlyTitle = findViewById(R.id.tvMonthlySubscriptionTitle);
        tvGroupTitle = findViewById(R.id.tvGroupInitiativeTitle);

        // الإعداد الافتراضي (اختيار الطلب الفردي)
        selectCard(cardSingleOrder);

        cardSingleOrder.setOnClickListener(v -> {
            selectCard(cardSingleOrder);
            // الانتقال بعد تأخير بسيط ليشعر المستخدم باللمسة
            v.postDelayed(() -> {
                Intent intent = new Intent(ServicesActivity.this, Order_Checkout_Activity.class);
                startActivity(intent);
            }, 200);
        });

        cardMonthlySubscription.setOnClickListener(v -> {
            selectCard(cardMonthlySubscription);
            v.postDelayed(() -> {
                Intent intent = new Intent(ServicesActivity.this, Monthly_Subscription_Activity.class);
                startActivity(intent);
            }, 200);
        });

        cardGroupInitiative.setOnClickListener(v -> {
            selectCard(cardGroupInitiative);
            v.postDelayed(() -> {
                Intent intent = new Intent(ServicesActivity.this, Group_Order_Activity.class);
                startActivity(intent);
            }, 200);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void selectCard(CardView selectedCard) {
        // إعادة تعيين جميع البطاقات للوضع غير المختار
        resetCards();

        // تمييز البطاقة المختارة
        selectedCard.setCardBackgroundColor(Color.parseColor("#0D63B3"));
        selectedCard.setCardElevation(8f);

        // تغيير ألوان النصوص داخل البطاقة المختارة
        if (selectedCard == cardSingleOrder) {
            tvSingleTitle.setTextColor(Color.WHITE);
            tvSingleDesc.setTextColor(Color.parseColor("#93C5FD"));
        } else if (selectedCard == cardMonthlySubscription) {
            tvMonthlyTitle.setTextColor(Color.WHITE);
        } else if (selectedCard == cardGroupInitiative) {
            tvGroupTitle.setTextColor(Color.WHITE);
        }
    }

    private void resetCards() {
        // العودة للألوان الافتراضية
        cardSingleOrder.setCardBackgroundColor(Color.WHITE);
        cardMonthlySubscription.setCardBackgroundColor(Color.WHITE);
        cardGroupInitiative.setCardBackgroundColor(Color.WHITE);

        cardSingleOrder.setCardElevation(2f);
        cardMonthlySubscription.setCardElevation(2f);
        cardGroupInitiative.setCardElevation(2f);

        // إعادة ألوان النصوص للوضع الطبيعي
        tvSingleTitle.setTextColor(Color.parseColor("#1E293B"));
        tvSingleDesc.setTextColor(Color.parseColor("#64748B"));
        tvMonthlyTitle.setTextColor(Color.parseColor("#1E293B"));
        tvGroupTitle.setTextColor(Color.parseColor("#1E293B"));
    }
}
