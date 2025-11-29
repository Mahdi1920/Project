package tn.esprit.project.adapters;

import android.app.AlertDialog;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.models.Menu;
import tn.esprit.project.utils.MyDatabase;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<Menu> menus;
    private final MenuAdapterListener listener;
    private final MyDatabase db;

    // ⭐ Ajouter onUpdateMenu
    public interface MenuAdapterListener {
        void onAddItem(Menu menu);
        void onMenuDetails(Menu menu);
        void onUpdateMenu(Menu menu);   // ⭐ nouveau
    }

    public MenuAdapter(List<Menu> menus, MenuAdapterListener listener, MyDatabase db) {
        this.menus = (menus != null) ? menus : new ArrayList<>();
        this.listener = listener;
        this.db = db;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu m = menus.get(position);

        holder.tvTitle.setText(m.getName());
        holder.tvDescription.setText(m.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(m.getImageUri() != null ? Uri.parse(m.getImageUri()) : R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivMenuImage);

        // + Item
        holder.btnAddItem.setOnClickListener(v -> {
            if (listener != null) listener.onAddItem(m);
        });

        // Détails
        holder.btnShowDetails.setOnClickListener(v -> {
            if (listener != null) listener.onMenuDetails(m);
        });

        // ⭐ Update menu
        holder.btnUpdateMenu.setOnClickListener(v -> {
            if (listener != null) listener.onUpdateMenu(m);
        });

        // Delete
        holder.btnDeleteMenu.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer ce menu ?")
                    .setPositiveButton("Oui", (dialog, which) -> {

                        db.menuDAO().delete(m);
                        menus.remove(m);
                        notifyDataSetChanged();

                        Toast.makeText(holder.itemView.getContext(), "Menu supprimé", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public void setMenus(List<Menu> menus) {
        this.menus = (menus != null) ? menus : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMenuImage;
        TextView tvTitle, tvDescription;
        Button btnAddItem, btnShowDetails;
        ImageButton btnDeleteMenu, btnUpdateMenu; // ⭐ ajouter Update

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            ivMenuImage = itemView.findViewById(R.id.ivMenuImage);
            tvTitle = itemView.findViewById(R.id.tvMenuTitle);
            tvDescription = itemView.findViewById(R.id.tvMenuDescription);

            btnAddItem = itemView.findViewById(R.id.btnAddItem);
            btnShowDetails = itemView.findViewById(R.id.btnShowDetails);

            btnDeleteMenu = itemView.findViewById(R.id.btnDeleteMenu);
            btnUpdateMenu = itemView.findViewById(R.id.btnUpdateMenu);  // ⭐ important
        }
    }
}
