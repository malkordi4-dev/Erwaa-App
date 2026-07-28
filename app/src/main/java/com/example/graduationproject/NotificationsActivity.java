package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class NotificationsActivity extends AppCompatActivity {

    private ImageView btnBack, btnSettings;
    private MaterialCardView btnReadAll;
    private View unreadIndicator;
    private View btnAcceptOrder, btnOrderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        btnReadAll = findViewById(R.id.btnReadAll);
        unreadIndicator = findViewById(R.id.unreadIndicator);

        // جلب أزرار التحكم داخل بطاقة الإشعار الأول (طلب التوريد)
        // ملاحظة: بما أن الأزرار لم تملك معرفات ID في ملفك، سنقوم بالوصول إليها ديناميكياً أو يمكنك إضافتها لاحقاً
        // هنا قمنا بعمل محاكاة منطقية للتفاعل
    }

    private void setupClickListeners() {
        // زر العودة للخلف
        btnBack.setOnClickListener(v -> finish());

        // زر الإعدادات / التنبيهات العلوي
        btnSettings.setOnClickListener(v ->
                Toast.makeText(NotificationsActivity.this, "الانتقال إلى إعدادات الإشعارات", Toast.LENGTH_SHORT).show()
        );

        // زر قراءة الكل (إخفاء شريط الإشعار الأزرق غير المقروء)
        btnReadAll.setOnClickListener(v -> {
            if (unreadIndicator != null && unreadIndicator.getVisibility() == View.VISIBLE) {
                unreadIndicator.setVisibility(View.GONE);
                Toast.makeText(NotificationsActivity.this, "تم تعيين جميع الإشعارات كمقروءة", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NotificationsActivity.this, "لا توجد إشعارات غير مقروءة", Toast.LENGTH_SHORT).show();
            }
        });
        // داخل ميثود setupClickListeners() في NotificationsActivity.java
// عند النقر على زر "التفاصيل" في الإشعار الأول
        btnOrderDetails.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, DeliveryConfirmationActivity.class);
            intent.putExtra("order_id", "#WA-8821");
            startActivity(intent);
        });
    }

    // ميثود مساعدة للتعامل مع نقرات الفلترة (الكل، طلبات جديدة، التقييمات، المدفوعات)
    public void onFilterClicked(View view) {
        TextView clickedFilter = (TextView) view;
        String filterText = clickedFilter.getText().toString();
        Toast.makeText(this, "تصفية حسب: " + filterText, Toast.LENGTH_SHORT).show();

        // هنا يمكنك بناء منطق تصفية البيانات (Filtering Logic) إذا كانت الإشعارات تعرض داخل RecyclerView
    }
}
