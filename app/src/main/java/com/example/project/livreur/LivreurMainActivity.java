package com.example.project.livreur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import com.example.project.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LivreurMainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livreur_main);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        setSupportActionBar(toolbar);

        setupViewPager();
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

        public ViewPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new AvailableOrdersFragment();
            } else {
                return new MyDeliveriesFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}