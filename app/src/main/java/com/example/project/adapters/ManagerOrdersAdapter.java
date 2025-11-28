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

public class ManagerOrdersAdapter extends RecyclerView.Adapter<ManagerOrdersAdapter.ViewHolder> {

    private List<Commande> orders = new ArrayList<>();
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onOrderClick(Commande commande);
        void onAcceptOrder(Commande commande);
        void onRejectOrder(Commande commande);
    }

    public ManagerOrdersAdapter(OnOrderActionListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Commande> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager_order, parent, false);
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
        TextView tvOrderId, tvStatus, tvClientName, tvClientAddress, tvTotalPrice;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvClientAddress = itemView.findViewById(R.id.tvClientAddress);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }

        public void bind(Commande commande) {
            tvOrderId.setText("CMD-" + commande.getCommandeId().substring(0, Math.min(8, commande.getCommandeId().length())));
            tvClientName.setText(commande.getClientName());
            tvClientAddress.setText(commande.getClientAddress());
            tvTotalPrice.setText(String.format("%.2f DT", commande.getTotalPrice()));

            // Set status
            String status = commande.getStatus();
            tvStatus.setText(getStatusText(status));
            tvStatus.setBackgroundColor(getStatusColor(status));

            // Show buttons only for pending orders
            if (status.equals(FirebaseHelper.STATUS_PENDING)) {
                btnAccept.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
            } else {
                btnAccept.setVisibility(View.GONE);
                btnReject.setVisibility(View.GONE);
            }

            // Click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onOrderClick(commande);
            });

            btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAcceptOrder(commande);
            });

            btnReject.setOnClickListener(v -> {
                if (listener != null) listener.onRejectOrder(commande);
            });
        }

        private String getStatusText(String status) {
            switch (status) {
                case FirebaseHelper.STATUS_PENDING:
                    return "En attente";
                case FirebaseHelper.STATUS_MANAGER_ACCEPTED:
                    return "Acceptée (En attente livreur)";
                case FirebaseHelper.STATUS_LIVREUR_ACCEPTED:
                    return "Livreur assigné";
                case FirebaseHelper.STATUS_PICKED_UP:
                    return "En cours de livraison";
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
                    return Color.parseColor("#FF9800"); // Orange
                case FirebaseHelper.STATUS_MANAGER_ACCEPTED:
                    return Color.parseColor("#2196F3"); // Blue
                case FirebaseHelper.STATUS_LIVREUR_ACCEPTED:
                    return Color.parseColor("#9C27B0"); // Purple
                case FirebaseHelper.STATUS_PICKED_UP:
                    return Color.parseColor("#FF5722"); // Deep Orange
                case FirebaseHelper.STATUS_DELIVERED:
                    return Color.parseColor("#4CAF50"); // Green
                case FirebaseHelper.STATUS_CANCELLED:
                    return Color.parseColor("#F44336"); // Red
                default:
                    return Color.GRAY;
            }
        }
    }
}