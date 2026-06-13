
package com.example.graduationproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class MapExplorerActivity extends AppCompatActivity {

    private MapView map = null;
    private IMapController mapController;
    private EditText etSearch;
    private CardView bottomSheetCard;
  // Changed from Button to View (CardView in XML)

    private BottomSheetBehavior<CardView> bottomSheetBehavior;
    private TextView tvLocationTitle, tvLocationAddress, tvNearestSource,move ,Confirm1 ;;
     // Changed from TextView to View (ImageView in XML)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // إعدادات مكتبة osmdroid للخرائط
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map_explorer);

        // ربط عناصر الواجهة المحدثة بالتصميم
        map = findViewById(R.id.map);
        move = findViewById(R.id.move1);
        Confirm1 = findViewById(R.id.btnConfirm1);
        bottomSheetCard = findViewById(R.id.bottomSheetCard);
        tvLocationTitle = findViewById(R.id.tvLocationTitle);
        tvLocationAddress = findViewById(R.id.tvLocationAddress);
        tvNearestSource = findViewById(R.id.tvNearestSource);
        etSearch = findViewById(R.id.etSearch);

        // إعداد الـ BottomSheetBehavior وإخفاء البطاقة المنزلقة في البداية تماماً
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetCard);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Navigation to ProviderDetailsActivity
        Confirm1.setOnClickListener(v -> {
            Intent intent = new Intent(MapExplorerActivity.this, ProviderDetailsActivity.class);
            startActivity(intent);
        });

        // Navigation to UserTypeActivity
        move.setOnClickListener(v -> {
            Intent intent = new Intent(MapExplorerActivity.this, UserTypeActivity.class);
            startActivity(intent);
        });

        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);
            mapController = map.getController();
            mapController.setZoom(12.0);
            mapController.setCenter(new GeoPoint(31.5017, 34.4578));

            // تفعيل أحداث الضغط على الخريطة
            setupMapEvents();
        }

        setupClickListeners();
        showWaterPoints(); // تحميل نقاط المياه افتراضياً عند البدء
    }

    private void setupMapEvents() {
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                hideBottomCard(); // إخفاء البطاقة عند النقر على الخريطة
                return true;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) { return false; }
        };
        map.getOverlays().add(0, new MapEventsOverlay(mReceive));
    }

    private void setupClickListeners() {
        // أزرار التحكم بالزوم
        findViewById(R.id.btnZoomIn).setOnClickListener(v -> mapController.zoomIn());
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> mapController.zoomOut());

        // زر موقعي الحالي
        findViewById(R.id.btnMyLocation).setOnClickListener(v -> {
            GeoPoint gazaCenter = new GeoPoint(31.5017, 34.4578);
            mapController.animateTo(gazaCenter);
            mapController.setZoom(15.0);
            hideBottomCard();
        });

        // أزرار الفلترة والطبقات الجانبية
        findViewById(R.id.btnLayerWater).setOnClickListener(v -> showWaterPoints());
        findViewById(R.id.btnLayerTruck).setOnClickListener(v -> showTrucks());
        findViewById(R.id.btnLayerStorage).setOnClickListener(v -> showStorage());

        // الاستماع لزر البحث في لوحة المفاتيح
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void addMarker(GeoPoint point, String title, String snippet, String sourceType, int iconRes) {
        if (map == null) return;

        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSnippet(snippet);

        marker.setRelatedObject(sourceType);

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null); // إلغاء الفقاعة الافتراضية

        if (iconRes != 0) {
            Drawable drawable = ContextCompat.getDrawable(this, iconRes);
            if (drawable != null) {
                int sizeInPx = 45;
                Bitmap bitmap = Bitmap.createBitmap(sizeInPx, sizeInPx, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                marker.setIcon(new BitmapDrawable(getResources(), bitmap));
            }
        }

        marker.setOnMarkerClickListener((m, mapView) -> {
            String source = (m.getRelatedObject() != null) ? m.getRelatedObject().toString() : "غير محدد";
            showBottomCard(m.getTitle(), m.getSnippet(), source);
            mapController.animateTo(m.getPosition());
            return true;
        });

        map.getOverlays().add(marker);
    }

    private void clearMap() {
        if (map == null) return;
        map.getOverlays().clear();
        setupMapEvents();
        hideBottomCard();
    }

    private void showWaterPoints() {
        clearMap();
        addMarker(new GeoPoint(31.51, 34.46), "بئر الشفاء", "نقطة توزيع مياه عذبة", "بئر جوفي 🚰", R.drawable.water);
        addMarker(new GeoPoint(31.52, 34.47), "خزان الرمال", "نقطة تعبئة رئيسية", "خزان مياه 🏗️", R.drawable.water);
        map.invalidate();
    }

    private void showTrucks() {
        clearMap();
        addMarker(new GeoPoint(31.49, 34.44), "صهريج مياه", "حي الصبرة - متاح", "صهريج متنقل 🚛", R.drawable.truck);
        addMarker(new GeoPoint(31.48, 34.43), "ناقلة مياه", "حي الزيتون - متاح", "صهريج متنقل 🚛", R.drawable.truck);
        map.invalidate();
    }

    private void showStorage() {
        clearMap();
        addMarker(new GeoPoint(31.53, 34.48), "مستودع الخزانات", "شمال غزة", "مستودع براميل 🛢️", R.drawable.barrel);
        map.invalidate();
    }

    private void performSearch(String query) {
        if (query.toLowerCase().contains("غزة")) {
            mapController.animateTo(new GeoPoint(31.5017, 34.4578));
            mapController.setZoom(14.0);
            hideBottomCard();
        }
    }

    private void showBottomCard(String title, String address, String sourceType) {
        tvLocationTitle.setText(title);
        tvLocationAddress.setText(address);
        tvNearestSource.setText(sourceType);

        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void hideBottomCard() {
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(map != null) map.onPause();
    }
}
