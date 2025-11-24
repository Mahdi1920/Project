package com.example.project.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.models.Commande;
import com.example.project.utils.FirebaseHelper;
import java.util.ArrayList;
import java.util.List;
import com.example.project.R;

public class ClientOrdersAdapter extends RecyclerView.Adapter<ClientOrdersAdapter.ViewHolder> {

    private List<Commande> orders = new ArrayList<>();
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Commande commande);
        void onTrackClick(Commande commande);
    }

    public ClientOrdersAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Commande> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Commande commande = orders.get(position);
        holder.bind(commande);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvRestaurantName, tvDeliveryPerson, tvTotalPrice;
        Button btnTrack;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvDeliveryPerson = itemView.findViewById(R.id.tvDeliveryPerson);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnTrack = itemView.findViewById(R.id.btnTrack);
        }

        public void bind(Commande commande) {
            tvOrderId.setText("CMD-" + commande.getCommandeId().substring(0, 8));
            tvRestaurantName.setText(commande.getRestaurantName());
            tvTotalPrice.setText(String.format("%.2f DT", commande.getTotalPrice()));

            // Set status
            String status = commande.getStatus();
            tvStatus.setText(getStatusText(status));
            tvStatus.setBackgroundColor(getStatusColor(status));

            // Set delivery person
            if (commande.getLivreurName() != null) {
                tvDeliveryPerson.setText("Livreur: " + commande.getLivreurName());
            } else {
                tvDeliveryPerson.setText("Livreur: Non assigné");
            }

            // Show track button only if order is accepted or picked up
            if (status.equals(FirebaseHelper.STATUS_ACCEPTED) ||
                    status.equals(FirebaseHelper.STATUS_PICKED_UP)) {
                btnTrack.setVisibility(View.VISIBLE);
            } else {
                btnTrack.setVisibility(View.GONE);
            }

            // Click listeners
            itemView.setOnClickListener(v -> listener.onOrderClick(commande));
            btnTrack.setOnClickListener(v -> listener.onTrackClick(commande));
        }

        private String getStatusText(String status) {
            switch (status) {
                case FirebaseHelper.STATUS_PENDING:
                    return "En attente";
                case FirebaseHelper.STATUS_ACCEPTED:
                    return "Acceptée";
                case FirebaseHelper.STATUS_PICKED_UP:
                    return "En cours";
                case FirebaseHelper.STATUS_DELIVERED:
                    return "Livrée";
                case FirebaseHelper.STATUS_CANCELLED:
                    return "Annulée";
                default:
                    return status;
            }
        }

        private int getStatusColor(String status) {
            switch (status) {
                case FirebaseHelper.STATUS_PENDING:
                    return Color.parseColor("#FF9800");
                case FirebaseHelper.STATUS_ACCEPTED:
                    return Color.parseColor("#2196F3");
                case FirebaseHelper.STATUS_PICKED_UP:
                    return Color.parseColor("#9C27B0");
                case FirebaseHelper.STATUS_DELIVERED:
                    return Color.parseColor("#4CAF50");
                case FirebaseHelper.STATUS_CANCELLED:
                    return Color.parseColor("#F44336");
                default:
                    return Color.GRAY;
            }
        }
    }
}