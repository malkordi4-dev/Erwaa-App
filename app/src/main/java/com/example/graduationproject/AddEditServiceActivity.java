package com.example.graduationproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddEditServiceActivity extends AppCompatActivity {

    private TextInputEditText etName, etDescription;
    private EditText etPriceLitre, etPriceCup;
    private ImageView btnBack, imgPreview1;
    private TextView tvHeaderTitle, tvServiceName, tvServiceId;
    private MaterialButton btnSave;
    private LinearLayout btnUploadImage;
    private SwitchMaterial switchAvailability;
    private MaterialCardView cardPreview1;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;

    private String editServiceId;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (cardPreview1 != null) {
                        cardPreview1.setVisibility(View.VISIBLE);
                        imgPreview1.setImageURI(uri);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editServiceId = getIntent().getStringExtra("service_id");

        if (editServiceId != null) {
            setContentView(R.layout.activity_edit_service);
        } else {
            setContentView(R.layout.activity_add_service);
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("service_images");

        initViews();

        if (editServiceId != null) {
            loadServiceForEdit(editServiceId);
        } else {
            if (tvHeaderTitle != null) tvHeaderTitle.setText("إضافة خدمة جديدة");
        }

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (btnUploadImage != null) btnUploadImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveService());
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        etName = findViewById(R.id.etServiceName);
        etDescription = findViewById(R.id.etServiceDescription);
        etPriceLitre = findViewById(R.id.etPriceLitre);
        etPriceCup = findViewById(R.id.etPriceCup);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imgPreview1 = findViewById(R.id.imgPreview1);
        cardPreview1 = findViewById(R.id.cardPreview1);

        if (editServiceId != null) {
            btnSave = findViewById(R.id.btnSaveEdits);
            switchAvailability = findViewById(R.id.switchAvailability);
            tvServiceName = findViewById(R.id.tvServiceName);
            tvServiceId = findViewById(R.id.tvServiceId);
        } else {
            btnSave = findViewById(R.id.btnSaveService);
        }
    }

    private void loadServiceForEdit(String serviceId) {
        db.collection("services").document(serviceId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    ServiceModel s = doc.toObject(ServiceModel.class);
                    if (s == null) return;
                    if (tvServiceName != null) tvServiceName.setText(s.getNameAr());
                    if (tvServiceId != null) tvServiceId.setText("معرف الخدمة: #" + serviceId.substring(0, Math.min(6, serviceId.length())));
                    if (etPriceLitre != null) etPriceLitre.setText(String.valueOf(s.getPrice()));
                    if (etPriceCup != null) etPriceCup.setText(String.valueOf(s.getPriceCup()));
                    if (switchAvailability != null) switchAvailability.setChecked(s.isActive());
                });
    }

    private void saveService() {
        String priceLitreStr = etPriceLitre != null ? etPriceLitre.getText().toString().trim() : "";
        String priceCupStr = etPriceCup != null ? etPriceCup.getText().toString().trim() : "";

        if (TextUtils.isEmpty(priceLitreStr)) {
            if (etPriceLitre != null) etPriceLitre.setError("السعر مطلوب");
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("جاري الحفظ...");

        db.collection("providers").document(mAuth.getUid()).get().addOnSuccessListener(providerDoc -> {
            Map<String, Object> data = new HashMap<>();
            data.put("price", Double.parseDouble(priceLitreStr));
            data.put("priceCup", TextUtils.isEmpty(priceCupStr) ? 0.0 : Double.parseDouble(priceCupStr));
            data.put("status", "pending");
            
            if (providerDoc.exists()) {
                // حفظ كافة بيانات المزود بمسميات تتوافق مع ServiceModel
                data.put("provider_name", providerDoc.getString("business_name"));
                data.put("provider_type", providerDoc.getString("provider_type"));
                data.put("provider_phone", providerDoc.getString("phone"));
                data.put("provider_id_number", providerDoc.getString("id_number"));
                data.put("municipality_code", providerDoc.getString("municipality_code"));
                data.put("region", providerDoc.getString("location_name"));
                data.put("latitude", providerDoc.getDouble("current_lat"));
                data.put("longitude", providerDoc.getDouble("current_lng"));
                data.put("provider_email", mAuth.getCurrentUser().getEmail());
            }

            if (editServiceId != null) {
                if (switchAvailability != null) data.put("isActive", switchAvailability.isChecked());
                db.collection("services").document(editServiceId).update(data)
                        .addOnSuccessListener(v -> {
                            Toast.makeText(this, "تم تحديث الخدمة وبانتظار موافقة الأدمن", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            } else {
                String name = etName != null ? etName.getText().toString().trim() : "";
                data.put("name_ar", name);
                data.put("description_ar", etDescription != null ? etDescription.getText().toString().trim() : "");
                data.put("provider_id", mAuth.getUid());
                data.put("isActive", false);
                data.put("created_at", com.google.firebase.Timestamp.now());
                
                db.collection("services").add(data)
                        .addOnSuccessListener(v -> {
                            Toast.makeText(this, "تمت إضافة الخدمة وبانتظار موافقة الأدمن", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            }
        }).addOnFailureListener(e -> {
            btnSave.setEnabled(true);
            btnSave.setText("حفظ");
            Toast.makeText(this, "خطأ في الاتصال", Toast.LENGTH_SHORT).show();
        });
    }
}
