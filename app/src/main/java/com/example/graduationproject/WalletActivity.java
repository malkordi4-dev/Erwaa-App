package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class WalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

       // setupBottomNavigation();
    }
    /*

    private void setupBottomNavigation() {
        findViewById(R.id.bottomNavOrders).getChildAt(0).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        findViewById(R.id.bottomNavOrders).getChildAt(2).setOnClickListener(v -> {
            startActivity(new Intent(this, My_Orders_Activity.class));
        });

        findViewById(R.id.bottomNavOrders).getChildAt(3).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapExplorerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

     */
}
