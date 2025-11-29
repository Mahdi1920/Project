package tn.esprit.project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import tn.esprit.project.R;
import tn.esprit.project.models.Order;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {

    public interface OnOrderActionListener {
        void onRequestChangeStatus(int orderId);
    }

    public interface OnOrderClickListener {
        void onOrderClick(int orderId);
    }

    public interface OnOrderTimelineListener {
        void onTimelineClick(int orderId);
    }

    public interface OnOrderMapListener {
        void onMapClick(int orderId);
    }

    public static class OrderDisplay {
        public final Order order;
        public final int itemCount;
        public OrderDisplay(Order order, int itemCount) {
            this.order = order;
            this.itemCount = itemCount;
        }
    }

    private final List<OrderDisplay> items = new ArrayList<>();
    private OnOrderActionListener actionListener;
    private OnOrderClickListener clickListener;
    private OnOrderTimelineListener timelineListener;
    private OnOrderMapListener mapListener;

    public void setOnOrderActionListener(OnOrderActionListener l) { this.actionListener = l; }
    public void setOnOrderClickListener(OnOrderClickListener l) { this.clickListener = l; }
    public void setOnOrderTimelineListener(OnOrderTimelineListener l) { this.timelineListener = l; }
    public void setOnOrderMapListener(OnOrderMapListener l) { this.mapListener = l; }

    public void setItems(List<OrderDisplay> list) {
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
        OrderDisplay od = items.get(position);
        Order o = od.order;
        holder.name.setText(holder.itemView.getContext().getString(R.string.order_number_format, o.getId()));
        holder.qty.setText(String.valueOf(od.itemCount));
        holder.price.setText(String.format(java.util.Locale.getDefault(), holder.itemView.getContext().getString(R.string.order_price_format), o.getTotalPrice()));
        holder.sub.setVisibility(View.VISIBLE);
        String statusText = o.getStatus() != null ? o.getStatus() : holder.itemView.getContext().getString(R.string.status_unknown);
        holder.sub.setText(holder.itemView.getContext().getString(R.string.order_status_prefix, statusText));

        holder.itemView.setOnLongClickListener(v -> {
            if (actionListener != null) actionListener.onRequestChangeStatus(o.getId());
            return true;
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onOrderClick(o.getId());
        });

        holder.btnTimeline.setOnClickListener(v -> {
            if (timelineListener != null) timelineListener.onTimelineClick(o.getId());
        });

        holder.btnMap.setOnClickListener(v -> {
            if (mapListener != null) mapListener.onMapClick(o.getId());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty, price, sub;
        Button btnTimeline, btnMap;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_order_item_name);
            qty = itemView.findViewById(R.id.tv_order_item_qty);
            price = itemView.findViewById(R.id.tv_order_item_price);
            sub = itemView.findViewById(R.id.tv_order_item_sub);
            btnTimeline = itemView.findViewById(R.id.btn_order_timeline);
            btnMap = itemView.findViewById(R.id.btn_order_map);
        }
    }
}
