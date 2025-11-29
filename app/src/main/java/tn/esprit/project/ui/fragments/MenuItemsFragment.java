package tn.esprit.project.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

import tn.esprit.project.R;
import tn.esprit.project.utils.NavigationUtils;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.ui.adapters.MenuItemAdapter;
import tn.esprit.project.viewmodel.MenuViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;
import tn.esprit.project.viewmodel.CartViewModel;

public class MenuItemsFragment extends Fragment {
    private static final String TAG = "MenuItemsFragment";
    public MenuItemsFragment() {}

    private static final int USER_ID = 1; // demo user id, replace with actual session id if available

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int menuId = getArguments() != null ? getArguments().getInt("menuId", -1) : -1;

        RecyclerView rv = view.findViewById(R.id.rv_menu_items);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        MenuItemAdapter adapter = new MenuItemAdapter();
        rv.setAdapter(adapter);

        ClientRepository repo = new ClientRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repo);
        MenuViewModel menuVM = new ViewModelProvider(this, factory).get(MenuViewModel.class);
        CartViewModel cartVM = new ViewModelProvider(this, factory).get(CartViewModel.class);

        menuVM.getItemsForMenu(menuId).observe(getViewLifecycleOwner(), list -> adapter.setItems(list != null ? list : Collections.emptyList()));

        // Do not capture navController (may become stale); resolve it at action time
        adapter.setOnMenuActionListener(new MenuItemAdapter.OnMenuActionListener() {
            @Override
            public void onAddToCart(int menuItemId, View v) {
                // add with quantity=1 for now
                cartVM.addToCart(menuItemId, 1, USER_ID);

                // Show dialog: continue shopping or go to cart
                new AlertDialog.Builder(requireContext())
                        .setTitle("Article ajouté")
                        .setMessage("L'article a été ajouté au panier. Continuer vos achats ou aller au panier ?")
                        .setPositiveButton("Aller au panier", (dialog, which) -> {
                            // robust navigate to cart
                            NavigationUtils.navigateTo(MenuItemsFragment.this, v, R.id.cartFragment, null);
                        })
                        .setNegativeButton("Continuer", (dialog, which) -> {
                            // do nothing, just dismiss to continue shopping
                            Toast.makeText(requireContext(), "Continuer vos achats", Toast.LENGTH_SHORT).show();
                        })
                        .setCancelable(true)
                        .show();
            }

            @Override
            public void onCustomize(int menuItemId, View v) {
                Bundle b = new Bundle();
                b.putInt("menuItemId", menuItemId);
                NavigationUtils.navigateTo(MenuItemsFragment.this, v, R.id.sandwichCustomizationFragment, b);
             }
         });
     }

 }
