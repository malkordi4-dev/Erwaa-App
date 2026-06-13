package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ProviderDetailsActivity extends AppCompatActivity {

    Button  c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_provider_details);
        c = findViewById(R.id.btnSelectService);
        c.setOnClickListener(v -> {

            Intent intent = new Intent(ProviderDetailsActivity.this, ServicesActivity.class );
            startActivity(intent);


        });


        // هنا يمكنك برمجة الأزرار والنصوص وتجهيز العمليات الخاصة بالصفحة
    }
}
