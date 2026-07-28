package com.example.graduationproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class WalletInitiatorsActivity extends AppCompatActivity{
        private MaterialCardView btnUserProfile;
        private ImageView btnNotification;
        private MaterialButton btnAddBudget;
        private TextView tvAvailableBalance, tvTotalFunding, tvTotalExpenses;

        private MaterialCardView cardTransaction1, cardTransaction2, cardTransaction3;
        private LinearLayout btnSupportSection;

        private LinearLayout navDashboard, navInitiatives, navMap, navWallet;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_provider_wallet);

            initViews();
            setupActions();
        }

        private void initViews() {
            btnUserProfile = findViewById(R.id.btnUserProfile);
            btnNotification = findViewById(R.id.btnNotification);
            btnAddBudget = findViewById(R.id.btnAddBudget);
            tvAvailableBalance = findViewById(R.id.tvAvailableBalance);
            tvTotalFunding = findViewById(R.id.tvTotalFunding);
            tvTotalExpenses = findViewById(R.id.tvTotalExpenses);

            // كروت سجل العمليات
            cardTransaction1 = findViewById(R.id.cardTransaction1);
            cardTransaction2 = findViewById(R.id.cardTransaction2);
            cardTransaction3 = findViewById(R.id.cardTransaction3);
            btnSupportSection = findViewById(R.id.btnSupportSection);

            // شريط الملاحة السفلي
            navDashboard = findViewById(R.id.nav_dashboard);
            navInitiatives = findViewById(R.id.nav_initiatives);
            navMap = findViewById(R.id.nav_map);
            navWallet = findViewById(R.id.nav_wallet);
        }

        private void setupActions() {
            // الضغط على الحساب الشخصي والنوتفكيشن
            btnUserProfile.setOnClickListener(v -> Toast.makeText(this, "فتح حساب المبادر الشخصي...", Toast.LENGTH_SHORT).show());
            btnNotification.setOnClickListener(v -> Toast.makeText(this, "عرض الإشعارات المالية للمبادرات", Toast.LENGTH_SHORT).show());

            // زر إضافة الميزانية وشحن المحفظة
            btnAddBudget.setOnClickListener(v -> {
                Toast.makeText(this, "جاري الانتقال لبوابة الإيداع وتخصيص الدعم...", Toast.LENGTH_SHORT).show();
            });

            // النقر على تفاصيل الحركات المالية في القائمة
            cardTransaction1.setOnClickListener(v -> Toast.makeText(this, "تفاصيل عملية: تمويل حي الرمال سقيا 4", Toast.LENGTH_SHORT).show());
            cardTransaction2.setOnClickListener(v -> Toast.makeText(this, "تفاصيل عملية: استلام دعم خارجي للمشروع", Toast.LENGTH_SHORT).show());
            cardTransaction3.setOnClickListener(v -> Toast.makeText(this, "تفاصيل عملية: فاتورة معدات الضخ اللوجستية", Toast.LENGTH_SHORT).show());

            // قسم الدعم والمساعدة
            btnSupportSection.setOnClickListener(v -> {
                Toast.makeText(this, "جاري فتح المحادثة الفورية مع دعم مساندة المبادرين...", Toast.LENGTH_SHORT).show();
            });

            // --- التحكم بأزرار التنقل السفلي لقسم المبادرين ---
            navDashboard.setOnClickListener(v -> Toast.makeText(this, "الانتقال إلى لوحة التحكم", Toast.LENGTH_SHORT).show());
            navInitiatives.setOnClickListener(v -> Toast.makeText(this, "عرض قائمة المبادرات الميدانية", Toast.LENGTH_SHORT).show());
            navMap.setOnClickListener(v -> Toast.makeText(this, "تحميل خريطة الاحتياج المائي الميدانية", Toast.LENGTH_SHORT).show());
            navWallet.setOnClickListener(v -> Toast.makeText(this, "أنت متواجد حالياً في المحفظة", Toast.LENGTH_SHORT).show());
        }
    }
