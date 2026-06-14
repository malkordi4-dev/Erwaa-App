package com.example.graduationproject;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Group_Order_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText etCoordinatorName, etCoordinatorPhone, etLocationDescription;
    private TextView tvTotalLitersBadge;
    private RecyclerView rvNeighborsList;
    private MapView mapViewLocation;
    private GoogleMap googleMap;
    
    private NeighborAdapter adapter;
    private List<Neighbor> neighborList = new ArrayList<>();
    private int totalLiters = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_order);

        // Initialize Views
        ImageView btnBack = findViewById(R.id.btnBack);
        etCoordinatorName = findViewById(R.id.etCoordinatorName);
        etCoordinatorPhone = findViewById(R.id.etCoordinatorPhone);
        tvTotalLitersBadge = findViewById(R.id.tvTotalLitersBadge);
        rvNeighborsList = findViewById(R.id.rvNeighborsList);
        LinearLayout btnAddNeighbor = findViewById(R.id.btnAddNeighborClick);
        mapViewLocation = findViewById(R.id.mapViewLocation);
        etLocationDescription = findViewById(R.id.etLocationDescription);
        android.view.View btnSubmit = findViewById(R.id.btnSubmitOrder);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        adapter = new NeighborAdapter(neighborList, position -> {
            totalLiters -= neighborList.get(position).getQuantity();
            neighborList.remove(position);
            adapter.notifyItemRemoved(position);
            updateTotalBadge();
        });
        rvNeighborsList.setLayoutManager(new LinearLayoutManager(this));
        rvNeighborsList.setAdapter(adapter);

        // Add Neighbor Button
        btnAddNeighbor.setOnClickListener(v -> showAddNeighborDialog());

        // Map setup
        mapViewLocation.onCreate(savedInstanceState);
        mapViewLocation.getMapAsync(this);

        // Submit Button
        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                Toast.makeText(this, "تم إرسال الطلب الجماعي للحي بنجاح", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void showAddNeighborDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("إضافة جار جديد");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputName = new EditText(this);
        inputName.setHint("اسم الجار");
        layout.addView(inputName);

        final EditText inputQty = new EditText(this);
        inputQty.setHint("الكمية (لتر)");
        inputQty.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputQty);

        builder.setView(layout);

        builder.setPositiveButton("إضافة", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String qtyStr = inputQty.getText().toString().trim();

            if (!name.isEmpty() && !qtyStr.isEmpty()) {
                int qty = Integer.parseInt(qtyStr);
                neighborList.add(new Neighbor(name, qty));
                totalLiters += qty;
                adapter.notifyItemInserted(neighborList.size() - 1);
                updateTotalBadge();
            } else {
                Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("إلغاء", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateTotalBadge() {
        tvTotalLitersBadge.setText(totalLiters + " لتر إجمالي");
    }

    private boolean validateInputs() {
        if (etCoordinatorName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "يرجى إدخال اسم المنسق", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCoordinatorPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "يرجى إدخال رقم الجوال", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (neighborList.isEmpty()) {
            Toast.makeText(this, "يرجى إضافة جار واحد على الأقل", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        LatLng defaultLoc = new LatLng(31.5, 34.4); // Gaza example
        googleMap.addMarker(new MarkerOptions().position(defaultLoc).title("نقطة تجمع الصهريج"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 15));
    }

    // MapView Lifecycle methods
    @Override
    protected void onResume() {
        super.onResume();
        mapViewLocation.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewLocation.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewLocation.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewLocation.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapViewLocation.onSaveInstanceState(outState);
    }
}
