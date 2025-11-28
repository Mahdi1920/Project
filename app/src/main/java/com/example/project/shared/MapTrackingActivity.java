package com.example.project.shared;

import android.Manifest;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.project.R;
import com.example.project.models.Commande;
import com.example.project.utils.FirebaseHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.geometry.LatLngBounds;
import org.maplibre.android.location.LocationComponent;
import org.maplibre.android.location.LocationComponentActivationOptions;
import org.maplibre.android.location.modes.CameraMode;
import org.maplibre.android.location.modes.RenderMode;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;
import org.maplibre.android.style.layers.LineLayer;
import org.maplibre.android.style.layers.Property;
import org.maplibre.android.style.layers.SymbolLayer;
import org.maplibre.android.style.sources.GeoJsonSource;
import org.maplibre.geojson.Feature;
import org.maplibre.geojson.FeatureCollection;
import org.maplibre.geojson.LineString;
import org.maplibre.geojson.Point;

import java.util.ArrayList;
import java.util.List;

import static org.maplibre.android.style.layers.PropertyFactory.*;

public class MapTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private MapView mapView;
    private MapLibreMap mapLibreMap;

    private TextView tvOrderId, tvStatus, tvDistance, tvLivreurName, tvLivreurPhone;
    private Button btnStartNavigation;
    private FloatingActionButton btnCenterMap;

    private String commandeId;
    private Commande commande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapLibre.getInstance(this);
        setContentView(R.layout.activity_map_tracking);

        commandeId = getIntent().getStringExtra("commandeId");

        setupViews(savedInstanceState);
        loadOrderDetails();
    }

    private void setupViews(Bundle savedInstanceState) {

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatus = findViewById(R.id.tvStatus);
        tvDistance = findViewById(R.id.tvDistance);
        tvLivreurName = findViewById(R.id.tvLivreurName);
        tvLivreurPhone = findViewById(R.id.tvLivreurPhone);

        btnStartNavigation = findViewById(R.id.btnStartNavigation);
        btnCenterMap = findViewById(R.id.btnCenterMap);

        btnStartNavigation.setOnClickListener(v -> startNavigation());
        btnCenterMap.setOnClickListener(v -> centerMapOnRoute());
    }

    @Override
    public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
        this.mapLibreMap = mapLibreMap;

        String mapTilerStyleUrl = "https://api.maptiler.com/maps/streets-v2/style.json?key=b4deCN3RJEq5zFbj2hWa ";
        mapLibreMap.setStyle(mapTilerStyleUrl, style -> {
            if (style != null) setupMapWithRoute();
        });

    }

    private void loadOrderDetails() {
        FirebaseHelper.getCommandesCollection()
                .document(commandeId)
                .get()
                .addOnSuccessListener(doc -> {
                    commande = doc.toObject(Commande.class);
                    if (commande != null) {
                        commande.setCommandeId(doc.getId());
                        displayOrderInfo();
                        if (mapLibreMap != null) setupMapWithRoute();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayOrderInfo() {
        tvOrderId.setText("CMD-" + commande.getCommandeId().substring(0, 8));
        tvStatus.setText(commande.getStatus() != null ? commande.getStatus() : "N/A");

        if (commande.getLivreurName() != null) {
            tvLivreurName.setText("Livreur: " + commande.getLivreurName());
            tvLivreurPhone.setText("Tél: " + commande.getLivreurPhone());
        }

        double dist = calculateDistance(
                commande.getRestaurantLatitude(),
                commande.getRestaurantLongitude(),
                commande.getClientLatitude(),
                commande.getClientLongitude()
        );

        tvDistance.setText(String.format("Distance: %.2f km", dist));
    }

    // ---------------------- FIX MARKER SIZE ----------------------
    private Bitmap resizeMarker(int drawableId, int width, int height) {
        Bitmap original = BitmapFactory.decodeResource(getResources(), drawableId);
        return Bitmap.createScaledBitmap(original, width, height, false);
    }
    // --------------------------------------------------------------

    private void setupMapWithRoute() {
        Style style = mapLibreMap.getStyle();
        if (style == null || commande == null) return;

        LatLng restaurant = new LatLng(commande.getRestaurantLatitude(), commande.getRestaurantLongitude());
        LatLng client = new LatLng(commande.getClientLatitude(), commande.getClientLongitude());

        // FIXED: add small marker icons
        style.addImage("restaurant-icon", resizeMarker(R.drawable.ic_restaurant, 64, 64));
        style.addImage("client-icon", resizeMarker(R.drawable.ic_client, 64, 64));

        Feature restaurantFeature = Feature.fromGeometry(
                Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude())
        );
        restaurantFeature.addStringProperty("icon", "restaurant-icon");

        Feature clientFeature = Feature.fromGeometry(
                Point.fromLngLat(client.getLongitude(), client.getLatitude())
        );
        clientFeature.addStringProperty("icon", "client-icon");

        style.addSource(new GeoJsonSource("markers-source",
                FeatureCollection.fromFeatures(new Feature[]{restaurantFeature, clientFeature})
        ));

        SymbolLayer layer = new SymbolLayer("markers-layer", "markers-source")
                .withProperties(
                        iconImage("{icon}"),
                        iconSize(1.0f),
                        iconAllowOverlap(true)
                );

        style.addLayer(layer);

        drawRouteLine(restaurant, client, style);
        centerMapOnRoute();
    }

    private void drawRouteLine(LatLng a, LatLng b, Style style) {
        List<Point> pts = new ArrayList<>();
        pts.add(Point.fromLngLat(a.getLongitude(), a.getLatitude()));
        pts.add(Point.fromLngLat(b.getLongitude(), b.getLatitude()));

        LineString line = LineString.fromLngLats(pts);

        style.addSource(new GeoJsonSource("route-source",
                FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(line)})
        ));

        LineLayer lineLayer = new LineLayer("route-layer", "route-source")
                .withProperties(
                        lineColor(Color.BLUE),
                        lineWidth(5f),
                        lineOpacity(0.8f),
                        lineCap(Property.LINE_CAP_ROUND),
                        lineJoin(Property.LINE_JOIN_ROUND)
                );

        style.addLayer(lineLayer);
    }

    private void centerMapOnRoute() {
        if (commande == null) return;

        LatLng restaurant = new LatLng(commande.getRestaurantLatitude(), commande.getRestaurantLongitude());
        LatLng client = new LatLng(commande.getClientLatitude(), commande.getClientLongitude());

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(restaurant)
                .include(client)
                .build();

        mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
    }

    private void startNavigation() {
        if (commande == null) return;

        LatLng dest = new LatLng(commande.getClientLatitude(), commande.getClientLongitude());

        CameraPosition pos = new CameraPosition.Builder()
                .target(dest)
                .zoom(16)
                .build();

        mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

        Toast.makeText(this, "Navigation démarrée", Toast.LENGTH_SHORT).show();
        btnStartNavigation.setText("Navigation Active");
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // MapView lifecycle
    @Override protected void onStart() { super.onStart(); mapView.onStart(); }
    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onStop() { super.onStop(); mapView.onStop(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}