package com.example.project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.models.OrderItem;
import java.util.ArrayList;
import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder> {

    private List<OrderItem> items = new ArrayList<>();
    private final OnItemRemoveListener listener;

    // Interface to communicate back to the Activity
    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    public OrderItemsAdapter(OnItemRemoveListener listener) {
        this.listener = listener;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        notifyDataSetChanged(); // Refresh the list
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // You will need to create this layout file: "order_item_row.xml"
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_row, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final TextView tvItemDetails;
        private final TextView tvItemSubtotal;
        private final ImageButton btnRemoveItem;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDetails = itemView.findViewById(R.id.tvItemDetails);
            tvItemSubtotal = itemView.findViewById(R.id.tvItemSubtotal);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);

            btnRemoveItem.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemRemove(position);
                }
            });
        }

        void bind(OrderItem item) {
            tvItemName.setText(item.getItemName());
            tvItemDetails.setText(String.format("Qty: %d x %.2f DT", item.getQuantity(), item.getPrice()));
            tvItemSubtotal.setText(String.format("%.2f DT", item.getSubtotal()));
        }
    }
}
