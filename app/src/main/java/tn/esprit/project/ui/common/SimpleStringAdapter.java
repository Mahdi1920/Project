package tn.esprit.project.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter RecyclerView minimal pour afficher une liste de String.
 * Utilise android.R.layout.simple_list_item_1 pour éviter de créer un layout XML supplémentaire.
 */
public class SimpleStringAdapter extends RecyclerView.Adapter<SimpleStringAdapter.ViewHolder> {

    private List<String> items;
    private OnItemClickListener listener;

    public SimpleStringAdapter(List<String> items) {
        this.items = items;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String value = items.get(position);
        holder.text.setText(value);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position, value);
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
        }
    }
}

