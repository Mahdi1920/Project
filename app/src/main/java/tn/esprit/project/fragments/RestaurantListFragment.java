package tn.esprit.project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.fragments.adapters.CategoryAdapter;
import tn.esprit.project.models.Category;
import tn.esprit.project.utils.MyDatabase;

public class RestaurantListFragment extends Fragment {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private MyDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        // Récupérer bouton retour
        Button btnBack = view.findViewById(R.id.btnBack);

        // Action du bouton retour → retour vers HomeFragment
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()) // redirection
                    .addToBackStack(null)
                    .commit();
        });

        // RecyclerView catégories
        rvCategories = view.findViewById(R.id.rvCategories);
        db = MyDatabase.getInstance(getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvCategories.setLayoutManager(layoutManager);
        rvCategories.setNestedScrollingEnabled(true);

        loadCategories();

        return view;
    }

    private void loadCategories() {
        List<Category> categories = Arrays.asList(Category.values());
        categoryAdapter = new CategoryAdapter(categories, db);
        rvCategories.setAdapter(categoryAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
    }
}
