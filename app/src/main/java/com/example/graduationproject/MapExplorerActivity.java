package com.example.graduationproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapExplorerActivity extends AppCompatActivity {

    private MapView map = null;
    private IMapController mapController;
    private EditText etSearch;
    private CardView bottomSheetCard;
    private BottomSheetBehavior<CardView> bottomSheetBehavior;
    private TextView tvLocationTitle, tvLocationAddress, tvNearestSource, move, Confirm1;
    private View btnMyOrders;

    private String selectedProviderName = "";
    private String selectedAddress = "";
    private String selectedSourceType = "";
    private double selectedLat = 0;
    private double selectedLng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map_explorer);

        map = findViewById(R.id.map);
        move = findViewById(R.id.move1);
        Confirm1 = findViewById(R.id.btnConfirm1);
        bottomSheetCard = findViewById(R.id.bottomSheetCard);
        tvLocationTitle = findViewById(R.id.tvLocationTitle);
        tvLocationAddress = findViewById(R.id.tvLocationAddress);
        tvNearestSource = findViewById(R.id.tvNearestSource);
        etSearch = findViewById(R.id.etSearch);
        
        btnMyOrders = findViewById(R.id.move22);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetCard);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        if (btnMyOrders != null) {
            btnMyOrders.setOnClickListener(v -> {
                Intent intent = new Intent(MapExplorerActivity.this, My_Orders_Activity.class);
                startActivity(intent);
            });
        }

        Confirm1.setOnClickListener(v -> {
            Intent intent = new Intent(MapExplorerActivity.this, ProviderDetailsActivity.class);
            intent.putExtra("provider_name", selectedProviderName);
            intent.putExtra("address", selectedAddress);
            intent.putExtra("source_type", selectedSourceType);
            intent.putExtra("lat", selectedLat);
            intent.putExtra("lng", selectedLng);
            startActivity(intent);
        });

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
            setupMapEvents();
        }

        setupClickListeners();
        fetchProvidersFromSupabase("well");
    }

    private void setupMapEvents() {
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                hideBottomCard();
                return true;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) { return false; }
        };
        map.getOverlays().add(0, new MapEventsOverlay(mReceive));
    }

    private void setupClickListeners() {
        findViewById(R.id.btnZoomIn).setOnClickListener(v -> mapController.zoomIn());
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> mapController.zoomOut());
        findViewById(R.id.btnMyLocation).setOnClickListener(v -> {
            GeoPoint gazaCenter = new GeoPoint(31.5017, 34.4578);
            mapController.animateTo(gazaCenter);
            mapController.setZoom(15.0);
            hideBottomCard();
        });

        findViewById(R.id.btnLayerWater).setOnClickListener(v -> fetchProvidersFromSupabase("well"));
        findViewById(R.id.btnLayerTruck).setOnClickListener(v -> fetchProvidersFromSupabase("truck"));
        findViewById(R.id.btnLayerStorage).setOnClickListener(v -> fetchProvidersFromSupabase("storage"));

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void fetchProvidersFromSupabase(String type) {
        clearMap();
        SupabaseApi api = SupbaseClient.getClient(this).create(SupabaseApi.class);
        api.getProviders("*", "eq." + type).enqueue(new Callback<List<ProviderModel>>() {
            @Override
            public void onResponse(Call<List<ProviderModel>> call, Response<List<ProviderModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProviderModel> providers = response.body();
                    if (providers.isEmpty()) {
                        Toast.makeText(MapExplorerActivity.this, "لا يوجد مزودين من هذا النوع حالياً", Toast.LENGTH_SHORT).show();
                    }
                    for (ProviderModel provider : providers) {
                        int iconRes = R.drawable.water;
                        if ("truck".equals(provider.getProviderType())) iconRes = R.drawable.truck;
                        else if ("storage".equals(provider.getProviderType())) iconRes = R.drawable.barrel;
                        
                        addMarker(new GeoPoint(provider.getCurrentLat(), provider.getCurrentLng()), 
                                 provider.getBusinessName(), 
                                 "حالة المزود: " + provider.getStatus(), 
                                 getArabicType(provider.getProviderType()), 
                                 iconRes);
                    }
                    map.invalidate();
                } else {
                    // إظهار كود الخطأ لمعرفة السبب (401: مفتاح خطأ، 404: جدول غير موجود، 400: عمود خطأ)
                    String errorMsg = "خطأ من السيرفر: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += "\n" + response.errorBody().string();
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    Log.e("SupabaseError", errorMsg);
                    Toast.makeText(MapExplorerActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProviderModel>> call, Throwable t) {
                Log.e("SupabaseError", "Network Failure", t);
                Toast.makeText(MapExplorerActivity.this, "خطأ في الشبكة: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getArabicType(String type) {
        if ("well".equals(type)) return "بئر مياه 🚰";
        if ("truck".equals(type)) return "صهريج متنقل 🚛";
        if ("storage".equals(type)) return "مستودع خزانات 🏗️";
        return "مزود خدمة";
    }

    private void addMarker(GeoPoint point, String title, String snippet, String sourceType, int iconRes) {
        if (map == null) return;
        Marker marker = new Marker(map);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setRelatedObject(sourceType);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setInfoWindow(null);

        if (iconRes != 0) {
            Drawable drawable = ContextCompat.getDrawable(this, iconRes);
            if (drawable != null) {
                int sizeInPx = 60;
                Bitmap bitmap = Bitmap.createBitmap(sizeInPx, sizeInPx, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                marker.setIcon(new BitmapDrawable(getResources(), bitmap));
            }
        }

        marker.setOnMarkerClickListener((m, mapView) -> {
            selectedProviderName = m.getTitle();
            selectedAddress = m.getSnippet();
            selectedSourceType = m.getRelatedObject().toString();
            selectedLat = m.getPosition().getLatitude();
            selectedLng = m.getPosition().getLongitude();

            showBottomCard(selectedProviderName, selectedAddress, selectedSourceType);
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
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void hideBottomCard() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onResume() { super.onResume(); if(map != null) map.onResume(); }
    @Override
    public void onPause() { super.onPause(); if(map != null) map.onPause(); }
}
