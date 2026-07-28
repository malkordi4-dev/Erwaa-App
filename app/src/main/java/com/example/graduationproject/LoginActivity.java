package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

// 🌟 استيراد مكتبة الفايربيس الخاصة بالمصادقة
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private CardView btnLoginSubmit;
    private TextView tvGoToRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Mapping to the correct IDs from activity_login.xml
        etLoginEmail = findViewById(R.id.etPhone);
        etLoginPassword = findViewById(R.id.etPassword);
        btnLoginSubmit = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvSignUp);

        if (btnLoginSubmit != null) {
            btnLoginSubmit.setOnClickListener(v -> performLoginWithFirebase());
        }

        // عند الضغط على الانتقال لإنشاء حساب جديد
        if (tvGoToRegister != null) {
            tvGoToRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, activity_initiative_register.class));
                finish(); // إغلاق الواجهة الحالية
            });
        }
    }

    // الدالة المعدلة للتحقق الفعلي عبر السيرفر
    private void performLoginWithFirebase() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "الرجاء إدخال البريد الإلكتروني وكلمة المرور", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "كلمة المرور يجب ألا تقل عن 6 خانات", Toast.LENGTH_SHORT).show();
            return;
        }

        // تعطيل الزر مؤقتاً أثناء التحقق لمنع النقرات المكررة
        btnLoginSubmit.setEnabled(false);

        // طلب تسجيل الدخول ومطابقة البيانات في لوحة الفايربيس
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // في حال كانت البيانات صحيحة 100%
                    Toast.makeText(LoginActivity.this, "✅ تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();

                    // حفظ حالة تسجيل الدخول في SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    prefs.edit()
                            .putBoolean("isLoggedIn", true)
                            .putBoolean("isFirstTime", false)
                            .apply();

                    // الانتقال إلى الواجهة الرئيسية (لوحة التحكم)
                    Intent intent = new Intent(LoginActivity.this, InitiatorDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    finish(); // إغلاق شاشة تسجيل الدخول نهائياً
                })
                .addOnFailureListener(e -> {
                    // في حال كان الإيميل غير موجود أو كلمة المرور خاطئة
                    btnLoginSubmit.setEnabled(true); // إعادة تفعيل الزر للمحاولة مجدداً
                    Toast.makeText(LoginActivity.this, "❌ خطأ في تسجيل الدخول: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
