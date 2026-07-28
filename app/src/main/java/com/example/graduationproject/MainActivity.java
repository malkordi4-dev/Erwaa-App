package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Splash screen: الانتظار لمدة 3 ثوانٍ
        new Handler(Looper.getMainLooper()).postDelayed(this::checkNavigationLogic, 3000);
    }

    private void checkNavigationLogic() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("isFirstTime", true);

        // التحقق من حالة تسجيل الدخول
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent;
        if (currentUser != null) {
            // مستخدم مسجل -> اذهب للخريطة
            intent = new Intent(MainActivity.this, MapExplorerActivity.class);
        } else if (isFirstTime) {
            // أول مرة يفتح التطبيق -> اذهب لشاشات Onboarding
            intent = new Intent(MainActivity.this, OnboardingActivity.class);
        } else {
            // غير مسجل دخول -> نذهب للخريطة أيضاً (كضيف) بناءً على طلبك لرؤية البيانات بدون شرط
            intent = new Intent(MainActivity.this, MapExplorerActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
