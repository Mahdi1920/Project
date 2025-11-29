package tn.esprit.project.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import tn.esprit.project.R;

public final class NavigationUtils {
    private static final String TAG = "NavigationUtils";

    private NavigationUtils() {}

    /**
     * Try to resolve a NavController in a robust order:
     * 1) Navigation.findNavController(poster)
     * 2) Navigation.findNavController(fragment.getView())
     * 3) Navigation.findNavController(fragment.getActivity(), nav_host_id)
     * 4) find NavHostFragment by id and get its controller
     */
    @Nullable
    public static NavController findNavController(Fragment fragment, @Nullable View poster) {
        if (fragment == null) return null;

        try {
            if (poster != null) {
                NavController nav = Navigation.findNavController(poster);
                Log.d(TAG, "resolved from poster view");
                return nav;
            }
        } catch (Exception e) {
            Log.d(TAG, "poster lookup failed: " + e.getMessage());
        }

        // Try resolving from fragment's view (safe: may throw if view not attached)
        try {
            if (fragment.getView() != null) {
                NavController nav = Navigation.findNavController(fragment.getView());
                Log.d(TAG, "resolved from Navigation.findNavController(view)");
                return nav;
            }
        } catch (Exception e) {
            Log.d(TAG, "Navigation.findNavController(view) failed: " + e.getMessage());
        }

        try {
            if (fragment.getActivity() != null) {
                NavController nav = Navigation.findNavController(fragment.getActivity(), R.id.nav_host_fragment_container);
                Log.d(TAG, "resolved from Navigation.findNavController(activity)");
                return nav;
            }
        } catch (Exception e) {
            Log.d(TAG, "Navigation.findNavController(activity) failed: " + e.getMessage());
        }

        try {
            androidx.fragment.app.Fragment host = fragment.requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
            if (host instanceof NavHostFragment) {
                NavController nav = ((NavHostFragment) host).getNavController();
                Log.d(TAG, "resolved from host fragment");
                return nav;
            }
        } catch (Exception e) {
            Log.d(TAG, "fragment manager lookup failed: " + e.getMessage());
        }

        Log.d(TAG, "all attempts to resolve NavController failed");
        return null;
    }

    /**
     * Safe navigate: tries to navigate and if IllegalStateException occurs, posts the navigation to UI thread.
     * Does not attempt arbitrary manual fragment creation (caller can fallback if navController is null).
     */
    public static void safeNavigate(Fragment fragment, @Nullable View poster, NavController navController, int destinationId, @Nullable Bundle args) {
        if (navController == null) return;

        try {
            navController.navigate(destinationId, args);
            return;
        } catch (IllegalStateException ise) {
            try {
                if (poster != null) poster.post(() -> {
                    try { navController.navigate(destinationId, args); } catch (Exception ignored) {}
                });
                else if (fragment.getView() != null) fragment.getView().post(() -> {
                    try { navController.navigate(destinationId, args); } catch (Exception ignored) {}
                });
                else fragment.requireActivity().runOnUiThread(() -> {
                    try { navController.navigate(destinationId, args); } catch (Exception ignored) {}
                });
            } catch (Exception e) {
                Log.d(TAG, "safeNavigate repost failed: " + e.getMessage());
            }
        } catch (Exception e) {
            Log.e(TAG, "safeNavigate failed", e);
        }
    }

    /**
     * Robust navigation helper: attempts to resolve a NavController and navigate to destinationId.
     * If no NavController is yet available, it will retry a few times via posts. If still unavailable
     * it falls back to replacing the nav host fragment's content directly.
     */
    public static void navigateTo(Fragment fragment, @Nullable View poster, int destinationId, @Nullable Bundle args) {
        if (fragment == null) return;

        NavController nav = findNavController(fragment, poster);
        if (nav != null) {
            try {
                nav.navigate(destinationId, args);
                return;
            } catch (IllegalStateException ise) {
                Log.d(TAG, "navigate immediate failed, will repost: " + ise.getMessage());
                // fallthrough to repost below
            } catch (Exception e) {
                Log.e(TAG, "navigate immediate error", e);
            }
        }

        // Retry posting to the view/fragment root a few times
        Runnable tryNavigate = new Runnable() {
            int attempts = 0;
            @Override
            public void run() {
                try {
                    NavController n = findNavController(fragment, poster);
                    if (n != null) {
                        try { n.navigate(destinationId, args); return; } catch (Exception e) { Log.d(TAG, "retry navigate failed: " + e.getMessage()); }
                    }
                } catch (Exception e) { Log.d(TAG, "retry resolution failed: " + e.getMessage()); }

                attempts++;
                if (attempts < 4) {
                    // schedule another attempt shortly
                    if (poster != null) poster.postDelayed(this, 80);
                    else if (fragment.getView() != null) fragment.getView().postDelayed(this, 80);
                    else fragment.requireActivity().getWindow().getDecorView().postDelayed(this, 80);
                } else {
                    // final fallback: if no NavController available, try manual fragment replacement inside host
                    try {
                        androidx.fragment.app.FragmentManager fm = fragment.requireActivity().getSupportFragmentManager();
                        androidx.fragment.app.Fragment dest = null;
                        // attempt to instantiate known fragments by id mapping if necessary
                        // As a safe generic fallback, navigate by id via NavigationUI is not possible here; we try manual replace for top-level destinations
                        if (destinationId == R.id.restaurantsFragment) dest = new tn.esprit.project.ui.fragments.RestaurantsFragment();
                        else if (destinationId == R.id.cartFragment) dest = new tn.esprit.project.ui.fragments.CartFragment();
                        else if (destinationId == R.id.ordersFragment) dest = new tn.esprit.project.ui.fragments.OrdersFragment();
                        else if (destinationId == R.id.profileFragment) dest = new tn.esprit.project.ui.fragments.ProfileFragment();

                        if (dest != null) {
                            fm.beginTransaction().replace(R.id.nav_host_fragment_container, dest).addToBackStack(null).commitAllowingStateLoss();
                        } else {
                            Log.w(TAG, "navigateTo: no fallback fragment mapping for " + destinationId);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "navigateTo fallback failed", e);
                    }
                }
            }
        };

        // start first retry
        if (poster != null) poster.post(tryNavigate);
        else if (fragment.getView() != null) fragment.getView().post(tryNavigate);
        else fragment.requireActivity().getWindow().getDecorView().post(tryNavigate);
    }
}
