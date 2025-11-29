package tn.esprit.project;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import tn.esprit.project.models.User;
import tn.esprit.project.utils.AppDatabase;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private static final String TAG = "MainActivity";

    // References
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // ensure drawer is unlocked so swipe to open always works
        try { drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); } catch (Exception ignored) {}

        // populate drawer header from DB (user id 1)
        View header = null;
        try {
            header = navigationView.getHeaderView(0);
        } catch (Exception ignored) {}

        ImageView ivAvatar = null;
        TextView tvName = null;
        TextView tvEmail = null;
        if (header != null) {
            ivAvatar = header.findViewById(R.id.iv_drawer_avatar);
            tvName = header.findViewById(R.id.tv_drawer_name);
            tvEmail = header.findViewById(R.id.tv_drawer_email);
        }

        final ImageView finalIvAvatar = ivAvatar;
        final TextView finalTvName = tvName;
        final TextView finalTvEmail = tvEmail;

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                User u = db.userDAO().getById(1);
                if (u != null && finalTvName != null && finalTvEmail != null && finalIvAvatar != null) {
                    final String name = u.getName() != null ? u.getName() : "Utilisateur Exemple";
                    final String email = u.getEmail() != null ? u.getEmail() : "email@example.com";
                    final String avatar = u.getAvatarUrl() != null ? u.getAvatarUrl() : "";

                    runOnUiThread(() -> {
                        finalTvName.setText(name);
                        finalTvEmail.setText(email);

                        try {
                            if (avatar == null || avatar.isEmpty()) {
                                finalIvAvatar.setImageResource(R.mipmap.profile_round);
                            } else if (avatar.startsWith("@drawable/") || avatar.startsWith("@mipmap/")) {
                                String resName = avatar.startsWith("@drawable/") ? avatar.substring("@drawable/".length()) : avatar.substring("@mipmap/".length());
                                int resId = getResources().getIdentifier(resName, "drawable", getPackageName());
                                if (resId == 0) resId = getResources().getIdentifier(resName, "mipmap", getPackageName());
                                if (resId != 0) {
                                    finalIvAvatar.setImageResource(resId);
                                } else {
                                    finalIvAvatar.setImageResource(R.mipmap.profile_round);
                                }
                            } else if (avatar.startsWith("content:") || avatar.startsWith("file:")) {
                                try {
                                    finalIvAvatar.setImageURI(Uri.parse(avatar));
                                } catch (Exception e) {
                                    finalIvAvatar.setImageResource(R.mipmap.profile_round);
                                }
                            } else {
                                finalIvAvatar.setImageResource(R.mipmap.profile_round);
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Failed to set avatar", e);
                            finalIvAvatar.setImageResource(R.mipmap.profile_round);
                        }
                    });
                }
            } catch (Exception e) {
                Log.w(TAG, "Error loading drawer header user", e);
            }
        }).start();

        // Resolve NavController using NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            // try deferred resolution (layout may not be ready) — will attempt again below
            navController = null;
        }

        // IDs of top-level destinations (hamburger shown for these) - make restaurantsFragment the only top-level (default)
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.restaurantsFragment)
                .setOpenableLayout(drawer)
                .build();

        if (navController != null) {
            setupNavigation(navController, navigationView, drawer);
        } else {
            // deferred: post a task to link NavController after layout inflation
            drawer.post(() -> {
                try {
                    NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
                    if (host != null) {
                        navController = host.getNavController();
                        setupNavigation(navController, navigationView, drawer);

                        // Explicit initial toolbar nav icon for deferred resolution
                        try {
                            int startId = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : (navController.getGraph() != null ? navController.getGraph().getStartDestinationId() : -1);
                            boolean isTop = startId != -1 && appBarConfiguration.getTopLevelDestinations().contains(startId);
                            if (isTop) {
                                toolbar.setNavigationIcon(R.drawable.ic_menu);
                                toolbar.setNavigationOnClickListener(v -> {
                                    try { drawer.openDrawer(GravityCompat.START); } catch (Exception e) { /* ignore */ }
                                });
                                if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            } else {
                                toolbar.setNavigationIcon(null);
                                if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                toolbar.setNavigationOnClickListener(v -> {
                                    try { navController.navigateUp(); } catch (Exception e) { getOnBackPressedDispatcher().onBackPressed(); }
                                });
                            }
                        } catch (Exception ignored) {}
                    } else {
                        Log.w(TAG, "NavHostFragment still null after post(). Check activity_main layout id.");
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Deferred NavController resolution failed", e);
                }
            });
        }

        // ensure drawer remains unlocked initially only if we're at top destination
        try {
            if (navController != null && appBarConfiguration.getTopLevelDestinations().contains(navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : -1)) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            } else {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        } catch (Exception ignored) {}

        // Register back-pressed callback to replace deprecated onBackPressed
        OnBackPressedCallback backCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DrawerLayout d = findViewById(R.id.drawer_layout);
                if (d != null && d.isDrawerOpen(GravityCompat.START)) {
                    d.closeDrawer(GravityCompat.START);
                    return;
                }

                // Let NavController handle back navigation when possible
                try {
                    if (navController != null) {
                        if (!navController.popBackStack()) {
                            finish();
                        }
                    } else {
                        finish();
                    }
                } catch (Exception e) {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backCallback);
    }

    private void setupNavigation(NavController navController, NavigationView navigationView, DrawerLayout drawer) {
        // Let NavigationUI manage the ActionBar (hamburger/up) using AppBarConfiguration
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Ensure the current destination is checked in the drawer
        try {
            if (navController.getCurrentDestination() != null) {
                int current = navController.getCurrentDestination().getId();
                try { navigationView.setCheckedItem(current); } catch (Exception ignored) {}
            } else if (navController.getGraph() != null) {
                // fallback to startDestination
                try { navigationView.setCheckedItem(navController.getGraph().getStartDestinationId()); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        // Close drawer when an item is selected
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            boolean handled = NavigationUI.onNavDestinationSelected(menuItem, navController);
            drawer.closeDrawers();
            return handled;
        });

        // Listen to destination changes to toggle hamburger/up and lock drawer
        navController.addOnDestinationChangedListener((controller, destination, args) -> {
            boolean isTop = appBarConfiguration.getTopLevelDestinations().contains(destination.getId());

            // show hamburger only on top-level using drawerToggle
            try {
                if (drawerToggle != null) {
                    drawerToggle.setDrawerIndicatorEnabled(isTop);
                    drawerToggle.syncState();
                }
            } catch (Exception ignored) {}

            // show up affordance when not top (NavigationUI will also manage this)
            if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(!isTop);

            // update checked item
            try { navigationView.setCheckedItem(destination.getId()); } catch (Exception ignored) {}

            // lock drawer when not top
            try {
                if (isTop) drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                else drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } catch (Exception ignored) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (navController != null) {
            int id = item.getItemId();
            if (id == R.id.action_profile) {
                navController.navigate(R.id.profileFragment);
                return true;
            } else if (id == R.id.action_about) {
                navController.navigate(R.id.aboutFragment);
                return true;
            }

            // Delegate other navigation items to NavigationUI (this handles up/hamburger and nav menu actions)
            if (NavigationUI.onNavDestinationSelected(item, navController)) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                if (drawer != null) drawer.closeDrawers();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
