package tn.esprit.project;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;

import tn.esprit.project.models.User;
import tn.esprit.project.utils.AppDatabase;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // populate drawer header from DB (user id 1)
        View header = navigationView.getHeaderView(0);
        ImageView ivAvatar = header.findViewById(R.id.iv_drawer_avatar);
        TextView tvName = header.findViewById(R.id.tv_drawer_name);
        TextView tvEmail = header.findViewById(R.id.tv_drawer_email);

        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.profile_round)
                .error(R.mipmap.profile_round)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 200);

        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                User u = db.userDAO().getById(1);
                if (u != null) {
                    final String name = u.getName() != null ? u.getName() : "Utilisateur Exemple";
                    final String email = u.getEmail() != null ? u.getEmail() : "email@example.com";
                    final String avatar = u.getAvatarUrl() != null ? u.getAvatarUrl() : "";

                    runOnUiThread(() -> {
                        tvName.setText(name);
                        tvEmail.setText(email);

                        try {
                            if (avatar == null || avatar.isEmpty()) {
                                Glide.with(this).load(R.mipmap.profile_round).apply(requestOptions).circleCrop().into(ivAvatar);
                            } else if (avatar.startsWith("@drawable/") || avatar.startsWith("@mipmap/")) {
                                // support both drawable and mipmap resources
                                String resName = avatar.startsWith("@drawable/") ? avatar.substring("@drawable/".length()) : avatar.substring("@mipmap/".length());
                                int resId = getResources().getIdentifier(resName, "drawable", getPackageName());
                                if (resId == 0) resId = getResources().getIdentifier(resName, "mipmap", getPackageName());
                                if (resId != 0) {
                                    Glide.with(this).load(resId).apply(requestOptions).circleCrop().into(ivAvatar);
                                } else {
                                    Glide.with(this).load(R.mipmap.profile_round).apply(requestOptions).circleCrop().into(ivAvatar);
                                }
                            } else if (avatar.startsWith("content:") || avatar.startsWith("file:")) {
                                Glide.with(this).load(Uri.parse(avatar)).apply(requestOptions).circleCrop().into(ivAvatar);
                            } else if (avatar.startsWith("http:") || avatar.startsWith("https:")) {
                                Glide.with(this).load(avatar).apply(requestOptions).circleCrop().into(ivAvatar);
                            } else {
                                // fallback to default
                                Glide.with(this).load(R.mipmap.profile_round).apply(requestOptions).circleCrop().into(ivAvatar);
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Failed to set avatar via Glide", e);
                            Glide.with(this).load(R.mipmap.profile_round).apply(requestOptions).into(ivAvatar);
                        }
                    });
                }
            } catch (Exception e) {
                Log.w(TAG, "Error loading drawer header user", e);
            }
        }).start();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            try {
                navController = androidx.navigation.Navigation.findNavController(this, R.id.nav_host_fragment_container);
            } catch (Exception ignored) {
                navController = null;
            }
        }

        // IDs of top-level destinations
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.restaurantsFragment, R.id.cartFragment, R.id.ordersFragment)
                .setOpenableLayout(drawer)
                .build();

        if (navController != null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            // Let NavigationUI manage the NavigationView selection and navigation
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // Safe fallback listener: if setupWithNavController doesn't handle (rare cases), handle clicks here.
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (navController == null) return false;
            final int targetId = menuItem.getItemId();
            // close drawer then navigate on UI thread to avoid "state saved" issues
            drawer.closeDrawers();
            drawer.post(() -> {
                try {
                    // if target is already current destination -> just close drawer
                    if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == targetId) {
                        return;
                    }

                    // Prefer NavigationUI which handles both direct destinations and actions
                    try {
                        boolean handled = NavigationUI.onNavDestinationSelected(menuItem, navController);
                        if (!handled) {
                            // If not handled by NavigationUI, try direct navigate
                            navController.navigate(targetId);
                        }
                    } catch (IllegalStateException ise) {
                        // state saved - repost a short time later and retry
                        drawer.postDelayed(() -> {
                            try {
                                NavigationUI.onNavDestinationSelected(menuItem, navController);
                            } catch (Exception ex) {
                                try { navController.navigate(targetId); } catch (Exception ignored) {}
                            }
                        }, 60);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Navigation fallback failed", e);
                }
            });
            return true;
        });

        // NavigationView uses NavigationUI.setupWithNavController(navigationView, navController) above.
        // Avoid custom listeners that call navigate() after state saved (causes "FragmentManager has already saved its state").

        // Keep the drawer toggle (hamburger) in sync
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (navController != null) {
            if (item.getItemId() == R.id.action_profile) {
                navController.navigate(R.id.profileFragment);
                return true;
            } else if (item.getItemId() == R.id.action_about) {
                navController.navigate(R.id.aboutFragment);
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
