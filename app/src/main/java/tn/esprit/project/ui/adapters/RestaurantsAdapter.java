package tn.esprit.project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.models.Restaurant;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.VH> {
    private List<Restaurant> items = new ArrayList<>();
    private OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public void setOnRestaurantClickListener(OnRestaurantClickListener l) {
        this.listener = l;
    }

    public void setItems(List<Restaurant> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Restaurant r = items.get(position);
        holder.name.setText(r.getName());
        holder.address.setText(r.getAddress());

        // imageUrl stored in model is a String. Try to resolve a drawable resource name if present
        String img = r.getImageUrl();
        if (img != null && !img.isEmpty()) {
            String resName = img;
            if (resName.startsWith("@drawable/")) {
                resName = resName.substring("@drawable/".length());
            }
            int resId = holder.image.getContext().getResources().getIdentifier(resName, "drawable", holder.image.getContext().getPackageName());
            RequestOptions opts = new RequestOptions().placeholder(android.R.drawable.ic_menu_report_image).error(android.R.drawable.ic_menu_report_image).centerCrop().override(400,300);
            if (resId != 0) {
                Glide.with(holder.image.getContext()).load(resId).apply(opts).into(holder.image);
            } else {
                // try as URL
                Glide.with(holder.image.getContext()).load(img).apply(opts).into(holder.image);
            }
        } else {
            Glide.with(holder.image.getContext()).load(android.R.drawable.ic_menu_report_image).into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRestaurantClick(r);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView name, address;
        ImageView image;
        VH(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_restaurant_name);
            address = itemView.findViewById(R.id.tv_restaurant_sub);
            image = (ImageView) itemView.findViewById(R.id.img_restaurant);
        }
    }
}
