package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class activity_initiative_register extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etIdNumber, etPhone;
    private Spinner spLocation;
    private CardView btnRegister;
    private TextView btnLogin;

    // الفايربيس
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiative_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etIdNumber = findViewById(R.id.etIdNumber);
        etPhone = findViewById(R.id.etPhone);
        spLocation = findViewById(R.id.spLocation);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        setupLocationSpinner();

        // عند الضغط على زر إنشاء الحساب
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> performFirebaseRegister());
        }

        // عند الضغط على "تسجيل الدخول" للانتقال للواجهة الأخرى
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(activity_initiative_register.this, LoginActivity.class));
                finish();
            });
        }
    }

    private void setupLocationSpinner() {
        String[] locations = {"غزة", "الوسطى", "خانيونس", "رفح", "الشمال"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spLocation != null) {
            spLocation.setAdapter(adapter);
        }
    }

    private void performFirebaseRegister() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String idNumber = etIdNumber.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String location = spLocation.getSelectedItem() != null ? spLocation.getSelectedItem().toString() : "";

        // التحقق من المدخلات
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(idNumber) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "الرجاء تعبئة كافة الحقول المطلوبة", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "كلمة المرور يجب ألا تقل عن 6 خانات", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idNumber.length() != 9) {
            Toast.makeText(this, "رقم الهوية يجب أن يتكون من 9 أرقام", Toast.LENGTH_SHORT).show();
            return;
        }

        // تعطيل الزر لتجنب النقرات المتكررة أثناء المعالجة
        btnRegister.setEnabled(false);

        // 1. إنشاء المستخدم في Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = mAuth.getCurrentUser().getUid();

                    // تحضير بيانات المستخدم لحفظها في Cloud Firestore
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", uid);
                    userMap.put("fullName", fullName);
                    userMap.put("email", email);
                    userMap.put("idNumber", idNumber);
                    userMap.put("phone", phone);
                    userMap.put("location", location);
                    userMap.put("role", "initiator"); // تحديد رتبته كمبادر تلقائياً

                    // 2. تخزين البيانات الإضافية في Firestore في جدول الـ users
                    db.collection("users").document(uid)
                            .set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(activity_initiative_register.this, "✅ تم إنشاء الحساب بنجاح!", Toast.LENGTH_SHORT).show();
                                // الانتقال للواجهة الرئيسية بعد التسجيل بنجاح
                                Intent intent = new Intent(activity_initiative_register.this, InitiatorDashboardActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnRegister.setEnabled(true);
                                Toast.makeText(activity_initiative_register.this, "⚠️ تم إنشاء الحساب ولكن فشل حفظ البيانات: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    // عرض تفاصيل الخطأ القادم من السيرفر مباشرة لمعرفة السبب بدقة
                    Toast.makeText(activity_initiative_register.this, "❌ فشل التسجيل: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                });
    }
}