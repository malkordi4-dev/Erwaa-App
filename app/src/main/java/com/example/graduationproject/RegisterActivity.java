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

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etPhone, etIdNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etIdNumber = findViewById(R.id.etIdNumber);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        CardView btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> performSignUp());
    }

    private void performSignUp() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String idNumber = etIdNumber.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || idNumber.isEmpty()) {
            Toast.makeText(this, "يرجى ملء كافة البيانات", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("full_name", fullName);
        userData.put("phone", phone);
        userData.put("id_number", idNumber);
        userData.put("is_provider", false);

        AuthRequest authRequest = new AuthRequest(email, password, userData);
        
        // تم التعديل هنا لتمرير (this)
        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);

        api.signUp(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showSuccessDialog();
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("Register", "Failure: " + t.getMessage());
                Toast.makeText(RegisterActivity.this, "خطأ في الاتصال بالسيرفر", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(RegisterActivity.this)
                .setTitle("تم التسجيل بنجاح")
                .setMessage("تم إنشاء حسابك بنجاح! يمكنك الآن تسجيل الدخول.")
                .setPositiveButton("تسجيل الدخول", (dialog, which) -> {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void handleError(Response<AuthResponse> response) {
        try {
            String errorBody = response.errorBody().string();
            SupabaseError error = new Gson().fromJson(errorBody, SupabaseError.class);
            Toast.makeText(RegisterActivity.this, error.getDisplayMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(RegisterActivity.this, "فشل إنشاء الحساب", Toast.LENGTH_SHORT).show();
        }
    }
}
