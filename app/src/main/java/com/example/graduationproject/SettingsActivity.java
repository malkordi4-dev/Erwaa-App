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

        // العودة
        btnBack.setOnClickListener(v -> finish());

        // تسجيل الخروج
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // تغيير كلمة المرور
        btnChangePassword.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
                mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "تم إرسال رابط إعادة تعيين كلمة المرور إلى بريدك", Toast.LENGTH_LONG).show());
            }
        });

        // تغيير اللغة
        btnLangEn.setOnClickListener(v -> setAppLocale("en"));
        btnLangAr.setOnClickListener(v -> setAppLocale("ar"));

        setupSwitches();
        setupBottomNavigation();
    }

    private void setAppLocale(String languageCode) {
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocale);
        Toast.makeText(this, "جاري تغيير اللغة...", Toast.LENGTH_SHORT).show();
    }

    private void setupSwitches() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        
        switchNotifications.setChecked(prefs.getBoolean("notifications", true));
        switchDataSaver.setChecked(prefs.getBoolean("data_saver", false));

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications", isChecked).apply();
        });

        switchDataSaver.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("data_saver", isChecked).apply();
        });
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapExplorerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        findViewById(R.id.navWallet).setOnClickListener(v -> startActivity(new Intent(this, WalletActivity.class)));
        findViewById(R.id.navOrders).setOnClickListener(v -> startActivity(new Intent(this, My_Orders_Activity.class)));
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            // Already on Settings
        });
    }
}
