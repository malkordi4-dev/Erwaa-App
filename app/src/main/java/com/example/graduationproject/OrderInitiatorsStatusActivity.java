package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class OrderInitiatorsStatusActivity extends AppCompatActivity{
        private ImageView btnBack;
        private TextView tvAcceptanceMainDesc;
        private TextView tvServiceTypeName;
        private TextView tvServiceQuantityValue;
        private TextView tvPaymentStatusLabel;
        private TextView tvOrderSerialAndDate;
        private Button btnNavigateToPayment;
        private Button btnCancelCurrentOrder;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order_waiting);
            initViews();
            displayOrderDetails();
            setupClickListeners();
        }
        private void initViews() {
            btnBack = findViewById(R.id.btnBack);
            tvAcceptanceMainDesc = findViewById(R.id.tvAcceptanceMainDesc);
            tvServiceTypeName = findViewById(R.id.tvServiceTypeName);
            tvServiceQuantityValue = findViewById(R.id.tvServiceQuantityValue);
            tvPaymentStatusLabel = findViewById(R.id.tvPaymentStatusLabel);
            tvOrderSerialAndDate = findViewById(R.id.tvOrderSerialAndDate);
            btnNavigateToPayment = findViewById(R.id.btnNavigateToPayment);
            btnCancelCurrentOrder = findViewById(R.id.btnCancelCurrentOrder);
        }

        private void displayOrderDetails() {
            // يمكنك تغيير هذه القيم بناءً على البيانات القادمة من الواجهة السابقة
            tvServiceTypeName.setText("توصيل مياه صالحة للشرب");
            tvServiceQuantityValue.setText("500 لتر");
            tvPaymentStatusLabel.setText("حالة الطلب: بانتظار الدفع");
            tvOrderSerialAndDate.setText("رقم الطلب: ERW-98421#  •  التاريخ: 24 مايو 2026");
        }

        private void setupClickListeners() {

            btnBack.setOnClickListener(v -> {
                finish();
            });

            // زر الانتقال إلى الدفع
            btnNavigateToPayment.setOnClickListener(v -> {
                // هنا يمكنك الانتقال لواجهة الدفع الفعلية عند برمجتها:
                // Intent intent = new Intent(OrderStatusActivity.this, PaymentActivity.class);
                // startActivity(intent);

                Toast.makeText(this, "جاري فتح بوابة الدفع الإلكتروني...", Toast.LENGTH_SHORT).show();
            });

            // زر إلغاء الطلب الحالي
            btnCancelCurrentOrder.setOnClickListener(v -> {
                // يمكنك هنا إضافة مربع حوار (AlertDialog) للتأكيد قبل الإلغاء
                Toast.makeText(this, "تم إرسال طلب إلغاء الحجز للمزود", Toast.LENGTH_SHORT).show();

                // كمثال: إغلاق الواجهة بعد الإلغاء
                // finish();
            });
        }
    }

