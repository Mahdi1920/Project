package tn.esprit.project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import tn.esprit.project.R;
import tn.esprit.project.models.OrderStatusUpdate;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ViewHolder> {

    private final List<OrderStatusUpdate> items = new ArrayList<>();

    public void setItems(List<OrderStatusUpdate> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status_update, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderStatusUpdate s = items.get(position);
        holder.text.setText(s.getStatus());
        String time = DateFormat.getDateTimeInstance().format(new Date(s.getTimestamp()));
        holder.time.setText(time);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text, time;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_status_text);
            time = itemView.findViewById(R.id.tv_status_time);
        }
    }
}

