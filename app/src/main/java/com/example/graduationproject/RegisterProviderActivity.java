package com.example.graduationproject;

import android.content.Intent;
import android.graphics.Color;
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

    private EditText etFullName, etEmail, etPassword, etIdNumber, etPhone, etMunicipalityCode;
    private TextView tvRegion;
    private CardView cardLocalStation, cardWell, cardTruck, btnSubmit;
    
    private String selectedRegion = "";
    private String selectedEquipment = "محطة محلية"; // Default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_provider);

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etIdNumber = findViewById(R.id.etIdNumber);
        etPhone = findViewById(R.id.etPhone);
        etMunicipalityCode = findViewById(R.id.etMunicipalityCode);
        tvRegion = findViewById(R.id.tvRegion);
        
        cardLocalStation = findViewById(R.id.cardLocalStation);
        cardWell = findViewById(R.id.cardWell);
        cardTruck = findViewById(R.id.cardTruck);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Back Button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Region Selection
        tvRegion.setOnClickListener(v -> showRegionDialog());

        // Equipment Selection
        cardLocalStation.setOnClickListener(v -> selectEquipment("محطة محلية", cardLocalStation));
        cardWell.setOnClickListener(v -> selectEquipment("بئر", cardWell));
        cardTruck.setOnClickListener(v -> selectEquipment("صهريج", cardTruck));

        // Initial selection state
        selectEquipment("محطة محلية", cardLocalStation);

        // Submit Button
        btnSubmit.setOnClickListener(v -> performSignUp());

        // Login Link
        TextView btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterProviderActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void showRegionDialog() {
        String[] regions = {"غزة - الرمال", "غزة - النصر", "خانيونس", "رفح", "دير البلح", "شمال غزة"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اختر منطقة التوزيع");
        builder.setItems(regions, (dialog, which) -> {
            selectedRegion = regions[which];
            tvRegion.setText(selectedRegion);
            tvRegion.setTextColor(Color.BLACK);
        });
        builder.show();
    }

    private void selectEquipment(String type, CardView selectedCard) {
        selectedEquipment = type;
        
        // Reset colors
        cardLocalStation.setCardBackgroundColor(Color.WHITE);
        cardWell.setCardBackgroundColor(Color.WHITE);
        cardTruck.setCardBackgroundColor(Color.WHITE);
        
        // Highlight selected
        selectedCard.setCardBackgroundColor(Color.parseColor("#E0F2FE"));
    }

    private void performSignUp() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String idNumber = etIdNumber.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String municipalityCode = etMunicipalityCode.getText().toString().trim();

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || idNumber.isEmpty() || selectedRegion.isEmpty()) {
            Toast.makeText(this, "يرجى ملء كافة البيانات الأساسية واختيار المنطقة", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare User Data
        Map<String, Object> userData = new HashMap<>();
        userData.put("full_name", fullName);
        userData.put("phone", phone);
        userData.put("id_number", idNumber);
        userData.put("municipality_code", municipalityCode);
        userData.put("location_name", selectedRegion);
        userData.put("equipment_type", selectedEquipment);
        userData.put("is_provider", true);
        userData.put("status", "pending_review");

        AuthRequest authRequest = new AuthRequest(email, password, userData);
        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);

        api.signUp(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    showSuccessDialog();
                } else {
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterProviderActivity.this, "خطأ في الاتصال: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("تم تقديم الطلب")
                .setMessage("تم استلام طلب تسجيلك بنجاح. سيتم مراجعة كود البلدية والبيانات خلال 48 ساعة.")
                .setPositiveButton("موافق", (dialog, which) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void handleError(Response<AuthResponse> response) {
        try {
            String errorBody = response.errorBody().string();
            SupabaseError error = new Gson().fromJson(errorBody, SupabaseError.class);
            Toast.makeText(this, error.getDisplayMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "فشل التسجيل، تأكد من صحة البيانات", Toast.LENGTH_SHORT).show();
        }
    }
}
