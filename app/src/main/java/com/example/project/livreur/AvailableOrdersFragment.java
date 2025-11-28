package com.example.project.livreur;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.adapters.OrdersAdapter;
import com.example.project.models.Commande;
import com.example.project.shared.OrderDetailsActivity;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.UserPreferences;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AvailableOrdersFragment extends Fragment implements OrdersAdapter.OnOrderClickListener {

    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private OrdersAdapter adapter;
    private UserPreferences userPrefs;
    private List<Commande> availableOrders = new ArrayList<>();
    private boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userPrefs = new UserPreferences(requireContext());

        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        progressBar = view.findViewById(R.id.progressBar);

        tvEmptyState.setText("Aucune commande disponible pour le moment");

        setupRecyclerView();
        loadOrders();
    }

    private void setupRecyclerView() {
        adapter = new OrdersAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        if (isFirstLoad) {
            progressBar.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }

        // Load ONLY manager-accepted orders (not yet taken by any livreur)
        FirebaseHelper.getCommandesCollection()
                .whereEqualTo("status", FirebaseHelper.STATUS_MANAGER_ACCEPTED)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    availableOrders.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Commande commande = document.toObject(Commande.class);
                        commande.setCommandeId(document.getId());

                        // Double check: ensure no livreurId is assigned
                        if (commande.getLivreurId() == null || commande.getLivreurId().isEmpty()) {
                            availableOrders.add(commande);
                        }
                    }

                    updateUI();
                    isFirstLoad = false;
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                    tvEmptyState.setText("Erreur: " + e.getMessage());
                    isFirstLoad = false;
                });
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);

        if (availableOrders.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setOrders(availableOrders);
        }
    }

    @Override
    public void onOrderClick(Commande commande) {
        Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
        intent.putExtra("commandeId", commande.getCommandeId());
        startActivity(intent);
    }

    @Override
    public void onActionClick(Commande commande) {
        // Livreur accepts the order
        String livreurId = userPrefs.getUserId();
        String livreurName = userPrefs.getUserName();
        String livreurPhone = userPrefs.getUserPhone();

        if (livreurId == null || livreurName == null) {
            Toast.makeText(requireContext(), "Erreur: Données livreur incomplètes", Toast.LENGTH_SHORT).show();
            return;
        }

        commande.setLivreurId(livreurId);
        commande.setLivreurName(livreurName);
        commande.setLivreurPhone(livreurPhone);
        commande.setStatus(FirebaseHelper.STATUS_LIVREUR_ACCEPTED);
        commande.setUpdatedAt(Timestamp.now());

        FirebaseHelper.getCommandesCollection()
                .document(commande.getCommandeId())
                .set(commande)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Commande acceptée! Prêt pour la livraison", Toast.LENGTH_SHORT).show();
                    // Remove from available list
                    availableOrders.remove(commande);
                    updateUI();

                    // Refresh in case another livreur took it
                    loadOrders();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload available orders when returning to this fragment
        loadOrders();
    }
}