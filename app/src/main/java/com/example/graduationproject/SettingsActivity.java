package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SwitchMaterial switchNotifications, switchDataSaver;
    private TextView btnLangEn, btnLangAr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        mAuth = FirebaseAuth.getInstance();

        // ربط العناصر
        ImageView btnBack = findViewById(R.id.btnMenu);
        MaterialCardView btnLogout = findViewById(R.id.btnLogout);
        RelativeLayout btnChangePassword = findViewById(R.id.btnChangePassword);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchDataSaver = findViewById(R.id.switchDataSaver);
        btnLangEn = findViewById(R.id.btnLangEn);
        btnLangAr = findViewById(R.id.btnLangAr);

        // العودة للواجهة السابقة
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // تسجيل الخروج الآمن من الفايربيس وتصفير الشاشات
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        // تغيير كلمة المرور عبر الفايربيس بشكل آمن
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

        // تغيير لغة التطبيق
        if (btnLangEn != null) btnLangEn.setOnClickListener(v -> setAppLocale("en"));
        if (btnLangAr != null) btnLangAr.setOnClickListener(v -> setAppLocale("ar"));

        setupSwitches();
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

    // 🌟 تحديث أزرار التنقل السفلي لتتطابق مع بقية شاشات التطبيق بشكل موحد وتفاعلي
    private void setupBottomNavigation() {
        View navDashboard = findViewById(R.id.nav_dashboard);
        View navInitiatives = findViewById(R.id.nav_initiatives);
        View navMap = findViewById(R.id.nav_map);
        View navWallet = findViewById(R.id.nav_wallet);

        if (navDashboard != null) {
            navDashboard.setOnClickListener(v -> {
                Intent intent = new Intent(this, InitiatorDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        if (navInitiatives != null) {
            navInitiatives.setOnClickListener(v -> {
                Intent intent = new Intent(this, InitiativesListActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (navMap != null) {
            navMap.setOnClickListener(v -> {
                Intent intent = new Intent(this, MapExplorerActivity.class);
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