package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ServicesActivity extends AppCompatActivity {
    CardView singleOrderCardView, MonthlySubscriptionCardView, GroupInitiativeCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);
        singleOrderCardView = findViewById(R.id.cardSingleOrder);
        MonthlySubscriptionCardView = findViewById(R.id.cardMonthlySubscription);
        GroupInitiativeCardView = findViewById(R.id.cardGroupInitiative);
        singleOrderCardView.setOnClickListener(v -> {
            Intent intent = new Intent(ServicesActivity.this, Order_Checkout_Activity.class
                   );
            startActivity(intent);
        });

        MonthlySubscriptionCardView.setOnClickListener(v -> {
            Intent intent = new Intent(ServicesActivity.this, Monthly_Subscription_Activity.class);
            startActivity(intent);

        });
        GroupInitiativeCardView.setOnClickListener(v -> {
            Intent intent = new Intent(ServicesActivity.this, Group_Order_Activity.class);
            startActivity(intent);

        });

    }
}
