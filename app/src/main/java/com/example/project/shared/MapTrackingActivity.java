package com.example.project.shared;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
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

import org.maplibre.android.MapLibre;
import org.maplibre.android.location.LocationComponent;
import org.maplibre.android.location.modes.CameraMode;
import org.maplibre.android.location.modes.RenderMode;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.geometry.LatLngBounds;
import org.maplibre.geojson.Feature;
import org.maplibre.geojson.FeatureCollection;
import org.maplibre.geojson.Point;
import org.maplibre.android.style.sources.GeoJsonSource;
import org.maplibre.android.style.layers.SymbolLayer;
import static org.maplibre.android.style.layers.PropertyFactory.*;
import org.maplibre.android.location.LocationComponentActivationOptions;
public class MapTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private MapView mapView;
    private MapLibreMap mapLibreMap;

    private MaterialToolbar toolbar;
    private TextView tvOrderId, tvStatus, tvLivreurName, tvLivreurPhone;

    private String commandeId;
    private Commande commande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapLibre.getInstance(this);
        setContentView(R.layout.activity_map_tracking);

        commandeId = getIntent().getStringExtra("commandeId");

        initializeViews(savedInstanceState);
        loadOrderDetails();
    }

    private void initializeViews(Bundle savedInstanceState) {
        toolbar        = findViewById(R.id.toolbar);
        tvOrderId      = findViewById(R.id.tvOrderId);
        tvStatus       = findViewById(R.id.tvStatus);
        tvLivreurName  = findViewById(R.id.tvLivreurName);
        tvLivreurPhone = findViewById(R.id.tvLivreurPhone);
        mapView        = findViewById(R.id.mapView);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
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
                        if (mapLibreMap != null) setupMapMarkers();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayOrderInfo() {
        tvOrderId.setText("CMD-" + commande.getCommandeId().substring(0, 8));
        tvStatus.setText(getStatusText(commande.getStatus()));
        tvStatus.setBackgroundColor(getStatusColor(commande.getStatus()));

        if (commande.getLivreurName() != null) {
            tvLivreurName.setText("Livreur : " + commande.getLivreurName());
            tvLivreurPhone.setText("Tél : " + commande.getLivreurPhone());
        }
    }

    /* ---------------------------------------------------------------------- */
    /* MapLibre Setup                                                         */
    /* ---------------------------------------------------------------------- */
    // No changes needed here, this is correct
    @Override
    public void onMapReady(@NonNull MapLibreMap mapLibreMap) {
        this.mapLibreMap = mapLibreMap;

        mapLibreMap.setStyle(Style.getPredefinedStyle("STREETS"), style -> {
            enableMyLocation(); // This will now use the new LocationComponent
            if (commande != null) setupMapMarkers();
        });
    }


    private void enableMyLocation() {
        // First, check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Style style = mapLibreMap.getStyle();
            if (style == null) {
                // Style is not ready yet, handle this case or wait
                return;
            }

            // Get an instance of the component
            LocationComponent locationComponent = mapLibreMap.getLocationComponent();

            // Set up and activate the LocationComponent
            LocationComponentActivationOptions activationOptions =
                    LocationComponentActivationOptions.builder(this, style)
                            .useDefaultLocationEngine(true) // Use the default location provider
                            .build();

            locationComponent.activateLocationComponent(activationOptions);

            // Enable the component to make it visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the camera and render modes
            locationComponent.setCameraMode(CameraMode.TRACKING); // Optional: Follow the user's location
            locationComponent.setRenderMode(RenderMode.COMPASS);   // Optional: Show a compass-style puck

        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    /* ---------------------------------------------------------------------- */
    /* NEW MARKER SYSTEM (MapLibre v11)                                       */
    /* ---------------------------------------------------------------------- */
    private void setupMapMarkers() {
        Style style = mapLibreMap.getStyle();
        if (style == null) return;

        LatLng restaurant = new LatLng(commande.getRestaurantLatitude(), commande.getRestaurantLongitude());
        LatLng client     = new LatLng(commande.getClientLatitude(), commande.getClientLongitude());

        // 1. Add marker icon to style
        style.addImage("restaurant-icon", BitmapFactory.decodeResource(getResources(), R.drawable.ic_restaurant));
        style.addImage("client-icon", BitmapFactory.decodeResource(getResources(), R.drawable.ic_client));

        // 2. Create GeoJSON features
        Feature fRestaurant = Feature.fromGeometry(
                Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude())
        );
        fRestaurant.addStringProperty("icon", "restaurant-icon");

        Feature fClient = Feature.fromGeometry(
                Point.fromLngLat(client.getLongitude(), client.getLatitude())
        );
        fClient.addStringProperty("icon", "client-icon");

        // 3. Add GeoJson source
        FeatureCollection fc = FeatureCollection.fromFeatures(new Feature[]{fRestaurant, fClient});
        GeoJsonSource source = new GeoJsonSource("markers-source", fc);

        if (style.getSource("markers-source") == null) {
            style.addSource(source);
        } else {
            ((GeoJsonSource) style.getSource("markers-source")).setGeoJson(fc);
        }

        // 4. Add SymbolLayer
        if (style.getLayer("markers-layer") == null) {
            style.addLayer(new SymbolLayer("markers-layer", "markers-source")
                    .withProperties(
                            iconImage("{icon}"),
                            iconSize(1.2f),
                            iconAllowOverlap(true),
                            textField("{title}"),
                            textSize(12f),
                            textOffset(new Float[]{0f, 1.5f}),
                            textColor(Color.BLACK)
                    ));
        }

        // 5. Zoom to show both markers
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(restaurant)
                .include(client)
                .build();

        mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
    }

    /* ---------------------------------------------------------------------- */
    /* Helpers                                                                 */
    /* ---------------------------------------------------------------------- */
    private String getStatusText(String status) {
        switch (status) {
            case FirebaseHelper.STATUS_ACCEPTED: return "Acceptée";
            case FirebaseHelper.STATUS_PICKED_UP: return "En cours";
            default: return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case FirebaseHelper.STATUS_ACCEPTED: return Color.parseColor("#2196F3");
            case FirebaseHelper.STATUS_PICKED_UP: return Color.parseColor("#9C27B0");
            default: return Color.GRAY;
        }
    }

    /* ---------------------------------------------------------------------- */
    /* Permissions + Lifecycle                                                 */
    /* ---------------------------------------------------------------------- */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        }
    }

    @Override protected void onStart()   { super.onStart();   mapView.onStart(); }
    @Override protected void onResume()  { super.onResume();  mapView.onResume(); }
    @Override protected void onPause()   { super.onPause();   mapView.onPause(); }
    @Override protected void onStop()    { super.onStop();    mapView.onStop(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    @Override public void onLowMemory()  { super.onLowMemory(); mapView.onLowMemory(); }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
