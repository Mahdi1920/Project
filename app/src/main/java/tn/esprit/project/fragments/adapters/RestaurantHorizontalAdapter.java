package tn.esprit.project.fragments.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.models.Restaurant;

public class RestaurantHorizontalAdapter extends RecyclerView.Adapter<RestaurantHorizontalAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private OnRestaurantClickListener listener;

    // Interface pour gérer les actions
    public interface OnRestaurantClickListener {
        void onDetailsClick(Restaurant restaurant);
        void onDeleteClick(Restaurant restaurant);
        void onUpdateClick(Restaurant restaurant); // <-- nouveau bouton Update
    }

    public RestaurantHorizontalAdapter(List<Restaurant> restaurants, OnRestaurantClickListener listener) {
        this.restaurants = restaurants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_horizontal, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.tvName.setText(restaurant.getName());
        holder.tvAddress.setText(restaurant.getAddress());

        Glide.with(holder.ivLogo.getContext())
                .load(restaurant.getLogoUri())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivLogo);

        // Actions des boutons
        holder.btnDetails.setOnClickListener(v -> {
            if (listener != null) listener.onDetailsClick(restaurant);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(restaurant);
        });

        holder.btnUpdate.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateClick(restaurant); // <-- action Update
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName, tvAddress;
        ImageButton btnDetails, btnDelete, btnUpdate; // <-- ajouter btnUpdate

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.ivLogo);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate); // <-- lier le bouton
        }
    }
}
