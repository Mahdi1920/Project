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

        setupRecyclerView();
        loadOrders();
    }

    private void setupRecyclerView() {
        adapter = new OrdersAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);

        // Load pending orders
        FirebaseHelper.getCommandesCollection()
                .whereEqualTo("status", FirebaseHelper.STATUS_PENDING)
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
                    Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onOrderClick(Commande commande) {
        Intent intent = new Intent(requireContext(), OrderDetailsActivity.class);
        intent.putExtra("commandeId", commande.getCommandeId());
        startActivity(intent);
    }

    @Override
    public void onActionClick(Commande commande) {
        // Accept order
        String livreurId = userPrefs.getUserId();
        String livreurName = userPrefs.getUserName();
        String livreurPhone = userPrefs.getUserPhone();

        commande.setLivreurId(livreurId);
        commande.setLivreurName(livreurName);
        commande.setLivreurPhone(livreurPhone);
        commande.setStatus(FirebaseHelper.STATUS_ACCEPTED);
        commande.setUpdatedAt(Timestamp.now());

        FirebaseHelper.getCommandesCollection()
                .document(commande.getCommandeId())
                .set(commande)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Commande acceptée", Toast.LENGTH_SHORT).show();
                    loadOrders();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }
}