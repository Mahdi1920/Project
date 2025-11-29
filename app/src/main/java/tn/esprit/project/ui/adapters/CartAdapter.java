package tn.esprit.project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tn.esprit.project.R;
import tn.esprit.project.models.CartItem;
import tn.esprit.project.models.CartItemWithMenuItem;
import tn.esprit.project.repository.ClientRepository;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {
    private List<CartItemWithMenuItem> items = new ArrayList<>();

    public void setItems(List<CartItemWithMenuItem> list) {
        this.items = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CartItemWithMenuItem cm = items.get(position);
        if (cm != null) {
            holder.name.setText(cm.getMenuItem().getName());
            holder.price.setText(String.format(Locale.getDefault(), "%.2f DT", cm.getMenuItem().getPrice()));
            holder.quantity.setText(String.valueOf(cm.getCartItem().getQuantity()));

            ClientRepository repo = new ClientRepository(holder.itemView.getContext());

            holder.remove.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                CartItemWithMenuItem removed = items.get(pos);
                repo.removeCartItem(removed.getCartItem());
                items.remove(pos);
                notifyItemRemoved(pos);
                Toast.makeText(v.getContext(), "Item removed", Toast.LENGTH_SHORT).show();
            });

            holder.btnIncrease.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                CartItemWithMenuItem item = items.get(pos);
                int newQty = item.getCartItem().getQuantity() + 1;
                item.getCartItem().setQuantity(newQty);
                repo.updateCartQuantity(item.getCartItem().getId(), newQty);
                notifyItemChanged(pos);
                Toast.makeText(v.getContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
            });

            holder.btnDecrease.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                CartItemWithMenuItem item = items.get(pos);
                int newQty = item.getCartItem().getQuantity() - 1;
                if (newQty <= 0) {
                    repo.removeCartItem(item.getCartItem());
                    items.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(v.getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                } else {
                    item.getCartItem().setQuantity(newQty);
                    repo.updateCartQuantity(item.getCartItem().getId(), newQty);
                    notifyItemChanged(pos);
                    Toast.makeText(v.getContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        View remove;
        MaterialButton btnIncrease, btnDecrease;
        VH(@NonNull View itemView) {
            super(itemView);
            // Use the IDs defined in item_cart.xml
            name = itemView.findViewById(R.id.tv_cart_name);
            price = itemView.findViewById(R.id.tv_cart_price);
            quantity = itemView.findViewById(R.id.tv_quantity);
            remove = itemView.findViewById(R.id.btn_remove);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
