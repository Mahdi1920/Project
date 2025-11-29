package tn.esprit.project.ui.fragments;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import tn.esprit.project.utils.NavigationUtils;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Locale;

import tn.esprit.project.R;
import tn.esprit.project.models.CartItemWithMenuItem;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.ui.adapters.CartAdapter;
import tn.esprit.project.viewmodel.CartViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;

public class CartFragment extends Fragment {
    private CartViewModel viewModel;
    private TextView tvTotal;
    private NavController navController; // moved to field to be accessible from listeners

    private static final int USER_ID = 1; // demo static user
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_CART_PLACEMENT = "cart_card_placement"; // values: "top" or "bottom"

    private boolean isInitialLoad = true;
    private int previousCartSize = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout root = view.findViewById(R.id.cart_root);
        RecyclerView rv = view.findViewById(R.id.rv_cart);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        CartAdapter adapter = new CartAdapter();
        rv.setAdapter(adapter);

        tvTotal = view.findViewById(R.id.tv_total);
        MaterialCardView summaryCard = view.findViewById(R.id.summary_card);
        MaterialButton btnCancel = view.findViewById(R.id.btn_cancel);
        MaterialButton btnValidate = view.findViewById(R.id.btn_validate);
        MaterialButton btnGoRestaurants = view.findViewById(R.id.btn_go_restaurants);

        // Enable simple layout animations when we reorder views
        LayoutTransition lt = new LayoutTransition();
        lt.setDuration(180);
        root.setLayoutTransition(lt);

        // NavController for navigation
        try {
            // resolve using NavigationUtils which wraps multiple safe strategies
            NavController resolved = NavigationUtils.findNavController(this, view);

            if (resolved == null) {
                // try to obtain NavHostFragment by id and get its controller
                try {
                    androidx.fragment.app.Fragment host = requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
                    if (host instanceof NavHostFragment) {
                        resolved = ((NavHostFragment) host).getNavController();
                    }
                } catch (Exception inner) {
                    Log.d("CartFragment", "Host lookup failed: " + inner.getMessage());
                }
            }

            this.navController = resolved;
        } catch (Exception e) {
            Log.e("CartFragment", "Failed to obtain NavController", e);
        }

        // If navController still null, retry once later when views are attached (avoids timing issues)
        if (this.navController == null) {
            view.post(() -> {
                try {
                    NavController delayed = NavigationUtils.findNavController(CartFragment.this, view);
                    if (delayed == null) {
                        try {
                            androidx.fragment.app.Fragment host = requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
                            if (host instanceof NavHostFragment) {
                                delayed = ((NavHostFragment) host).getNavController();
                            }
                        } catch (Exception inner) {
                            Log.d("CartFragment", "Delayed host lookup failed: " + inner.getMessage());
                        }
                    }
                    if (delayed != null) {
                        this.navController = delayed;
                        Log.d("CartFragment", "NavController resolved on delayed post");
                    } else {
                        Log.d("CartFragment", "NavController still null after delayed attempt");
                    }
                } catch (Exception ex) {
                    Log.e("CartFragment", "Delayed NavController resolution failed", ex);
                }
            });
        }

        // initial visibility: keep summary_card visible (we want access always)
        summaryCard.setVisibility(View.VISIBLE);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.getString(KEY_CART_PLACEMENT, "top");

        ClientRepository repo = new ClientRepository(requireContext());
        viewModel = new ViewModelProvider(this, new ViewModelFactory(repo)).get(CartViewModel.class);

        viewModel.observeCart(USER_ID).observe(getViewLifecycleOwner(), list -> {
            adapter.setItems(list);
            updateTotal(list); // will set total to 0 if list null/empty

            boolean empty = (list == null || list.isEmpty());
            int newSize = (list == null) ? 0 : list.size();

            TextView tvEmpty = view.findViewById(R.id.tv_empty_cart);

            if (empty) {
                // Show the summary card even if empty (total will be 0)
                tvEmpty.setVisibility(View.GONE);
                summaryCard.setVisibility(View.VISIBLE);
                // show Go restaurants button
                btnValidate.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btnGoRestaurants.setVisibility(View.VISIBLE);
                btnGoRestaurants.setEnabled(true);

                // move card to top when cart empty
                moveCardToTop(root, summaryCard);

                // no automatic navigation away
                isInitialLoad = false;
            } else {
                tvEmpty.setVisibility(View.GONE);
                if (summaryCard.getVisibility() != View.VISIBLE) {
                    summaryCard.setVisibility(View.VISIBLE);
                    summaryCard.bringToFront();
                    root.post(() -> {
                        root.requestLayout();
                        summaryCard.setAlpha(0f);
                        summaryCard.animate().alpha(1f).setDuration(160).start();
                    });
                }
                btnValidate.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnGoRestaurants.setVisibility(View.GONE);
                btnValidate.setEnabled(true);
                btnCancel.setEnabled(true);

                // animate/minor reposition: if item added -> move to bottom; if removed -> move to top
                if (!isInitialLoad) {
                    if (newSize > previousCartSize) {
                        moveCardToBottom(root, summaryCard);
                    } else if (newSize < previousCartSize) {
                        moveCardToTop(root, summaryCard);
                    }
                } else {
                    // initial load: place appropriately
                    // list is non-empty in this branch, move card to bottom
                    moveCardToBottom(root, summaryCard);
                }

                if (isInitialLoad) isInitialLoad = false;
            }

            previousCartSize = newSize;
        });

        btnCancel.setOnClickListener(v -> {
            if (getContext() == null) return;
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmer annulation")
                    .setMessage("Voulez-vous vraiment vider le panier ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        viewModel.clearCart(USER_ID);
                        Toast.makeText(getContext(), "Panier annulé", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        btnValidate.setOnClickListener(v -> {
            if (getContext() == null) return;
            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmer commande")
                    .setMessage("Confirmez-vous la validation de la commande ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        long orderId = viewModel.placeOrder(USER_ID);
                        if (orderId > 0) {
                            Toast.makeText(getContext(), "Commande validée", Toast.LENGTH_SHORT).show();
                            // Navigate to Restaurants list (main customer flow)
                            try {
                                // robust navigate to restaurants (centralized)
                                NavigationUtils.navigateTo(CartFragment.this, getView(), R.id.restaurantsFragment, null);
                            } catch (Exception e) {
                                // ignore navigation failures
                            }
                        } else {
                            Toast.makeText(getContext(), "Le panier est vide", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        btnGoRestaurants.setOnClickListener(v -> {
            NavigationUtils.navigateTo(CartFragment.this, v, R.id.restaurantsFragment, null);
        });
    }

    private void moveCardToTop(LinearLayout root, View summaryCard) {
        // Place summaryCard right after toolbar (index 1)
        if (root.indexOfChild(summaryCard) == 1) return; // already top
        root.removeView(summaryCard);
        int insertIndex = 1; // after toolbar
        if (insertIndex > root.getChildCount()) insertIndex = root.getChildCount();
        root.addView(summaryCard, insertIndex);
    }

    private void moveCardToBottom(LinearLayout root, View summaryCard) {
        // Place summaryCard at the end
        int lastIndex = root.getChildCount() - 1;
        if (root.indexOfChild(summaryCard) == lastIndex) return; // already bottom
        root.removeView(summaryCard);
        root.addView(summaryCard);
    }

    private void updateTotal(List<CartItemWithMenuItem> list) {
        double total = 0.0;
        if (list != null) {
            for (CartItemWithMenuItem c : list) {
                if (c == null) continue;
                double price = c.getMenuItem() != null ? c.getMenuItem().getPrice() : 0.0;
                int qty = c.getCartItem() != null ? c.getCartItem().getQuantity() : 0;
                total += price * qty;
            }
        }
        tvTotal.setText(String.format(Locale.getDefault(), "Total: %.2f DT", total));
    }
}
