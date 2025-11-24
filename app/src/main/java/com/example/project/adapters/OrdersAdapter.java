package com.example.project.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.models.Commande;
import com.example.project.utils.FirebaseHelper;
import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Commande> orders = new ArrayList<>();
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Commande commande);
        void onActionClick(Commande commande);
    }

    public OrdersAdapter(OnOrderClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Commande> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Commande commande = orders.get(position);
        holder.bind(commande);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvRestaurantName, tvClientName, tvClientAddress, tvTotalPrice;
        Button btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvClientAddress = itemView.findViewById(R.id.tvClientAddress);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        public void bind(Commande commande) {
            tvOrderId.setText("CMD-" + commande.getCommandeId().substring(0, 8));
            tvRestaurantName.setText(commande.getRestaurantName());
            tvClientName.setText(commande.getClientName());
            tvClientAddress.setText(commande.getClientAddress());
            tvTotalPrice.setText(String.format("%.2f DT", commande.getTotalPrice()));

            // Set status
            String status = commande.getStatus();
            tvStatus.setText(getStatusText(status));
            tvStatus.setBackgroundColor(getStatusColor(status));

            // Set button based on status
            setupActionButton(commande);

            // Click listeners
            itemView.setOnClickListener(v -> listener.onOrderClick(commande));
            btnAction.setOnClickListener(v -> listener.onActionClick(commande));
        }

        private void setupActionButton(Commande commande) {
            String status = commande.getStatus();

            if (status.equals(FirebaseHelper.STATUS_PENDING)) {
                btnAction.setText("Accepter");
                btnAction.setVisibility(View.VISIBLE);
            } else if (status.equals(FirebaseHelper.STATUS_ACCEPTED)) {
                btnAction.setText("Récupéré");
                btnAction.setVisibility(View.VISIBLE);
            } else if (status.equals(FirebaseHelper.STATUS_PICKED_UP)) {
                btnAction.setText("Livrer");
                btnAction.setVisibility(View.VISIBLE);
            } else {
                btnAction.setVisibility(View.GONE);
            }
        }

        private String getStatusText(String status) {
            switch (status) {
                case FirebaseHelper.STATUS_PENDING:
                    return "En attente";
                case FirebaseHelper.STATUS_ACCEPTED:
                    return "Acceptée";
                case FirebaseHelper.STATUS_PICKED_UP:
                    return "Récupérée";
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