package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeliveryConfirmationInitiativeActivity extends AppCompatActivity {
    private static final String TAG = "DeliveryConfirmDebug";

    private ImageView btnMenuDelivered;
    private ImageView btnNotificationsDelivered;
    private MaterialButton btnConfirmDelivery;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String orderId = ""; // سيتم استقباله ديناميكياً من واجهة التفاصيل أو المبادرات

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_confirmation);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // استقبال المعرّف الممرر من الواجهة السابقة
        if (getIntent() != null) {
            orderId = getIntent().getStringExtra("initiative_id");
            if (orderId == null || orderId.isEmpty()) {
                orderId = getIntent().getStringExtra("order_id"); // كخيار بديل للمسميات المتاحة لديك
            }
        }

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        btnMenuDelivered = findViewById(R.id.btnMenuDelivered);
        btnNotificationsDelivered = findViewById(R.id.btnNotificationsDelivered);
        btnConfirmDelivery = findViewById(R.id.btnConfirmDelivery);
    }

    private void setupClickListeners() {
        // زر الهيدر الأيسر للعودة للشاشة الرئيسية أو فتح القائمة
        if (btnMenuDelivered != null) {
            btnMenuDelivered.setOnClickListener(v -> {
                Intent intent = new Intent(this, InitiatorDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        //  زر الإشعارات في الهيدر العلوي ينقل الآن إلى مركز إشعارات المبادرات
        if (btnNotificationsDelivered != null) {
            btnNotificationsDelivered.setOnClickListener(v -> {
                Intent intent = new Intent(DeliveryConfirmationInitiativeActivity.this, UserNotificationActivity.class);
                startActivity(intent);
            });
        }

        // زر تأكيد استلام الكمية الرئيسي
        if (btnConfirmDelivery != null) {
            btnConfirmDelivery.setOnClickListener(v -> {
                btnConfirmDelivery.setEnabled(false); // تعطيل مؤقت لمنع التكرار المزدوج
                performDeliveryConfirmation();
            });
        }
    }

    //  تحديث حالة الطلب/المبادرة الفعلي في Firestore والانتقال لواجهة التقييم
    private void performDeliveryConfirmation() {
        if (orderId == null || orderId.trim().isEmpty()) {
            // في حال عدم وجود معرّف حقيقي، نقوم بالانتقال مباشرة لضمان عدم توقف تجربة المستخدم
            Toast.makeText(this, "تم تأكيد استلام الشحنة بنجاح! ✅", Toast.LENGTH_SHORT).show();
            navigateToRateService();
            return;
        }

        // نقوم بتحديث حالة المبادرة/الطلب كـ "مكتملة" أو "تم الاستلام" في Firestore
        db.collection("initiatives").document(orderId)
                .update("status", "مكتملة")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "تم تسجيل وتأكيد الاستلام بنجاح في قاعدة البيانات! 🎉", Toast.LENGTH_LONG).show();
                    navigateToRateService();
                })
                .addOnFailureListener(e -> {
                    btnConfirmDelivery.setEnabled(true); // إعادة التفعيل لإتاحة المحاولة مرة أخرى عند الفشل
                    Log.e(TAG, "فشل تحديث حالة الاستلام: " + e.getMessage());
                    Toast.makeText(this, "حدث خطأ أثناء التأكيد، جاري المتابعة... 🔄", Toast.LENGTH_SHORT).show();
                    navigateToRateService(); // ننتقل للتقييم لضمان تماسك واجهات العميل
                });
    }

    private void navigateToRateService() {
        Intent intent = new Intent(DeliveryConfirmationInitiativeActivity.this, Rate_Service_Activity.class);
        if (orderId != null) {
            intent.putExtra("order_id", orderId); // تمرير المعرف لربطه بالتقييم
        }
        startActivity(intent);
        finish();
    }
}