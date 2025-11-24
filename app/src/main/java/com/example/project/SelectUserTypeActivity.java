package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.project.client.ClientMainActivity;
import com.example.project.livreur.LivreurMainActivity;
import com.example.project.manager.ManagerMainActivity;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.UserPreferences;
import com.example.project.R;
import com.google.android.material.card.MaterialCardView;

public class SelectUserTypeActivity extends AppCompatActivity {

    private MaterialCardView cardLivreur, cardManager, cardClient;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        userPrefs = new UserPreferences(this);

        // Initialize views
        cardLivreur = findViewById(R.id.cardLivreur);
        cardManager = findViewById(R.id.cardManager);
        cardClient = findViewById(R.id.cardClient);

        // Set click listeners
        cardLivreur.setOnClickListener(v -> selectUserType(FirebaseHelper.USER_TYPE_LIVREUR));
        cardManager.setOnClickListener(v -> selectUserType(FirebaseHelper.USER_TYPE_MANAGER));
        cardClient.setOnClickListener(v -> selectUserType(FirebaseHelper.USER_TYPE_CLIENT));
    }

    private void selectUserType(String userType) {
        // For demo purposes, create a mock user
        String userId = generateUserId(userType);
        String userName = "User " + userType;
        String userPhone = "123456789";

        // Save user preferences
        userPrefs.saveUser(userId, userName, userType, userPhone);

        // For manager, also save a mock restaurant ID
        if (userType.equals(FirebaseHelper.USER_TYPE_MANAGER)) {
            userPrefs.saveRestaurantId("restaurant_001");
        }

        // Navigate to appropriate activity
        Intent intent;
        switch (userType) {
            case FirebaseHelper.USER_TYPE_LIVREUR:
                intent = new Intent(this, LivreurMainActivity.class);
                break;
            case FirebaseHelper.USER_TYPE_MANAGER:
                intent = new Intent(this, ManagerMainActivity.class);
                break;
            case FirebaseHelper.USER_TYPE_CLIENT:
                intent = new Intent(this, ClientMainActivity.class);
                break;
            default:
                Toast.makeText(this, "Type d'utilisateur invalide", Toast.LENGTH_SHORT).show();
                return;
        }

        startActivity(intent);
        finish();
    }

    private String generateUserId(String userType) {
        return userType + "_" + System.currentTimeMillis();
    }
}