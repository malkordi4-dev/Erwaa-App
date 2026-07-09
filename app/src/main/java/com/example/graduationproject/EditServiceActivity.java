package com.example.graduationproject;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditServiceActivity extends AppCompatActivity {

    private ImageView btnBack, imgServiceCard;
    private TextView tvServiceName, tvServiceId;
    private EditText etPriceLitre, etPriceCup;
    private SwitchMaterial switchAvailability;
    private MaterialButton btnSaveEdits;
    private View btnUploadImage;
    private ImageView imgPreview1, imgPreview2;
    private MaterialCardView cardPreview1, cardPreview2;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;

    private String serviceId;
    private ServiceModel currentService;
    private List<Uri> selectedImages = new ArrayList<>();
    private int currentImageSlot = 0;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && currentImageSlot < 2) {
                    selectedImages.add(uri);
                    if (currentImageSlot == 0) {
                        cardPreview1.setVisibility(View.VISIBLE);
                        imgPreview1.setImageURI(uri);
                    } else {
                        cardPreview2.setVisibility(View.VISIBLE);
                        imgPreview2.setImageURI(uri);
                    }
                    currentImageSlot++;
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("service_images");

        serviceId = getIntent().getStringExtra("service_id");
        if (serviceId == null) {
            finish();
            return;
        }

        initViews();
        loadServiceData();

        btnBack.setOnClickListener(v -> finish());
        btnUploadImage.setOnClickListener(v -> {
            if (currentImageSlot < 2) imagePickerLauncher.launch("image/*");
        });

        btnSaveEdits.setOnClickListener(v -> saveChanges());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgServiceCard = findViewById(R.id.imgServiceCard);
        tvServiceName = findViewById(R.id.tvServiceName);
        tvServiceId = findViewById(R.id.tvServiceId);
        etPriceLitre = findViewById(R.id.etPriceLitre);
        etPriceCup = findViewById(R.id.etPriceCup);
        switchAvailability = findViewById(R.id.switchAvailability);
        btnSaveEdits = findViewById(R.id.btnSaveEdits);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imgPreview1 = findViewById(R.id.imgPreview1);
        imgPreview2 = findViewById(R.id.imgPreview2);
        cardPreview1 = findViewById(R.id.cardPreview1);
        cardPreview2 = findViewById(R.id.cardPreview2);
    }

    private void loadServiceData() {
        db.collection("services").document(serviceId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                currentService = doc.toObject(ServiceModel.class);
                if (currentService != null) {
                    tvServiceName.setText(currentService.getNameAr());
                    tvServiceId.setText("معرف الخدمة: #" + serviceId.substring(0, 6));
                    etPriceLitre.setText(String.valueOf(currentService.getPrice()));
                    switchAvailability.setChecked(currentService.isActive());
                    // ملاحظة: هنا يمكن استخدام Glide لتحميل صورة الخدمة الحالية
                }
            }
        });
    }

    private void saveChanges() {
        String priceStr = etPriceLitre.getText().toString().trim();
        if (TextUtils.isEmpty(priceStr)) {
            etPriceLitre.setError("السعر مطلوب");
            return;
        }

        btnSaveEdits.setEnabled(false);
        btnSaveEdits.setText("جاري إرسال التعديلات...");

        Map<String, Object> updates = new HashMap<>();
        updates.put("price", Double.parseDouble(priceStr));
        
        // عند التعديل، نغير الحالة لتنتظر موافقة الأدمن مرة أخرى
        updates.put("status", "pending");
        updates.put("isActive", false); 

        if (selectedImages.isEmpty()) {
            applyUpdatesToFirestore(updates);
        } else {
            // رفع الصورة الجديدة أولاً
            StorageReference ref = storageRef.child(System.currentTimeMillis() + ".jpg");
            ref.putFile(selectedImages.get(0)).continueWithTask(task -> {
                if (!task.isSuccessful()) throw task.getException();
                return ref.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updates.put("imageUrl", task.getResult().toString());
                }
                applyUpdatesToFirestore(updates);
            });
        }
    }

    private void applyUpdatesToFirestore(Map<String, Object> updates) {
        db.collection("services").document(serviceId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "تم إرسال التعديلات للأدمن للمراجعة", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSaveEdits.setEnabled(true);
                    btnSaveEdits.setText("حفظ التعديلات");
                    Toast.makeText(this, "فشل الحفظ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
