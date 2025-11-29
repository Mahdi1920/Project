package tn.esprit.project.ui.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.models.Menu;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.VH> {
    private List<Menu> items = new ArrayList<>();
    private OnMenuClickListener listener;

    public interface OnMenuClickListener {
        void onMenuClick(Menu menu);
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Menu> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Menu m = items.get(position);
        holder.name.setText(m.getName());
        holder.desc.setText(m.getDescription() != null ? m.getDescription() : "");

        // Hide price container (this view is for menus, not menu items)
        if (holder.priceContainer != null) holder.priceContainer.setVisibility(View.GONE);

        // load image from imageUrl similar to RestaurantsAdapter (no external library)
        String img = m.getImageUrl();
        if (img != null && !img.isEmpty()) {
            String resName = img;
            if (resName.startsWith("@drawable/")) resName = resName.substring("@drawable/".length());
            else if (resName.startsWith("@mipmap/")) resName = resName.substring("@mipmap/".length());

            int resId = holder.img.getContext().getResources().getIdentifier(resName, "drawable", holder.img.getContext().getPackageName());
            if (resId == 0) {
                resId = holder.img.getContext().getResources().getIdentifier(resName, "mipmap", holder.img.getContext().getPackageName());
            }

            if (resId != 0) {
                holder.img.setImageResource(resId);
            } else {
                try {
                    if (img.startsWith("content:") || img.startsWith("file:")) {
                        holder.img.setImageURI(Uri.parse(img));
                    } else {
                        // remote URLs (http/https) are not loaded without an image library; use placeholder
                        holder.img.setImageResource(android.R.drawable.ic_menu_report_image);
                    }
                } catch (Exception e) {
                    holder.img.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            }
        } else {
            holder.img.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMenuClick(m);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, desc;
        LinearLayout priceContainer;
        Button btnAdd;

        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_menu);
            name = itemView.findViewById(R.id.tv_menu_name);
            desc = itemView.findViewById(R.id.tv_menu_desc);
            priceContainer = itemView.findViewById(R.id.price_container);
        }
    }
}
