package com.example.graduationproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

    public class StationProfileActivity extends AppCompatActivity {

        private ImageView btnNotifications;
        private ShapeableImageView imgProfileBig;
        private MaterialCardView btnEditProfileImage;
        private TextView tvStationName, tvStationType, tvStationAddress, tvStationPhone, tvStationCapacity, tvWorkHours, tvDocStatus;
        private RelativeLayout btnLocationClick, btnPhoneClick;
        private MaterialCardView btnDocuments, btnWorkHoursCard, btnAccountSettings, btnLogout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_profile);

            initViews();
            setupClickListeners();
        }

        private void initViews() {
            btnNotifications = findViewById(R.id.btnNotifications);
            imgProfileBig = findViewById(R.id.imgProfileBig);
            btnEditProfileImage = findViewById(R.id.btnEditProfileImage);

            // النصوص التعريفية للمحطة
            tvStationName = findViewById(R.id.tvStationName);
            tvStationType = findViewById(R.id.tvStationType);
            tvStationAddress = findViewById(R.id.tvStationAddress);
            tvStationPhone = findViewById(R.id.tvStationPhone);
            tvStationCapacity = findViewById(R.id.tvStationCapacity);
            tvWorkHours = findViewById(R.id.tvWorkHours);
            tvDocStatus = findViewById(R.id.tvDocStatus);

            // الحاويات التفاعلية
            btnLocationClick = findViewById(R.id.btnLocationClick);
            btnPhoneClick = findViewById(R.id.btnPhoneClick);
            btnDocuments = findViewById(R.id.btnDocuments);
            btnWorkHoursCard = findViewById(R.id.btnWorkHours); // الكارد الحاوي لساعات العمل
            btnAccountSettings = findViewById(R.id.btnAccountSettings);
            btnLogout = findViewById(R.id.btnLogout);
        }

        private void setupClickListeners() {
            // إدارة التنبيهات والإشعارات اللوجستية للمحطة
            btnNotifications.setOnClickListener(v ->
                    Toast.makeText(this, "عرض إشعارات المحطة وتحديثات الحصص المائية الخاصة بك", Toast.LENGTH_SHORT).show());

            // تعديل صورة المحطة الميدانية
            btnEditProfileImage.setOnClickListener(v ->
                    Toast.makeText(this, "جاري فتح المعرض لتحديث صورة المحطة الرسمية...", Toast.LENGTH_SHORT).show());

            // عند الضغط على العنوان: يمكن ربطه بخرائط جوجل
            btnLocationClick.setOnClickListener(v -> {
                String address = tvStationAddress.getText().toString();
                Toast.makeText(this, "جاري فتح موقع المحطة على الخريطة...", Toast.LENGTH_SHORT).show();
                 // Intent لفتح الخرائط تلقائياً:
                 Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                 Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                 startActivity(mapIntent);
            });

            // عند الضغط على الهاتف: إجراء اتصال فوري بالمحطة أو الصهريج
            btnPhoneClick.setOnClickListener(v -> {
                String phoneNumber = tvStationPhone.getText().toString();
                Toast.makeText(this, "جاري الاتصال بـ: " + phoneNumber, Toast.LENGTH_SHORT).show();
                 Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                 startActivity(callIntent);
            });

            // استعراض الوثائق والتراخيص المعتمدة لبئر المياه أو الصهريج
            btnDocuments.setOnClickListener(v ->
                    Toast.makeText(this, "جاري تحميل وفحص شهادات جودة المياه والتراخيص القانونية...", Toast.LENGTH_SHORT).show());

            // تعديل ساعات العمل واستقبال المبادرات
            btnWorkHoursCard.setOnClickListener(v ->
                    Toast.makeText(this, "تعديل أوقات العمل وجدول ضخ المياه اليومي", Toast.LENGTH_SHORT).show());

            // الانتقال لصفحة إعدادات الحساب المتقدمة
            btnAccountSettings.setOnClickListener(v ->
                    Toast.makeText(this, "جاري الانتقال لإعدادات الحساب وتغيير كلمة المرور...", Toast.LENGTH_SHORT).show());

            // تسجيل الخروج الآمن من المنصة
            btnLogout.setOnClickListener(v -> {
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(StationProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
