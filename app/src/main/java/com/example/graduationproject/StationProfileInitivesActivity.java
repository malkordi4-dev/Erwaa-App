package com.example.graduationproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StationProfileInitivesActivity extends AppCompatActivity {
    private static final String TAG = "StationProfileDebug";
    private static final int PICK_IMAGE_REQUEST = 200;

    // عناصر واجهة المستخدم
    private ImageView btnNotifications;
    private ShapeableImageView imgProfileBig;
    private MaterialCardView btnEditProfileImage;
    private TextView tvStationName, tvStationType, tvStationAddress, tvStationPhone, tvStationCapacity, tvDocStatus, tvWorkHours;
    private RelativeLayout btnLocationClick, btnPhoneClick;
    private MaterialCardView btnDocuments, btnWorkHoursCard, btnAccountSettings, btnLogout;

    // متغيرات الفايربيس والحالة المحلية
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile); // تأكد أن الاسم يطابق ملف الـ XML الخاص بك

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        loadStationData();
        setupClickListeners();
    }

    private void initViews() {
        btnNotifications = findViewById(R.id.btnNotifications);
        imgProfileBig = findViewById(R.id.imgProfileBig);
        btnEditProfileImage = findViewById(R.id.btnEditProfileImage);

        tvStationName = findViewById(R.id.tvStationName);
        tvStationType = findViewById(R.id.tvStationType);
        tvStationAddress = findViewById(R.id.tvStationAddress);
        tvStationPhone = findViewById(R.id.tvStationPhone);
        tvStationCapacity = findViewById(R.id.tvStationCapacity);
        tvDocStatus = findViewById(R.id.tvDocStatus);
        tvWorkHours = findViewById(R.id.tvWorkHours);

        btnLocationClick = findViewById(R.id.btnLocationClick);
        btnPhoneClick = findViewById(R.id.btnPhoneClick);

        btnDocuments = findViewById(R.id.btnDocuments);
        btnWorkHoursCard = findViewById(R.id.btnWorkHours); // CardView المخصص لساعات العمل
        btnAccountSettings = findViewById(R.id.btnAccountSettings);
        btnLogout = findViewById(R.id.btnLogout);
    }

    /**
     * 🌟 جلب بيانات المحطة ديناميكياً من مستند Firestore المخصص للمستخدم الحالي
     */
    private void loadStationData() {
        // نبحث داخل مجموعة "stations" أو "users" بناءً على الهيكل التنظيمي لمشروعك
        db.collection("stations").document(currentUserId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "فشل جلب بيانات المحطة بشكل حي: " + error.getMessage());
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("stationName");
                        if (name == null) name = documentSnapshot.getString("name");

                        String type = documentSnapshot.getString("stationType");
                        String address = documentSnapshot.getString("address");
                        String phone = documentSnapshot.getString("phone");
                        String capacity = documentSnapshot.getString("capacity");
                        String docStatus = documentSnapshot.getString("documentStatus");
                        String workHours = documentSnapshot.getString("workHours");

                        // تحديث النصوص في الواجهة
                        if (name != null) tvStationName.setText(name);
                        if (type != null) tvStationType.setText(type);
                        if (address != null) tvStationAddress.setText(address);
                        if (phone != null) tvStationPhone.setText(phone);
                        if (capacity != null) tvStationCapacity.setText(capacity);
                        if (docStatus != null) tvDocStatus.setText(docStatus);
                        if (workHours != null) tvWorkHours.setText(workHours);

                        // هنا يمكنك إدراج كود تحميل الصورة إذا كانت مكتبة Glide أو Picasso مضافة
                        // String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                    }
                });
    }

    private void setupClickListeners() {
        // الإشعارات العلوية
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(this, UserNotificationActivity.class);
                startActivity(intent);
            });
        }

        // اختيار صورة جديدة وتحديثها
        if (btnEditProfileImage != null) {
            btnEditProfileImage.setOnClickListener(v -> openGallery());
        }
        if (imgProfileBig != null) {
            imgProfileBig.setOnClickListener(v -> openGallery());
        }

        // الضغط على العنوان لفتح خرائط جوجل بمعاينة الموقع الجغرافي
        if (btnLocationClick != null) {
            btnLocationClick.setOnClickListener(v -> {
                String address = tvStationAddress.getText().toString();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(this, "تطبيق خرائط جوجل غير متوفر", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // الضغط على الهاتف للاتصال المباشر بالمحطة
        if (btnPhoneClick != null) {
            btnPhoneClick.setOnClickListener(v -> {
                String phoneNumber = tvStationPhone.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            });
        }

        // التفاعل مع الوثائق المعتمدة
        if (btnDocuments != null) {
            btnDocuments.setOnClickListener(v -> {
                Toast.makeText(this, "جاري استعراض الوثائق والمستندات القانونية للمحطة...", Toast.LENGTH_SHORT).show();
            });
        }

        // التفاعل مع ساعات العمل
        if (btnWorkHoursCard != null) {
            btnWorkHoursCard.setOnClickListener(v -> {
                Toast.makeText(this, "ساعات عمل المحطة: " + tvWorkHours.getText(), Toast.LENGTH_SHORT).show();
            });
        }

        // الانتقال لصفحة الإعدادات اللوجستية للحساب
        if (btnAccountSettings != null) {
            btnAccountSettings.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsInitiatorsActivity.class);
                startActivity(intent);
            });
        }

        // تسجيل الخروج الفعلي والآمن من المنصة والعودة لصفحة تسجيل الدخول
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(StationProfileInitivesActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(this, "تم تسجيل الخروج بنجاح 👋", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgProfileBig.setImageURI(selectedImageUri);

            // تحديث رابط الصورة محلياً في الفايربيس لضمان الحفظ المباشر
            updateProfileImageInFirestore(selectedImageUri.toString());
        }
    }

    private void updateProfileImageInFirestore(String uriString) {
        Map<String, Object> update = new HashMap<>();
        update.put("profileImageUrl", uriString);

        db.collection("stations").document(currentUserId)
                .update(update)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "تم تحديث الصورة بنجاح! ✅", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e(TAG, "فشل تحديث الصورة: " + e.getMessage()));
    }
}