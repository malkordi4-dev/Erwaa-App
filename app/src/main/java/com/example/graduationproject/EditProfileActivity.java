package com.example.graduationproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions; // 🌟 استيراد الميزة الذكية لدمج البيانات وإنشائها

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileDebug";

    // كود الطلب لاختيار صورة من المعرض
    private static final int PICK_IMAGE_REQUEST = 100;

    private ImageView btnBack;
    private ShapeableImageView ivUserAvatar;
    private MaterialCardView btnChangePhoto;
    private TextInputEditText etFullName, etPhoneNumber, etEmail, etAddress;

    private View btnSaveChanges;
    private Button btnCancel;

    private Uri selectedImageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            Toast.makeText(this, "الرجاء تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadCurrentUserData();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadCurrentUserData() {
        // فحص كلا الكولكشنز المحتملة (users أو initiators) لضمان جلب البيانات أياً كان نوع الحساب
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        fillFields(documentSnapshot.getData());
                    } else {
                        // محاولة البحث البديل في جدول المبادرين
                        db.collection("initiators").document(currentUserId)
                                .get()
                                .addOnSuccessListener(initiatorSnapshot -> {
                                    if (initiatorSnapshot.exists()) {
                                        fillFields(initiatorSnapshot.getData());
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "خطأ أثناء تحميل البيانات الشخصية: " + e.getMessage());
                    Toast.makeText(this, "فشل جلب البيانات من الخادم", Toast.LENGTH_SHORT).show();
                });
    }

    private void fillFields(Map<String, Object> data) {
        if (data == null) return;

        // فحص مرن لاسم المستخدم
        String name = (String) data.get("name");
        if (name == null) name = (String) data.get("userName");
        if (name == null) name = (String) data.get("fullName");

        // فحص مرن لرقم الهاتف
        String phone = (String) data.get("phone");
        if (phone == null) phone = (String) data.get("phoneNumber");

        String email = (String) data.get("email");
        String address = (String) data.get("address");

        if (etFullName != null && name != null) etFullName.setText(name);
        if (etPhoneNumber != null && phone != null) etPhoneNumber.setText(phone);
        if (etEmail != null && email != null) etEmail.setText(email);
        if (etAddress != null && address != null) etAddress.setText(address);
    }

    private void setupClickListeners() {
        // زر الرجوع في الهيدر العلوي
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // زر تغيير الصورة الشخصية
        if (btnChangePhoto != null) {
            btnChangePhoto.setOnClickListener(v -> openGallery());
        }
        if (ivUserAvatar != null) {
            ivUserAvatar.setOnClickListener(v -> openGallery());
        }

        // زر حفظ التعديلات الفعال
        if (btnSaveChanges != null) {
            btnSaveChanges.setOnClickListener(v -> {
                Log.d(TAG, "تم الضغط على زر حفظ التعديلات");
                performSaveChanges();
            });
        }

        // زر إلغاء التعديل
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                Toast.makeText(this, "تم إلغاء التعديلات", Toast.LENGTH_SHORT).show();
                finish();
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
            if (ivUserAvatar != null) {
                ivUserAvatar.setImageURI(selectedImageUri);
            }
            Toast.makeText(this, "تم اختيار الصورة بنجاح", Toast.LENGTH_SHORT).show();
        }
    }

    private void performSaveChanges() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phoneNumber = etPhoneNumber.getText() != null ? etPhoneNumber.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("الاسم بالكامل مطلوب");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError("رقم الجوال مطلوب");
            etPhoneNumber.requestFocus();
            return;
        }

        if (phoneNumber.length() < 9) {
            etPhoneNumber.setError("يرجى إدخال رقم جوال صحيح");
            etPhoneNumber.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("صيغة البريد الإلكتروني غير صحيحة");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError("العنوان مطلوب");
            etAddress.requestFocus();
            return;
        }

        // حفظ التغييرات الحقيقية في الفايربيس
        saveDataToFirebase(fullName, phoneNumber, email, address, selectedImageUri);
    }

    private void saveDataToFirebase(String name, String phone, String email, String address, Uri imageUri) {
        if (btnSaveChanges != null) {
            btnSaveChanges.setEnabled(false); // تعطيل مؤقت لمنع التكرار
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", name);
        updatedData.put("userName", name);
        updatedData.put("fullName", name);
        updatedData.put("phone", phone);
        updatedData.put("phoneNumber", phone);
        updatedData.put("email", email);
        updatedData.put("address", address);

        if (imageUri != null) {
            updatedData.put("profileImageUrl", imageUri.toString());
        }

        // نستخدم .set() مع SetOptions.merge() بدلاً من .update() لتجنب خطأ عدم وجود المستند
        db.collection("users").document(currentUserId)
                .set(updatedData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "🎉 تم تحديث بيانات ملفك الشخصي بنجاح!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // في حال كان الحساب يتبع لمجموعة المبادرين "initiators" يتم الحفظ بنفس الطريقة الآمنة
                    db.collection("initiators").document(currentUserId)
                            .set(updatedData, SetOptions.merge())
                            .addOnSuccessListener(aVoidInner -> {
                                Toast.makeText(this, "🎉 تم تحديث بيانات ملفك الشخصي بنجاح!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(eInner -> {
                                if (btnSaveChanges != null) {
                                    btnSaveChanges.setEnabled(true);
                                }
                                Log.e(TAG, "فشل حفظ التعديلات نهائياً: " + eInner.getMessage());
                                Toast.makeText(this, "حدث خطأ أثناء حفظ التعديلات", Toast.LENGTH_SHORT).show();
                            });
                });
    }
}