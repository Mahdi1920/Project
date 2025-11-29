package tn.esprit.project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tn.esprit.project.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // On charge le layout fragment_home.xml
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Boutons
        Button btnAdd = view.findViewById(R.id.btnAddRestaurant);
        Button btnView = view.findViewById(R.id.btnViewRestaurants);

        // Cliquer sur Ajouter restaurant
        btnAdd.setOnClickListener(v -> {
            if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new AddRestaurantFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnView.setOnClickListener(v -> {
            if (isAdded() && getActivity() != null && !getActivity().isFinishing()) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new RestaurantListFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
