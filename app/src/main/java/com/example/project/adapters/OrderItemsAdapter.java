package com.example.project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project.R;
import com.example.project.models.OrderItem;
import java.util.ArrayList;
import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {

    private List<OrderItem> items = new ArrayList<>();
    private OnItemRemoveListener listener;

    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    public OrderItemsAdapter(OnItemRemoveListener listener) {
        this.listener = listener;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public List<OrderItem> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemDetails, tvSubtotal;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemDetails = itemView.findViewById(R.id.tvItemDetails);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(OrderItem item, int position) {
            tvItemName.setText(item.getItemName());
            tvItemDetails.setText(String.format("Qty: %d x %.2f DT", item.getQuantity(), item.getPrice()));
            tvSubtotal.setText(String.format("%.2f DT", item.getSubtotal()));

            btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemove(position);
                }
            });
        }
    }
}