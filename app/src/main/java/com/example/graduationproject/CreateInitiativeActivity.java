package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateInitiativeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_LOCATION=1001;
    //  رقم كود فرعي لطلب = 1001;

    private EditText etTitle, etWaterAmount, etSearchProvider;
    private AutoCompleteTextView atvLocation;
    private TextView tvEstimatedCost;
    private MaterialButton btnSubmitInitiative;

    private MaterialButton btn5k, btn10k, btn25k;

    private RadioGroup radioGroupFunding;
    private RadioButton rbInternalFunding, rbCrowdFunding;
    private View layoutInternalFunding, layoutCrowdFunding;
    private String selectedFundingType = "تمويل داخلي / مؤسساتي";

    private MaterialCardView cardProvider1, cardProvider2;
    private TextView tvProvider1Name, tvProvider2Name;
    private String selectedProvider = "شركة مياه غزة المركزية صهريج البلدية";

    private FirebaseFirestore db;
    private final double PRICE_PER_LITER = 0.05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_initiative);

        db = FirebaseFirestore.getInstance();

        etTitle = findViewById(R.id.etInitiativeName);
        atvLocation = findViewById(R.id.spinner_target_district);
        etWaterAmount = findViewById(R.id.etWaterAmount);
        tvEstimatedCost = findViewById(R.id.tvEstimatedCost);
        btnSubmitInitiative = findViewById(R.id.btnPublishInitiative);
        etSearchProvider = findViewById(R.id.etSearchProvider);

        btn5k = findViewById(R.id.btn5k);
        btn10k = findViewById(R.id.btn10k);
        btn25k = findViewById(R.id.btn25k);

        radioGroupFunding = findViewById(R.id.radioGroupFunding);
        rbInternalFunding = findViewById(R.id.rbInternalFunding);
        rbCrowdFunding = findViewById(R.id.rbCrowdFunding);
        layoutInternalFunding = findViewById(R.id.layoutInternalFunding);
        layoutCrowdFunding = findViewById(R.id.layoutCrowdFunding);

        cardProvider1 = findViewById(R.id.cardProvider1);
        cardProvider2 = findViewById(R.id.cardProvider2);

        if (cardProvider1 != null) tvProvider1Name = cardProvider1.findViewById(R.id.tvProvider1Name);
        if (cardProvider2 != null) tvProvider2Name = cardProvider2.findViewById(R.id.tvProvider2Name);

        setupLocationDropdown();
        setupWaterAmountInput();
        setupQuickAmountButtons();
        setupFundingRadioLogic();
        setupProviderSelection();
        setupProviderSearchLogic();

        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        //  تشغيل دالة الحفظ والانتقال عند الضغط على الزر الرئيسي
        if (btnSubmitInitiative != null) {
            btnSubmitInitiative.setOnClickListener(v -> saveInitiativeToFirebase());
        }
    }

    //   لفتح واجهة الخريطة لتحديد الموقع بدلاً من كتابته
    private void setupLocationDropdown() {
        if (atvLocation == null) return;

        atvLocation.setFocusable(false);
        atvLocation.setClickable(true);

        atvLocation.setOnClickListener(v -> {
            Intent intent = new Intent(CreateInitiativeActivity.this, MapExplorerActivity.class);
            intent.putExtra("is_pick_mode", true);
            startActivityForResult(intent, REQUEST_CODE_PICK_LOCATION);
        });
    }

    //  استقبال النتيجة الراجعة من شاشة الخريطة وتعبئتها في الحقل
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_LOCATION && resultCode == RESULT_OK && data != null) {
            String selectedLocationName = data.getStringExtra("selected_location_name");
            if (selectedLocationName != null && atvLocation != null) {
                atvLocation.setText(selectedLocationName);
                Toast.makeText(this, "📍 تم تحديد الموقع: " + selectedLocationName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupWaterAmountInput() {
        if (etWaterAmount == null) return;
        etWaterAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateCost();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupQuickAmountButtons() {
        if (btn5k != null) btn5k.setOnClickListener(v -> etWaterAmount.setText("5000"));
        if (btn10k != null) btn10k.setOnClickListener(v -> etWaterAmount.setText("10000"));
        if (btn25k != null) btn25k.setOnClickListener(v -> etWaterAmount.setText("25000"));
    }

    private void setupFundingRadioLogic() {
        if (rbInternalFunding == null || rbCrowdFunding == null) return;

        if (layoutInternalFunding != null) {
            layoutInternalFunding.setOnClickListener(v -> {
                selectedFundingType = "تمويل داخلي / مؤسساتي";
                rbInternalFunding.setChecked(true);
                rbCrowdFunding.setChecked(false);
            });
        }
        rbInternalFunding.setOnClickListener(v -> {
            selectedFundingType = "تمويل داخلي / مؤسساتي";
            rbInternalFunding.setChecked(true);
            rbCrowdFunding.setChecked(false);
        });

        if (layoutCrowdFunding != null) {
            layoutCrowdFunding.setOnClickListener(v -> {
                selectedFundingType = "تمويل جماعي / تبرعات";
                rbCrowdFunding.setChecked(true);
                rbInternalFunding.setChecked(false);
            });
        }
        rbCrowdFunding.setOnClickListener(v -> {
            selectedFundingType = "تمويل جماعي / تبرعات";
            rbCrowdFunding.setChecked(true);
            rbInternalFunding.setChecked(false);
        });
    }

    private void setupProviderSelection() {
        if (cardProvider1 == null || cardProvider2 == null) return;

        cardProvider1.setOnClickListener(v -> {
            selectedProvider = "شركة مياه غزة المركزية صهريج البلدية";
            cardProvider1.setStrokeColor(android.graphics.Color.parseColor("#0069B4"));
            cardProvider2.setStrokeColor(android.graphics.Color.parseColor("#E2E8F0"));
        });

        cardProvider2.setOnClickListener(v -> {
            selectedProvider = "الوفاق لنقل المياه صهريج الجنوب";
            cardProvider2.setStrokeColor(android.graphics.Color.parseColor("#0069B4"));
            cardProvider1.setStrokeColor(android.graphics.Color.parseColor("#E2E8F0"));
        });
    }

    private void setupProviderSearchLogic() {
        if (etSearchProvider == null) return;

        etSearchProvider.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim().toLowerCase();

                if (cardProvider1 != null && tvProvider1Name != null) {
                    String name1 = tvProvider1Name.getText().toString().toLowerCase();
                    cardProvider1.setVisibility(name1.contains(query) ? View.VISIBLE : View.GONE);
                }

                if (cardProvider2 != null && tvProvider2Name != null) {
                    String name2 = tvProvider2Name.getText().toString().toLowerCase();
                    cardProvider2.setVisibility(name2.contains(query) ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void calculateCost() {
        if (tvEstimatedCost == null || etWaterAmount == null) return;
        String amountStr = etWaterAmount.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            tvEstimatedCost.setText("0.00 ILS");
            return;
        }
        try {
            int liters = Integer.parseInt(amountStr);
            double estimatedCost = liters * PRICE_PER_LITER;
            tvEstimatedCost.setText(String.format("%.2f ILS", estimatedCost));
        } catch (NumberFormatException e) {
            tvEstimatedCost.setText("0.00 ILS");
        }
    }

    private void saveInitiativeToFirebase() {
        if (etTitle == null || atvLocation == null || etWaterAmount == null) return;

        String title = etTitle.getText().toString().trim();
        String location = atvLocation.getText().toString().trim();
        String waterStr = etWaterAmount.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(location) || TextUtils.isEmpty(waterStr)) {
            Toast.makeText(this, "🔴 الرجاء ملء جميع الحقول واختيار الحي المستهدف", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int targetLiters = Integer.parseInt(waterStr);
            if (targetLiters <= 0) {
                Toast.makeText(this, "🔴 الكمية يجب أن تكون أكبر من صفر لتر", Toast.LENGTH_SHORT).show();
                return;
            }

            InitiativeModel newInitiative = new InitiativeModel("", title, location, targetLiters, 0, selectedFundingType);
            btnSubmitInitiative.setEnabled(false);

            db.collection("initiatives")
                    .add(newInitiative)
                    .addOnSuccessListener(documentReference -> {
                        String docId = documentReference.getId();
                        db.collection("initiatives").document(docId).update("id", docId);

                        Toast.makeText(CreateInitiativeActivity.this, "✅ تم إطلاق مبادرة التمويل بنجاح!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(CreateInitiativeActivity.this, InitiativesListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSubmitInitiative.setEnabled(true);
                        Toast.makeText(CreateInitiativeActivity.this, "❌ فشل الإرسال: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "🔴 الرجاء إدخال كمية لترات صحيحة", Toast.LENGTH_SHORT).show();
        }
    }
}