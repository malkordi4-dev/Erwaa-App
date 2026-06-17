package com.example.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    private EditText etEmail, etPassword;

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_EMAIL = "saved_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ImageView btnTogglePassword = findViewById(R.id.btnTogglePassword);
        View btnLogin = findViewById(R.id.btnLogin);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        TextView tvRegister = findViewById(R.id.tvSignUp);
        ImageView btnBack = findViewById(R.id.btnBack);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString(KEY_EMAIL, "");
        if (!savedEmail.isEmpty() && etEmail != null) {
            etEmail.setText(savedEmail);
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (btnTogglePassword != null) {
            btnTogglePassword.setOnClickListener(v -> {
                if (isPasswordVisible) {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                isPasswordVisible = !isPasswordVisible;
                etPassword.setSelection(etPassword.getText().length());
            });
        }

        if (tvForgotPassword != null) {
            tvForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            });
        }

        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, UserTypeActivity.class);
                startActivity(intent);
            });
        }

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> performLogin());
        }
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال البريد الإلكتروني وكلمة المرور", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthRequest request = new AuthRequest(email, password);
        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);

        api.signIn(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(KEY_EMAIL, email);
                    editor.putString(KEY_USER_ID, authResponse.getUser().getId());
                    editor.putString("access_token", authResponse.getAccessToken()); // Save access token for RLS
                    editor.putBoolean(KEY_IS_LOGGED_IN, true);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MapExplorerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "البريد الإلكتروني أو كلمة المرور غير صحيحة", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "خطأ في الاتصال بالسيرفر", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
