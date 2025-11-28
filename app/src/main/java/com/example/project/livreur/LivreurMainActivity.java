package com.example.project.livreur;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project.R;
import com.example.project.utils.FirebaseHelper;
import com.example.project.utils.UserPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;

public class LivreurMainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MaterialToolbar toolbar;
    private UserPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livreur_main);

        userPrefs = new UserPreferences(this);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        setSupportActionBar(toolbar);

        // Fetch latest user info
        fetchLivreurInfo();

        setupViewPager();
    }

    private void fetchLivreurInfo() {
        String userId = userPrefs.getUserId();

        if (userId == null) {
            Toast.makeText(this, "Erreur: ID utilisateur non trouvé", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseHelper.getUsersCollection()
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String phone = doc.getString("phone");
                        String type = doc.getString("type");

                        // Save refreshed data
                        userPrefs.setUserName(name);
                        userPrefs.setUserPhone(phone);
                        userPrefs.setUserType(type);

                        Toast.makeText(this, "Profil synchronisé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Commandes Disponibles");
                    } else {
                        tab.setText("Mes Livraisons");
                    }
                }).attach();
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(@NonNull FragmentActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return (position == 0)
                    ? new AvailableOrdersFragment()
                    : new MyDeliveriesFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
