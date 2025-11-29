package tn.esprit.project.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tn.esprit.project.R;
import tn.esprit.project.models.Restaurant;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.ui.adapters.MenuItemAdapter;
import tn.esprit.project.viewmodel.MenuViewModel;
import tn.esprit.project.viewmodel.RestaurantsViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;

public class RestaurantDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int restaurantId = getArguments() != null ? getArguments().getInt("restaurantId", -1) : -1;

        TextView name = view.findViewById(R.id.tv_restaurant_detail_name);
        TextView address = view.findViewById(R.id.tv_restaurant_detail_address);
        RecyclerView rv = view.findViewById(R.id.rv_restaurant_menu);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        ClientRepository repo = new ClientRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repo);

        // Observe restaurants list and find the one with matching id
        RestaurantsViewModel restaurantsVM = new ViewModelProvider(this, factory).get(RestaurantsViewModel.class);
        restaurantsVM.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            if (restaurants != null) {
                for (Restaurant rr : restaurants) {
                    if (rr.getId() == restaurantId) {
                        name.setText(rr.getName());
                        address.setText(rr.getAddress());
                        break;
                    }
                }
            }
        });

        // MenuViewModel to observe menu items
        MenuViewModel menuVM = new ViewModelProvider(this, factory).get(MenuViewModel.class);
        MenuItemAdapter adapter = new MenuItemAdapter();
        rv.setAdapter(adapter);
        menuVM.getMenuForRestaurant(restaurantId).observe(getViewLifecycleOwner(), list -> adapter.setItems(list != null ? list : java.util.Collections.emptyList()));

        adapter.setOnMenuActionListener(new MenuItemAdapter.OnMenuActionListener() {
            @Override
            public void onAddToCart(int menuItemId, View v) {
                repo.addToCart(menuItemId, 1, 1);
            }

            @Override
            public void onCustomize(int menuItemId, View v) {
                tn.esprit.project.ui.fragments.SandwichCustomizationFragment frag = new tn.esprit.project.ui.fragments.SandwichCustomizationFragment();
                Bundle b = new Bundle();
                b.putInt("menuItemId", menuItemId);
                frag.setArguments(b);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.nav_host_fragment_container, frag)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
