package tn.esprit.project.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

import tn.esprit.project.R;
import tn.esprit.project.models.Restaurant;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.ui.adapters.MenuListAdapter;
import tn.esprit.project.utils.NavigationUtils;
import tn.esprit.project.viewmodel.RestaurantsViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;

public class MenuListFragment extends Fragment {
    public MenuListFragment() {}

    public static MenuListFragment newInstance(int restaurantId, String restaurantName) {
        MenuListFragment f = new MenuListFragment();
        Bundle b = new Bundle();
        b.putInt("restaurantId", restaurantId);
        b.putString("restaurantName", restaurantName);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int restaurantId = getArguments() != null ? getArguments().getInt("restaurantId", -1) : -1;
        String restaurantName = getArguments() != null ? getArguments().getString("restaurantName", "") : "";

        ImageView iv = view.findViewById(R.id.iv_restaurant_image);
        TextView tvName = view.findViewById(R.id.tv_restaurant_name);
        TextView tvAddress = view.findViewById(R.id.tv_restaurant_address);

        RecyclerView rv = view.findViewById(R.id.rv_menus);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        MenuListAdapter adapter = new MenuListAdapter();
        rv.setAdapter(adapter);

        ClientRepository repo = new ClientRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repo);

        // populate restaurant header using RestaurantsViewModel
        RestaurantsViewModel restaurantsVM = new ViewModelProvider(this, factory).get(RestaurantsViewModel.class);
        restaurantsVM.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            if (restaurants != null) {
                for (Restaurant r : restaurants) {
                    if (r.getId() == restaurantId) {
                        tvName.setText(r.getName());
                        tvAddress.setText(r.getAddress());
                        // if imageUrl exists, you can load with an image loader (Glide/Picasso) — here keep placeholder
                        break;
                    }
                }
            }
        });

        // observe menus
        tn.esprit.project.viewmodel.MenuViewModel menuVM = new ViewModelProvider(this, factory).get(tn.esprit.project.viewmodel.MenuViewModel.class);
        menuVM.getMenusForRestaurant(restaurantId).observe(getViewLifecycleOwner(), menus -> {
            adapter.setItems(menus != null ? menus : Collections.emptyList());
        });

        adapter.setOnMenuClickListener(menu -> {
            Bundle b = new Bundle();
            b.putInt("menuId", menu.getId());
            b.putString("menuName", menu.getName());
            NavigationUtils.navigateTo(MenuListFragment.this, view, R.id.menuItemsFragment, b);
        });
    }
}
