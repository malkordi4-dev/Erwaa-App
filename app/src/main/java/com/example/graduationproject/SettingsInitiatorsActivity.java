package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsInitiatorsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RelativeLayout btnChangePassword, btnPrivacy, btnAbout;
    private LinearLayout btnLogout;
    private TextView btnLangEn, btnLangAr;
    private SwitchMaterial switchNotifications, switchDataSaver;

    private LinearLayout navDashboard, navNeedMap, navInitiators, navWallet;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        setupClickListeners();

        setupSwitches();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnMenu);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);

        btnLangEn = findViewById(R.id.btnLangEn);
        btnLangAr = findViewById(R.id.btnLangAr);

        switchNotifications = findViewById(R.id.switchNotifications);
        switchDataSaver = findViewById(R.id.switchDataSaver);

        navDashboard = findViewById(R.id.navDashboard);
        navNeedMap = findViewById(R.id.navNeedMap);
        navInitiators = findViewById(R.id.navInitiators);
        navWallet = findViewById(R.id.navWallet);
    }

    private void setupClickListeners() {
        // العودة للواجهة السابقة عند الضغط على زر السهم العلوي
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // تسجيل الخروج الآمن من الفايربيس
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(SettingsInitiatorsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // تغيير كلمة المرور عبر البريد الإلكتروني في الفايربيس بشكل آمن
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                    mAuth.sendPasswordResetEmail(user.getEmail())
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, "تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك الإلكتروني بنجاح ✅", Toast.LENGTH_LONG).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "فشل إرسال الرابط: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(this, "عذراً، هذا الحساب غير مرتبط ببريد إلكتروني صالح لإعادة التعيين.", Toast.LENGTH_LONG).show();
                }
            });
        }

        // سياسة الخصوصية
        if (btnPrivacy != null) {
            btnPrivacy.setOnClickListener(v -> {
                Toast.makeText(this, "عرض سياسة الخصوصية لمشروع إرواء...", Toast.LENGTH_SHORT).show();
                // هنا يمكنك الانتقال لـ Activity سياسة الخصوصية إذا كانت متوفرة لديك
            });
        }

        // حول التطبيق
        if (btnAbout != null) {
            btnAbout.setOnClickListener(v -> {
                Toast.makeText(this, "تطبيق إرواء - الإصدار 2.4.0", Toast.LENGTH_SHORT).show();
            });
        }

        // تغيير لغة التطبيق
        if (btnLangEn != null) btnLangEn.setOnClickListener(v -> setAppLocale("en"));
        if (btnLangAr != null) btnLangAr.setOnClickListener(v -> setAppLocale("ar"));

        // تهيئة مستمعات شريط التنقل السفلي الموحد
        setupBottomNavigation();
    }

    private void setAppLocale(String languageCode) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
        Toast.makeText(this, "جاري تغيير لغة التطبيق...", Toast.LENGTH_SHORT).show();
    }

    private void setupSwitches() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);

        if (switchNotifications != null) {
            switchNotifications.setChecked(prefs.getBoolean("notifications", true));
            switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("notifications", isChecked).apply();
            });
        }

        if (switchDataSaver != null) {
            switchDataSaver.setChecked(prefs.getBoolean("data_saver", false));
            switchDataSaver.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("data_saver", isChecked).apply();
            });
        }
    }

    //  تفعيل التنقل الفعلي المتوافق تماماً مع المخطط السفلي الجديد لواجهتك
    private void setupBottomNavigation() {
        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(this, InitiatorDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navNeedMap != null) {
            navNeedMap.setOnClickListener(v -> {
                Intent intent = new Intent(this, NeedMapActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navInitiators != null) {
            navInitiators.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProfileInitiatorsActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navWallet != null) {
            navWallet.setOnClickListener(v -> {
                Intent intent = new Intent(this, WalletActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }
}