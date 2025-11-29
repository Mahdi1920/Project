package tn.esprit.project.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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

            // Use NavController from the view to navigate via Navigation Component
            try {
                Navigation.findNavController(view).navigate(R.id.action_restaurants_to_menu, args);
            } catch (Exception e) {
                // fallback: try navigate directly to menuFragment id
                try { Navigation.findNavController(view).navigate(R.id.menuFragment, args); } catch (Exception ignored) {}
            }
        });

        viewModel.getRestaurants().observe(getViewLifecycleOwner(), restaurants -> {
            adapter.setItems(restaurants);
        });
    }
}
