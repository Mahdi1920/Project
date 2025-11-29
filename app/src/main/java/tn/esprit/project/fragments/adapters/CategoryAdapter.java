package tn.esprit.project.fragments.adapters;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.fragments.RestaurantDetailsFragment;
import tn.esprit.project.fragments.UpdateRestaurantFragment;
import tn.esprit.project.models.Category;
import tn.esprit.project.models.Restaurant;
import tn.esprit.project.utils.MyDatabase;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private MyDatabase db;

    public CategoryAdapter(List<Category> categories, MyDatabase db) {
        this.categories = categories;
        this.db = db;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvCategoryTitle.setText(category.name());

        List<Restaurant> restaurants = db.restaurantDAO().getByCategory(category);

        if (restaurants.isEmpty()) {
            holder.tvEmpty.setVisibility(View.VISIBLE);
            holder.rvRestaurants.setVisibility(View.GONE);
        } else {
            holder.tvEmpty.setVisibility(View.GONE);
            holder.rvRestaurants.setVisibility(View.VISIBLE);

            holder.rvRestaurants.setLayoutManager(
                    new LinearLayoutManager(holder.rvRestaurants.getContext(),
                            LinearLayoutManager.HORIZONTAL, false)
            );
            holder.rvRestaurants.setAdapter(
                    new RestaurantHorizontalAdapter(restaurants, new RestaurantHorizontalAdapter.OnRestaurantClickListener() {
                        @Override
                        public void onDetailsClick(Restaurant restaurant) {
                            // Ouvrir RestaurantDetailsFragment
                            RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putInt("restaurant_id", restaurant.getId());
                            fragment.setArguments(bundle);

                            // Exécuter la transaction
                            ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, fragment) // fragment_container doit exister dans ton layout principal
                                    .addToBackStack(null)
                                    .commit();
                        }

                        @Override
                        public void onDeleteClick(Restaurant restaurant) {
                            new AlertDialog.Builder(holder.itemView.getContext())
                                    .setTitle("Confirmation")
                                    .setMessage("Voulez-vous vraiment supprimer ce restaurant ?")
                                    .setPositiveButton("Oui", (dialog, which) -> {
                                        db.restaurantDAO().delete(restaurant);
                                        restaurants.remove(restaurant);
                                        notifyDataSetChanged();

                                        // Afficher un message en bas
                                        Snackbar.make(holder.itemView, "Restaurant supprimé", Snackbar.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }

                        @Override
                        public void onUpdateClick(Restaurant restaurant) {
                            // ouvrir UpdateRestaurantFragment
                            UpdateRestaurantFragment fragment = UpdateRestaurantFragment.newInstance(restaurant.getId());
                            ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    })
            );
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryTitle;
        RecyclerView rvRestaurants;
        TextView tvEmpty;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryTitle = itemView.findViewById(R.id.tvCategoryTitle);
            rvRestaurants = itemView.findViewById(R.id.rvRestaurantsHorizontal);
            tvEmpty = itemView.findViewById(R.id.tvEmptyCategory);
        }
    }

}
