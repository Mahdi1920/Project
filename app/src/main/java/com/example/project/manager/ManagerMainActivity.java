package com.example.project.manager;

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
import com.example.project.adapters.OrdersAdapter;
import com.example.project.models.Commande;
import com.example.project.shared.OrderDetailsActivity;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ManagerMainActivity extends AppCompatActivity implements OrdersAdapter.OnOrderClickListener {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewOrders;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private FloatingActionButton fabCreateOrder;

    private OrdersAdapter adapter;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);

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
        fabCreateOrder = findViewById(R.id.fabCreateOrder);

        setSupportActionBar(toolbar);

        fabCreateOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateOrderActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new OrdersAdapter(this);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        String restaurantId = userPrefs.getRestaurantId();

        // Load all orders for this restaurant
        FirebaseHelper.getCommandesCollection()
                .whereEqualTo("restaurantId", restaurantId)
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
    public void onActionClick(Commande commande) {
        // Managers don't have action buttons in their view
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}