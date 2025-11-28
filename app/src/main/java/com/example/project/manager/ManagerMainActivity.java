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
import com.example.project.adapters.ManagerOrdersAdapter;
import com.example.project.models.Commande;
import com.example.project.shared.OrderDetailsActivity;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.StaticDataProvider;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ManagerMainActivity extends AppCompatActivity implements ManagerOrdersAdapter.OnOrderActionListener {

    private MaterialToolbar toolbar;
    private RecyclerView recyclerViewOrders;
    private TextView tvEmptyState;
    private ProgressBar progressBar;

    private ManagerOrdersAdapter adapter;
    private List<Commande> allOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);

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
        toolbar.setTitle("Manager - Gestion Commandes");
    }

    private void setupRecyclerView() {
        adapter = new ManagerOrdersAdapter(this);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        // Combine static data and Firebase data
        allOrders.clear();

        // 1. Load static commands
        List<Commande> staticCommands = StaticDataProvider.getStaticCommands();
        allOrders.addAll(staticCommands);

        // 2. Load from Firebase (commands that were already accepted)
        FirebaseHelper.getCommandesCollection()
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    queryDocumentSnapshots.forEach(document -> {
                        Commande firebaseCmd = document.toObject(Commande.class);
                        firebaseCmd.setCommandeId(document.getId());

                        // Update static command if it exists in Firebase
                        boolean found = false;
                        for (int i = 0; i < allOrders.size(); i++) {
                            if (allOrders.get(i).getCommandeId().equals(firebaseCmd.getCommandeId())) {
                                allOrders.set(i, firebaseCmd); // Replace with Firebase version
                                StaticDataProvider.updateCommandStatus(
                                        firebaseCmd.getCommandeId(),
                                        firebaseCmd.getStatus()
                                );
                                found = true;
                                break;
                            }
                        }

                        // If not found in static data, add it (custom orders)
                        if (!found) {
                            allOrders.add(firebaseCmd);
                        }
                    });

                    progressBar.setVisibility(View.GONE);

                    if (allOrders.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        adapter.setOrders(allOrders);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    // Even if Firebase fails, show static data
                    adapter.setOrders(allOrders);
                    Toast.makeText(this, "Chargement données statiques uniquement", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onOrderClick(Commande commande) {
        Intent intent = new Intent(this, OrderDetailsActivity.class);
        intent.putExtra("commandeId", commande.getCommandeId());
        startActivity(intent);
    }

    @Override
    public void onAcceptOrder(Commande commande) {
        // Update status
        commande.setStatus(FirebaseHelper.STATUS_MANAGER_ACCEPTED);
        commande.setUpdatedAt(Timestamp.now());

        // Update in static data
        StaticDataProvider.updateCommandStatus(
                commande.getCommandeId(),
                FirebaseHelper.STATUS_MANAGER_ACCEPTED
        );

        // Save to Firebase
        FirebaseHelper.getCommandesCollection()
                .document(commande.getCommandeId())
                .set(commande)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Commande acceptée et visible pour les livreurs", Toast.LENGTH_SHORT).show();
                    loadOrders(); // Refresh list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Even if Firebase fails, local status is updated
                    loadOrders();
                });
    }

    @Override
    public void onRejectOrder(Commande commande) {
        // Update status
        commande.setStatus(FirebaseHelper.STATUS_CANCELLED);
        commande.setUpdatedAt(Timestamp.now());

        // Update in static data
        StaticDataProvider.updateCommandStatus(
                commande.getCommandeId(),
                FirebaseHelper.STATUS_CANCELLED
        );

        // Save to Firebase
        FirebaseHelper.getCommandesCollection()
                .document(commande.getCommandeId())
                .set(commande)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Commande refusée", Toast.LENGTH_SHORT).show();
                    loadOrders();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    loadOrders();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}