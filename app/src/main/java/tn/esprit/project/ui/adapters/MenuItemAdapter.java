package tn.esprit.project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.models.MenuItem;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.VH> {
    private List<MenuItem> items = new ArrayList<>();
    private OnMenuActionListener actionListener;

    public interface OnMenuActionListener {
        void onAddToCart(int menuItemId, View v);
        void onCustomize(int menuItemId, View v);
    }

    public MenuItemAdapter() {}

    public MenuItemAdapter(OnMenuActionListener listener) {
        this.actionListener = listener;
    }

    public void setItems(List<MenuItem> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    public void setOnMenuActionListener(OnMenuActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MenuItem m = items.get(position);
        holder.name.setText(m.getName());
        holder.price.setText(String.valueOf(m.getPrice()));
        // Load image with Glide using placeholder and size limit to avoid huge bitmaps
        RequestOptions opts = new RequestOptions()
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .override(300, 300);
        Glide.with(holder.img.getContext()).load(m.getImageUrl()).apply(opts).into(holder.img);

        String lowerName = (m.getName() != null) ? m.getName().toLowerCase() : "";
        String lowerDesc = (m.getDescription() != null) ? m.getDescription().toLowerCase() : "";
        boolean isSandwich = lowerName.contains("sandwich") || lowerDesc.contains("sandwich");

        if (isSandwich) {
            holder.add.setText("Customize");
            holder.add.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onCustomize(m.getId(), v);
            });
        } else {
            holder.add.setText("Add to Cart");
            holder.add.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onAddToCart(m.getId(), v);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView name, price, desc;
        Button add;
        ImageView img;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_item_name);
            price = itemView.findViewById(R.id.tv_item_price);
            img = itemView.findViewById(R.id.iv_item_image);
            add = itemView.findViewById(R.id.btn_item_add);
        }
    }
}
