package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesActivity extends AppCompatActivity {
    CardView cardSingleOrder, cardMonthlySubscription, cardGroupInitiative;
    TextView tvSingleTitle, tvSingleDesc, tvMonthlyTitle, tvGroupTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        cardSingleOrder = findViewById(R.id.cardSingleOrder);
        cardMonthlySubscription = findViewById(R.id.cardMonthlySubscription);
        cardGroupInitiative = findViewById(R.id.cardGroupInitiative);

        tvSingleTitle = findViewById(R.id.tvSingleOrderTitle);
        tvSingleDesc = findViewById(R.id.tvSingleOrderDesc);
        tvMonthlyTitle = findViewById(R.id.tvMonthlySubscriptionTitle);
        tvGroupTitle = findViewById(R.id.tvGroupInitiativeTitle);

        selectCard(cardSingleOrder);
        
        // جلب البيانات الديناميكية من سوبابيز لتحديث النصوص إذا تغيرت في القاعدة
        loadServicesData();

        cardSingleOrder.setOnClickListener(v -> {
            selectCard(cardSingleOrder);
            v.postDelayed(() -> {
                Intent intent = new Intent(ServicesActivity.this, Order_Checkout_Activity.class);
                intent.putExtra("service_id", 1); 
                startActivity(intent);
            }, 200);
        });

        cardMonthlySubscription.setOnClickListener(v -> {
            selectCard(cardMonthlySubscription);
            v.postDelayed(() -> {
                Intent intent = new Intent(ServicesActivity.this, Monthly_Subscription_Activity.class);
                intent.putExtra("service_id", 2);
                startActivity(intent);
            }, 200);
        });

        cardGroupInitiative.setOnClickListener(v -> {
            selectCard(cardGroupInitiative);
            v.postDelayed(() -> {
                Intent intent = new Intent(ServicesActivity.this, Group_Order_Activity.class);
                intent.putExtra("service_id", 3);
                startActivity(intent);
            }, 200);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadServicesData() {
        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);
        api.getServices("*").enqueue(new Callback<List<ServiceModel>>() {
            @Override
            public void onResponse(Call<List<ServiceModel>> call, Response<List<ServiceModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (ServiceModel service : response.body()) {
                        updateServiceUI(service);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ServiceModel>> call, Throwable t) {
                Log.e("Supabase", "Error fetching services", t);
            }
        });
    }

    private void updateServiceUI(ServiceModel service) {
        // تحديث النصوص بناءً على المعرفات الموجودة في جدول SQL الذي أنشأناه
        if (service.getId() == 1) {
            tvSingleTitle.setText(service.getNameAr());
            tvSingleDesc.setText(service.getDescriptionAr());
        } else if (service.getId() == 2) {
            tvMonthlyTitle.setText(service.getNameAr());
        } else if (service.getId() == 3) {
            tvGroupTitle.setText(service.getNameAr());
        }
    }

    private void selectCard(CardView selectedCard) {
        resetCards();
        selectedCard.setCardBackgroundColor(Color.parseColor("#0D63B3"));
        selectedCard.setCardElevation(8f);
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
        cardSingleOrder.setCardBackgroundColor(Color.WHITE);
        cardMonthlySubscription.setCardBackgroundColor(Color.WHITE);
        cardGroupInitiative.setCardBackgroundColor(Color.WHITE);
        cardSingleOrder.setCardElevation(2f);
        cardMonthlySubscription.setCardElevation(2f);
        cardGroupInitiative.setCardElevation(2f);
        tvSingleTitle.setTextColor(Color.parseColor("#1E293B"));
        tvSingleDesc.setTextColor(Color.parseColor("#64748B"));
        tvMonthlyTitle.setTextColor(Color.parseColor("#1E293B"));
        tvGroupTitle.setTextColor(Color.parseColor("#1E293B"));
    }
}
