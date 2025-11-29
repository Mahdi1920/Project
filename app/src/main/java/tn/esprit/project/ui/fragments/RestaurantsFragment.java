package tn.esprit.project.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tn.esprit.project.R;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.ui.adapters.RestaurantsAdapter;
import tn.esprit.project.viewmodel.RestaurantsViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;

public class RestaurantsFragment extends Fragment {
    private RestaurantsViewModel viewModel;
    private RecyclerView rv;
    private RestaurantsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurants, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rv = view.findViewById(R.id.rv_restaurants);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RestaurantsAdapter();
        rv.setAdapter(adapter);

        // create repository from DB singleton
        ClientRepository repo = new ClientRepository(requireContext());
        viewModel = new ViewModelProvider(this, new ViewModelFactory(repo)).get(RestaurantsViewModel.class);

        adapter.setOnRestaurantClickListener(restaurant -> {
            Bundle args = new Bundle();
            args.putInt("restaurantId", restaurant.getId());
            args.putString("restaurantName", restaurant.getName());

            // Open MenusFragment for the selected restaurant
            MenuListFragment fragment = new MenuListFragment();
            fragment.setArguments(args);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        viewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            adapter.setItems(restaurants);
        });
    }
}
