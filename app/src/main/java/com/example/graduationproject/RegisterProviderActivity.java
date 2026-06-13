package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterProviderActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etPhone, etIdNumber, etMunicipalityCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_provider);

        // ربط الحقول بشكل صحيح
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etIdNumber = findViewById(R.id.etIdNumber);
        etMunicipalityCode = findViewById(R.id.etMunicipalityCode);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView btnLogin = findViewById(R.id.btnLogin);
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                Intent intent = new Intent(RegisterProviderActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            });
        }

        CardView btnSubmit = findViewById(R.id.btnSubmit);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> performProviderSignUp());
        }
    }

    private void performProviderSignUp() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String idNumber = etIdNumber != null ? etIdNumber.getText().toString().trim() : "";
        String munCode = etMunicipalityCode.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || munCode.isEmpty()) {
            Toast.makeText(this, "يرجى ملء كافة البيانات المطلوبة لمزود الخدمة", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "جاري إنشاء حساب مزود الخدمة...", Toast.LENGTH_SHORT).show();

        AuthRequest authRequest = new AuthRequest(email, password);
        SupabaseApi api = SupbaseClient.getClient().create(SupabaseApi.class);

        api.signUp(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String userId = response.body().getUser().getId();
                    saveProviderProfile(userId, fullName, email, phone, idNumber, munCode, api);
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("RegisterProvider", "Failure: " + t.getMessage());
                Toast.makeText(RegisterProviderActivity.this, "خطأ في الاتصال بالسيرفر، تأكد من الإنترنت", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveProviderProfile(String userId, String fullName, String email, String phone, String idNumber, String munCode, SupabaseApi api) {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("id", userId);
        profileData.put("full_name", fullName);
        profileData.put("email", email);
        profileData.put("phone", phone);
        profileData.put("id_number", idNumber);
        profileData.put("municipality_code", munCode);
        profileData.put("is_provider", true);

        api.createProfile(profileData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                showSuccessDialog();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showSuccessDialog(); 
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(RegisterProviderActivity.this)
                .setTitle("تم إنشاء الحساب")
                .setMessage("تم تسجيل حساب المزود بنجاح! يمكنك الآن تسجيل الدخول.")
                .setPositiveButton("تسجيل الدخول", (dialog, which) -> {
                    Intent intent = new Intent(RegisterProviderActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void handleError(Response<AuthResponse> response) {
        try {
            String errorBody = response.errorBody().string();
            // طباعة الخطأ الأصلي في Logcat للمطور
            Log.e("RegisterProviderError", "Raw Error: " + errorBody);
            
            SupabaseError error = new Gson().fromJson(errorBody, SupabaseError.class);
            // عرض الرسالة المترجمة للعربية
            Toast.makeText(RegisterProviderActivity.this, error.getDisplayMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(RegisterProviderActivity.this, "فشل التسجيل، تأكد من البيانات", Toast.LENGTH_SHORT).show();
        }
    }
}
