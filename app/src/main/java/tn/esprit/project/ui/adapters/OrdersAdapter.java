package tn.esprit.project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import tn.esprit.project.R;
import tn.esprit.project.models.OrderItem;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private final List<OrderItem> items = new ArrayList<>();

    public void setItems(List<OrderItem> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem oi = items.get(position);
        // OrderItem doesn't store the menu item's display name by default.
        // Show the menu item id as fallback. You can replace this by a join query or by enriching OrderItem with a name field.
        holder.name.setText("Item #" + oi.getMenuItemId());
        holder.qty.setText(String.valueOf(oi.getQuantity()));
        holder.price.setText(String.format("%.2f TND", oi.getPrice()));
        // sub text optional
        holder.sub.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty, price, sub;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_order_item_name);
            qty = itemView.findViewById(R.id.tv_order_item_qty);
            price = itemView.findViewById(R.id.tv_order_item_price);
            sub = itemView.findViewById(R.id.tv_order_item_sub);
        }
    }
}
