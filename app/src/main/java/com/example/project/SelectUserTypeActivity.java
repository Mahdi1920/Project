package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.project.livreur.LivreurMainActivity;
import com.example.project.manager.ManagerMainActivity;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.UserPreferences;
import com.google.android.material.card.MaterialCardView;

public class SelectUserTypeActivity extends AppCompatActivity {

    private MaterialCardView cardLivreur, cardManager;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);

        userPrefs = new UserPreferences(this);

        // Initialize views - ONLY TWO CARDS NOW
        cardLivreur = findViewById(R.id.cardLivreur);
        cardManager = findViewById(R.id.cardManager);

        // Set click listeners
        cardLivreur.setOnClickListener(v -> selectUserType(FirebaseHelper.USER_TYPE_LIVREUR));
        cardManager.setOnClickListener(v -> selectUserType(FirebaseHelper.USER_TYPE_MANAGER));
    }

    private void selectUserType(String userType) {
        // Create mock user
        String userId = generateUserId(userType);
        String userName = getUserName(userType);
        String userPhone = "+216 " + (20000000 + (int)(Math.random() * 9999999));

        // Save user preferences
        userPrefs.saveUser(userId, userName, userType, userPhone);

        // For manager, save restaurant ID
        if (userType.equals(FirebaseHelper.USER_TYPE_MANAGER)) {
            userPrefs.saveRestaurantId("restaurant_001");
        }

        // Navigate to appropriate activity
        Intent intent;
        if (userType.equals(FirebaseHelper.USER_TYPE_LIVREUR)) {
            intent = new Intent(this, LivreurMainActivity.class);
        } else {
            intent = new Intent(this, ManagerMainActivity.class);
        }

        startActivity(intent);
        finish();
    }

    private String generateUserId(String userType) {
        return userType + "_" + System.currentTimeMillis();
    }

    private String getUserName(String userType) {
        if (userType.equals(FirebaseHelper.USER_TYPE_MANAGER)) {
            return "Manager Restaurant";
        } else {
            return "Livreur " + ((int)(Math.random() * 100));
        }
    }
}