package com.example.project.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project.R;
import com.example.project.adapters.ClientOrdersAdapter;
import com.example.project.models.Commande;
import com.example.project.shared.MapTrackingActivity;
import com.example.project.shared.OrderDetailsActivity;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ClientMainActivity extends AppCompatActivity implements ClientOrdersAdapter.OnOrderClickListener {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewOrders;
    private TextView tvEmptyState;
    private ProgressBar progressBar;

    private ClientOrdersAdapter adapter;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        userPrefs = new UserPreferences(this);
        initializeViews();
        setupRecyclerView();
        loadOrders();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        adapter = new ClientOrdersAdapter(this);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        String clientId = userPrefs.getUserId();

        // Load all orders for this client
        FirebaseHelper.getCommandesCollection()
                .whereEqualTo("clientId", clientId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    List<Commande> orders = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Commande commande = document.toObject(Commande.class);
                        commande.setCommandeId(document.getId());
                        orders.add(commande);
                    }

                    if (orders.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setOrders(orders);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onOrderClick(Commande commande) {
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra("commandeId", commande.getCommandeId());
        startActivity(intent);
    }

    @Override
    public void onTrackClick(Commande commande) {
        Intent intent = new Intent(this, MapTrackingActivity.class);
        intent.putExtra("commandeId", commande.getCommandeId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}