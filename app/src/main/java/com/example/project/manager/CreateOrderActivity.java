package com.example.project.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project.R;
import com.example.project.adapters.OrderItemsAdapter;
import com.example.project.models.Commande;
import com.example.project.models.OrderItem;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderActivity extends AppCompatActivity implements OrderItemsAdapter.OnItemRemoveListener {

    private MaterialToolbar toolbar;
    private TextInputEditText etClientName, etClientPhone, etClientAddress, etLatitude, etLongitude;
    private TextInputEditText etItemName, etQuantity, etPrice;
    private Button btnAddItem, btnCreateOrder;
    private RecyclerView recyclerViewItems;
    private TextView tvTotal;

    private OrderItemsAdapter adapter;
    private List<OrderItem> orderItems = new ArrayList<>();
    private double totalPrice = 0;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        userPrefs = new UserPreferences(this);
        initializeViews();
        setupRecyclerView();
        setupListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        etClientName = findViewById(R.id.etClientName);
        etClientPhone = findViewById(R.id.etClientPhone);
        etClientAddress = findViewById(R.id.etClientAddress);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        etItemName = findViewById(R.id.etItemName);
        etQuantity = findViewById(R.id.etQuantity);
        etPrice = findViewById(R.id.etPrice);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        tvTotal = findViewById(R.id.tvTotal);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        // Set default coordinates for Tunis
        etLatitude.setText("36.8065");
        etLongitude.setText("10.1815");
    }

    private void setupRecyclerView() {
        adapter = new OrderItemsAdapter(this);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(adapter);
    }

    private void setupListeners() {
        btnAddItem.setOnClickListener(v -> addItem());
        btnCreateOrder.setOnClickListener(v -> createOrder());
    }

    private void addItem() {
        String itemName = etItemName.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (itemName.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        OrderItem item = new OrderItem(itemName, quantity, price);
        orderItems.add(item);
        adapter.setItems(orderItems);

        calculateTotal();

        // Clear fields
        etItemName.setText("");
        etQuantity.setText("");
        etPrice.setText("");
    }

    @Override
    public void onItemRemove(int position) {
        orderItems.remove(position);
        adapter.setItems(orderItems);
        calculateTotal();
    }

    private void calculateTotal() {
        totalPrice = 0;
        for (OrderItem item : orderItems) {
            totalPrice += item.getSubtotal();
        }
        tvTotal.setText(String.format("Total: %.2f DT", totalPrice));
    }

    private void createOrder() {
        String clientName = etClientName.getText().toString().trim();
        String clientPhone = etClientPhone.getText().toString().trim();
        String clientAddress = etClientAddress.getText().toString().trim();
        String latStr = etLatitude.getText().toString().trim();
        String lonStr = etLongitude.getText().toString().trim();

        if (clientName.isEmpty() || clientPhone.isEmpty() || clientAddress.isEmpty() ||
                latStr.isEmpty() || lonStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs client", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Veuillez ajouter au moins un article", Toast.LENGTH_SHORT).show();
            return;
        }

        double latitude = Double.parseDouble(latStr);
        double longitude = Double.parseDouble(lonStr);

        // Create commande
        Commande commande = new Commande();
        commande.setClientId("client_" + System.currentTimeMillis());
        commande.setClientName(clientName);
        commande.setClientPhone(clientPhone);
        commande.setClientAddress(clientAddress);
        commande.setClientLatitude(latitude);
        commande.setClientLongitude(longitude);

        // Restaurant info (use mock data or from preferences)
        String restaurantId = userPrefs.getRestaurantId();
        commande.setRestaurantId(restaurantId);
        commande.setRestaurantName("Restaurant " + restaurantId);
        commande.setRestaurantAddress("123 Rue Principale, Tunis");
        commande.setRestaurantLatitude(36.8065);
        commande.setRestaurantLongitude(10.1815);

        commande.setStatus(FirebaseHelper.STATUS_PENDING);
        commande.setItems(orderItems);
        commande.setTotalPrice(totalPrice);
        commande.setCreatedAt(Timestamp.now());
        commande.setUpdatedAt(Timestamp.now());

        // Save to Firebase
        FirebaseHelper.getCommandesCollection()
                .add(commande)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Commande créée avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}