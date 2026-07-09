package com.example.graduationproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AddServiceActivity extends AppCompatActivity {

    private EditText etServiceName, etServiceDesc, etPriceLitre, etPriceCup;
    private ImageView btnBack, imgPreview1, imgPreview2;
    private MaterialCardView cardPreview1, cardPreview2;
    private View btnUploadImage;
    private MaterialButton btnSave;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;

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
        setContentView(R.layout.activity_add_service);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("service_images");

        initViews();

        btnBack.setOnClickListener(v -> finish());

        btnUploadImage.setOnClickListener(v -> {
            if (currentImageSlot < 2) {
                imagePickerLauncher.launch("image/*");
            } else {
                Toast.makeText(this, "يمكنك إضافة صورتين فقط", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> saveService());
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etServiceName = findViewById(R.id.etServiceName);
        etServiceDesc = findViewById(R.id.etServiceDescription);
        etPriceLitre = findViewById(R.id.etPriceLitre);
        etPriceCup = findViewById(R.id.etPriceCup);
        btnSave = findViewById(R.id.btnSaveService);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imgPreview1 = findViewById(R.id.imgPreview1);
        imgPreview2 = findViewById(R.id.imgPreview2);
        cardPreview1 = findViewById(R.id.cardPreview1);
        cardPreview2 = findViewById(R.id.cardPreview2);
    }

    private void saveService() {
        String name = etServiceName.getText().toString().trim();
        String desc = etServiceDesc.getText().toString().trim();
        String priceLitreStr = etPriceLitre.getText().toString().trim();
        String priceCupStr = etPriceCup.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etServiceName.setError("الاسم مطلوب");
            return;
        }
        if (TextUtils.isEmpty(priceLitreStr)) {
            etPriceLitre.setError("السعر مطلوب");
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("جاري الحفظ...");

        String uid = mAuth.getCurrentUser().getUid();

        ServiceModel service = new ServiceModel();
        service.setProviderId(uid);
        service.setNameAr(name);
        service.setDescriptionAr(desc);
        service.setPrice(Double.parseDouble(priceLitreStr));
        service.setPriceCup(TextUtils.isEmpty(priceCupStr) ? 0.0 : Double.parseDouble(priceCupStr));
        service.setStatus("pending");
        service.setActive(false);
        service.setCreatedAt(Timestamp.now());
        service.setProviderEmail(mAuth.getCurrentUser().getEmail());

        // جلب تفاصيل المزود لإدراجها في مستند الخدمة
        db.collection("providers").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        service.setProviderName(doc.getString("business_name"));
                        service.setProviderType(doc.getString("provider_type"));
                        service.setProviderPhone(doc.getString("phone"));
                        service.setProviderIdNumber(doc.getString("id_number"));
                        service.setMunicipalityCode(doc.getString("municipality_code"));
                        service.setRegion(doc.getString("location_name"));
                        
                        if (doc.contains("current_lat")) {
                            service.setLatitude(doc.getDouble("current_lat"));
                        }
                        if (doc.contains("current_lng")) {
                            service.setLongitude(doc.getDouble("current_lng"));
                        }
                    }
                    uploadImagesAndSave(service);
                })
                .addOnFailureListener(e -> uploadImagesAndSave(service));
    }

    private void uploadImagesAndSave(ServiceModel service) {
        if (selectedImages.isEmpty()) {
            saveToFirestore(service);
            return;
        }

        StorageReference ref = storageRef.child(System.currentTimeMillis() + ".jpg");
        ref.putFile(selectedImages.get(0))
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        service.setImageUrl(task.getResult().toString());
                    }
                    saveToFirestore(service);
                });
    }

    private void saveToFirestore(ServiceModel service) {
        db.collection("services").add(service)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "تم إرسال الخدمة للأدمن للمراجعة", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("إضافة الخدمة");
                    Toast.makeText(this, "فشل الحفظ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
