package com.example.project.shared;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project.R;
import com.example.project.adapters.OrderItemsAdapter;
import com.example.project.models.Commande;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class OrderDetailsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvOrderId, tvStatus, tvRestaurantName, tvRestaurantAddress;
    private TextView tvClientName, tvClientPhone, tvClientAddress;
    private TextView tvLivreurName, tvLivreurPhone, tvTotalPrice;
    private MaterialCardView cardDeliveryPerson;
    private RecyclerView recyclerViewItems;
    private Button btnNavigate;

    private OrderItemsAdapter adapter;
    private UserPreferences userPrefs;
    private Commande commande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        userPrefs = new UserPreferences(this);
        initializeViews();

        String commandeId = getIntent().getStringExtra("commandeId");
        if (commandeId != null) {
            loadOrderDetails(commandeId);
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatus = findViewById(R.id.tvStatus);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        tvClientName = findViewById(R.id.tvClientName);
        tvClientPhone = findViewById(R.id.tvClientPhone);
        tvClientAddress = findViewById(R.id.tvClientAddress);
        tvLivreurName = findViewById(R.id.tvLivreurName);
        tvLivreurPhone = findViewById(R.id.tvLivreurPhone);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        cardDeliveryPerson = findViewById(R.id.cardDeliveryPerson);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        btnNavigate = findViewById(R.id.btnNavigate);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new OrderItemsAdapter(null);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(adapter);
    }

    private void loadOrderDetails(String commandeId) {
        FirebaseHelper.getCommandesCollection()
                .document(commandeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    commande = documentSnapshot.toObject(Commande.class);
                    if (commande != null) {
                        commande.setCommandeId(documentSnapshot.getId());
                        displayOrderDetails();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayOrderDetails() {
        tvOrderId.setText("CMD-" + commande.getCommandeId().substring(0, 8));

        // Status
        String status = commande.getStatus();
        tvStatus.setText(getStatusText(status));
        tvStatus.setBackgroundColor(getStatusColor(status));

        // Restaurant info
        tvRestaurantName.setText(commande.getRestaurantName());
        tvRestaurantAddress.setText(commande.getRestaurantAddress());

        // Client info
        tvClientName.setText(commande.getClientName());
        tvClientPhone.setText(commande.getClientPhone());
        tvClientAddress.setText(commande.getClientAddress());

        // Delivery person info
        if (commande.getLivreurName() != null) {
            cardDeliveryPerson.setVisibility(View.VISIBLE);
            tvLivreurName.setText(commande.getLivreurName());
            tvLivreurPhone.setText(commande.getLivreurPhone());
        }

        // Items
        adapter.setItems(commande.getItems());

        // Total
        tvTotalPrice.setText(String.format("Total: %.2f DT", commande.getTotalPrice()));

        // Navigate button (only for livreur)
        String userType = userPrefs.getUserType();
        if (userType.equals(FirebaseHelper.USER_TYPE_LIVREUR) &&
                (status.equals(FirebaseHelper.STATUS_ACCEPTED) || status.equals(FirebaseHelper.STATUS_PICKED_UP))) {
            btnNavigate.setVisibility(View.VISIBLE);
            btnNavigate.setOnClickListener(v -> openNavigation());
        }
    }

    private void openNavigation() {
        double lat = commande.getClientLatitude();
        double lng = commande.getClientLongitude();

        String uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng;

        // --- Try Organic Maps ---
        Intent organicIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        organicIntent.setPackage("app.organicmaps");
        if (organicIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(organicIntent);
            return;
        }

        // --- Try OsmAnd ---
        Intent osmandIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        osmandIntent.setPackage("net.osmand");
        if (osmandIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(osmandIntent);
            return;
        }

        // --- Fallback: open browser with OpenStreetMap directions ---
        String browserUrl = "https://www.openstreetmap.org/directions?to=" + lat + "%2C" + lng;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserUrl));
        startActivity(browserIntent);
    }


    private String getStatusText(String status) {
        switch (status) {
            case FirebaseHelper.STATUS_PENDING:
                return "En attente";
            case FirebaseHelper.STATUS_ACCEPTED:
                return "Acceptée";
            case FirebaseHelper.STATUS_PICKED_UP:
                return "Récupérée";
            case FirebaseHelper.STATUS_DELIVERED:
                return "Livrée";
            case FirebaseHelper.STATUS_CANCELLED:
                return "Annulée";
            default:
                return status;
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case FirebaseHelper.STATUS_PENDING:
                return Color.parseColor("#FF9800");
            case FirebaseHelper.STATUS_ACCEPTED:
                return Color.parseColor("#2196F3");
            case FirebaseHelper.STATUS_PICKED_UP:
                return Color.parseColor("#9C27B0");
            case FirebaseHelper.STATUS_DELIVERED:
                return Color.parseColor("#4CAF50");
            case FirebaseHelper.STATUS_CANCELLED:
                return Color.parseColor("#F44336");
            default:
                return Color.GRAY;
        }
    }
}