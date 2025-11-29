package tn.esprit.project.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import tn.esprit.project.R;
import tn.esprit.project.models.MenuItem;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.viewmodel.CartViewModel;
import tn.esprit.project.viewmodel.MenuViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;

public class MenuDetailsFragment extends Fragment {
    private MenuViewModel menuViewModel;
    private CartViewModel cartViewModel;
    private TextView name, price, description;
    private Button add;
    private int menuItemId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = view.findViewById(R.id.tv_menu_details_name);
        price = view.findViewById(R.id.tv_menu_details_price);
        description = view.findViewById(R.id.tv_menu_details_description);
        add = view.findViewById(R.id.btn_menu_details_add);

        menuItemId = getArguments() != null ? getArguments().getInt("menuItemId", -1) : -1;

        ClientRepository repo = new ClientRepository(requireContext());
        menuViewModel = new ViewModelProvider(this, new ViewModelFactory(repo)).get(MenuViewModel.class);
        cartViewModel = new ViewModelProvider(this, new ViewModelFactory(repo)).get(CartViewModel.class);

        if (menuItemId != -1) {
            // Use repository directly (getMenuItemById returns LiveData<MenuItem>)
            repo.getMenuItemById(menuItemId).observe(getViewLifecycleOwner(), item -> {
                if (item != null) populate(item);
            });
        }

        add.setOnClickListener(v -> {
            if (menuItemId != -1) {
                int userId = 1; // TODO: replace with actual authenticated user id
                cartViewModel.addToCart(menuItemId, 1, userId);
                Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populate(MenuItem item) {
        name.setText(item.getName());
        price.setText(String.valueOf(item.getPrice()));
        description.setText(item.getDescription() != null ? item.getDescription() : "");
    }
}
