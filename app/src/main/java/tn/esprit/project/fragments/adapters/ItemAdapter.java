package tn.esprit.project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.models.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;

    public ItemAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item i = items.get(position);
        holder.tvName.setText(i.getName());
        holder.tvDescription.setText(i.getDescription());
        holder.tvPrice.setText(String.format("%.2f €", i.getPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvDescription = itemView.findViewById(R.id.tvItemDescription);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}
