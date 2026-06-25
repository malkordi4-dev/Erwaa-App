package com.example.graduationproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.Map;

public class MapExplorerActivity extends AppCompatActivity {

    private static final String TAG = "MapExplorerDebug";
    private MapView map = null;
    private IMapController mapController;
    private EditText etSearch;
    private CardView bottomSheetCard;
    private BottomSheetBehavior<MaterialCardView> bottomSheetBehavior;
    private TextView tvLocationTitle, tvLocationAddress, tvNearestSource, login;
    private CardView Confirm1;
    
    private MaterialCardView btnFilterWell, btnFilterTruck, btnFilterStorage;
    private String currentFilter = null;
    private FirebaseFirestore db;

    private String selectedProviderId = "";
    private String selectedProviderName = "";
    private String selectedSourceType = "";
    private String selectedAddress = "";
    private double selectedLat = 0;
    private double selectedLng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map_explorer);

        db = FirebaseFirestore.getInstance();
        initViews();
        
        login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                fetchProviders(currentFilter, etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        bottomSheetBehavior = BottomSheetBehavior.from((MaterialCardView) bottomSheetCard);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);
            mapController = map.getController();
            mapController.setZoom(15.0);
            // مركز الخريطة الافتراضي (غزة - الرمال)
            mapController.setCenter(new GeoPoint(31.516, 34.448)); 
            setupMapEvents();
        }

        setupClickListeners();
        
        // عند الدخول للواجهة: يتم فحص البيانات وإظهار جميع المزودين تلقائياً
        checkAndAddSampleData();
    }

    private void checkAndAddSampleData() {
        db.collection("providers").limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && (task.getResult() == null || task.getResult().isEmpty())) {
                addSampleProvidersToFirestore();
            } else {
                // جلب كافة المزودين افتراضياً
                fetchProviders(null, null);
            }
        });

        db.collection("services").limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && (task.getResult() == null || task.getResult().isEmpty())) {
                addSampleServicesToFirestore();
            }
            displayServicesMessage();
        });
    }

    private void addSampleProvidersToFirestore() {
        String[] names = {
            "محطة الرمال المركزية", "بئر الشيخ رضوان", "صهريج حي النصر السريع", 
            "مزود مياه تل الهوا", "بئر الشجاعية الكبير", "محطة مياه الزيتون",
            "صهريج معسكر جباليا", "بئر خانيونس الرئيسي", "مستودع دير البلح", "صهريج رفح الحدودي"
        };
        String[] types = { "storage", "well", "truck", "well", "well", "storage", "truck", "well", "storage", "truck" };
        double[] lats = { 31.516, 31.538, 31.530, 31.498, 31.505, 31.492, 31.542, 31.345, 31.417, 31.285 };
        double[] lngs = { 34.448, 34.462, 34.455, 34.438, 34.482, 34.465, 34.492, 34.305, 34.350, 34.255 };
        String[] addresses = { "حي الرمال", "الشيخ رضوان", "حي النصر", "تل الهوا", "الشجاعية", "الزيتون", "جباليا", "خانيونس", "دير البلح", "رفح" };

        for (int i = 0; i < names.length; i++) {
            Map<String, Object> provider = new HashMap<>();
            provider.put("business_name", names[i]);
            provider.put("provider_type", types[i]);
            provider.put("current_lat", lats[i]);
            provider.put("current_lng", lngs[i]);
            provider.put("status", "نشط");
            provider.put("location_name", addresses[i]);
            db.collection("providers").add(provider);
        }
        // جلب البيانات بعد الإضافة
        new android.os.Handler().postDelayed(() -> fetchProviders(null, null), 1500);
    }

    private void addSampleServicesToFirestore() {
        String[][] services = {
            {"1", "طلب مياه لمرة واحدة", "صهريج مياه نقي يصلك فوراً إلى باب المنزل."},
            {"2", "اشتراك مياه شهري", "تعبئة دورية لخزانك (4 مرات شهرياً) بخصم 20%."},
            {"3", "مبادرة الطلب الجماعي", "وفر تكاليف النقل بالطلب المشترك مع الجيران."}
        };
        for (String[] s : services) {
            Map<String, Object> service = new HashMap<>();
            service.put("name_ar", s[1]);
            service.put("description_ar", s[2]);
            db.collection("services").document(s[0]).set(service);
        }
    }

    private void displayServicesMessage() {
        db.collection("services").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                StringBuilder sb = new StringBuilder("الخدمات المتوفرة حالياً:\n");
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String name = doc.getString("name_ar");
                    if (name != null) sb.append("🔹 ").append(name).append("\n");
                }
                Toast.makeText(MapExplorerActivity.this, sb.toString().trim(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initViews() {
        map = findViewById(R.id.map);
        login = findViewById(R.id.login);
        Confirm1 = findViewById(R.id.btnConfirm);
        bottomSheetCard = findViewById(R.id.bottomSheetCard);
        tvLocationTitle = findViewById(R.id.tvLocationTitle);
        tvLocationAddress = findViewById(R.id.tvLocationAddress);
        tvNearestSource = findViewById(R.id.tvNear);
        etSearch = findViewById(R.id.etSearch);
        btnFilterWell = findViewById(R.id.btnFilterWell);
        btnFilterTruck = findViewById(R.id.btnFilterTruck);
        btnFilterStorage = findViewById(R.id.btnFilterStorage);
    }

    private void setupClickListeners() {
        btnFilterWell.setOnClickListener(v -> handleFilterClick("well", btnFilterWell));
        btnFilterTruck.setOnClickListener(v -> handleFilterClick("truck", btnFilterTruck));
        btnFilterStorage.setOnClickListener(v -> handleFilterClick("storage", btnFilterStorage));

        findViewById(R.id.btnZoomIn).setOnClickListener(v -> mapController.zoomIn());
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> mapController.zoomOut());
        findViewById(R.id.btnMyLocation).setOnClickListener(v -> {
            mapController.animateTo(new GeoPoint(31.516, 34.448));
            mapController.setZoom(16.0);
            hideBottomCard();
        });

        Confirm1.setOnClickListener(v -> {
            if (selectedProviderId.isEmpty()) return;
            Intent intent = new Intent(this, ProviderDetailsActivity.class);
            intent.putExtra("provider_id", selectedProviderId);
            intent.putExtra("provider_name", selectedProviderName);
            intent.putExtra("source_type", selectedSourceType);
            intent.putExtra("address", selectedAddress);
            intent.putExtra("lat", selectedLat);
            intent.putExtra("lng", selectedLng);
            startActivity(intent);
        });
    }

    private void handleFilterClick(String type, MaterialCardView clickedCard) {
        if (type.equals(currentFilter)) {
            currentFilter = null;
            resetFilterUI();
            fetchProviders(null, null);
        } else {
            currentFilter = type;
            updateFilterUI(clickedCard);
            fetchProviders(type, null);
        }
    }

    private void updateFilterUI(MaterialCardView activeCard) {
        resetFilterUI();
        activeCard.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
        activeCard.setStrokeColor(Color.parseColor("#0069B4"));
        activeCard.setStrokeWidth(4);
    }

    private void resetFilterUI() {
        MaterialCardView[] cards = {btnFilterWell, btnFilterTruck, btnFilterStorage};
        for (MaterialCardView c : cards) {
            c.setCardBackgroundColor(Color.WHITE);
            c.setStrokeWidth(0);
        }
    }

    private void fetchProviders(String type, String searchQuery) {
        clearMap();
        Query query = db.collection("providers");
        if (type != null) {
            query = query.whereEqualTo("provider_type", type);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                StringBuilder foundProviders = new StringBuilder();
                int count = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId();
                    String businessName = document.getString("business_name");
                    String providerType = document.getString("provider_type");
                    String locName = document.getString("location_name");
                    Double lat = document.getDouble("current_lat");
                    Double lng = document.getDouble("current_lng");

                    if (businessName == null || lat == null || lng == null) continue;

                    if (searchQuery != null && !businessName.toLowerCase().contains(searchQuery.toLowerCase())) {
                        continue;
                    }

                    count++;
                    foundProviders.append("📍 ").append(businessName).append("\n");

                    int iconRes = R.drawable.water;
                    int color = Color.parseColor("#0069B4"); // Well - Blue
                    if ("truck".equals(providerType)) {
                        iconRes = R.drawable.truck;
                        color = Color.parseColor("#4CAF50"); // Truck - Green
                    } else if ("storage".equals(providerType)) {
                        iconRes = R.drawable.barrel;
                        color = Color.parseColor("#FF9800"); // Storage - Orange
                    }

                    addCustomMarker(id, new GeoPoint(lat, lng), businessName, locName, getArabicType(providerType), iconRes, color);
                }
                
                // إظهار الرسالة فقط عند الفلترة وليس عند التحميل الكلي
                if (type != null) {
                    if (count > 0) {
                        Toast.makeText(this, "تم العثور على مزودي خدمة " + getArabicType(type) + ":\n" + foundProviders.toString().trim(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "لا يوجد مزودين متاحين لخدمة " + getArabicType(type), Toast.LENGTH_SHORT).show();
                    }
                }
                map.invalidate();
            }
        });
    }

    private void addCustomMarker(String id, GeoPoint point, String title, String address, String sourceType, int iconRes, int color) {
        Marker marker = new Marker(map);
        marker.setPosition(point);
        // التمركز في المنتصف ليتناسب مع شكل البطاقة المربعة
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setInfoWindow(null);
        
        View markerView = LayoutInflater.from(this).inflate(R.layout.custom_marker_layout, null);
        ImageView iconView = markerView.findViewById(R.id.markerIcon);
        MaterialCardView container = markerView.findViewById(R.id.markerContainer);
        
        if (iconView != null) {
            iconView.setImageResource(iconRes);
            iconView.setColorFilter(color);
        }
        
        if (container != null) {
            container.setStrokeColor(color);
        }

        // قياس الواجهة ورسمها لتحويلها إلى Bitmap بجودة عالية وحجم كبير
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);
        
        marker.setIcon(new BitmapDrawable(getResources(), bitmap));
        marker.setOnMarkerClickListener((m, mapView) -> {
            selectedProviderId = id;
            selectedProviderName = title;
            selectedSourceType = sourceType;
            selectedAddress = address;
            selectedLat = point.getLatitude();
            selectedLng = point.getLongitude();
            showBottomCard(title, address, sourceType);
            mapController.animateTo(point);
            return true;
        });
        map.getOverlays().add(marker);
    }

    private void setupMapEvents() {
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override public boolean singleTapConfirmedHelper(GeoPoint p) { hideBottomCard(); return true; }
            @Override public boolean longPressHelper(GeoPoint p) { return false; }
        };
        map.getOverlays().add(0, new MapEventsOverlay(mReceive));
    }

    private String getArabicType(String type) {
        if ("well".equals(type)) return "بئر مياه 🚰";
        if ("truck".equals(type)) return "صهريج متنقل 🚛";
        if ("storage".equals(type)) return "مستودع مياه 🏗️";
        return "مزود خدمة";
    }

    private void clearMap() {
        if (map == null) return;
        map.getOverlays().clear();
        setupMapEvents();
        hideBottomCard();
    }

    private void showBottomCard(String title, String address, String sourceType) {
        if (tvLocationTitle != null) tvLocationTitle.setText(title);
        if (tvLocationAddress != null) tvLocationAddress.setText(address);
        if (tvNearestSource != null) tvNearestSource.setText(sourceType);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void hideBottomCard() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override public void onResume() { super.onResume(); if(map != null) map.onResume(); }
    @Override public void onPause() { super.onPause(); if(map != null) map.onPause(); }
}
