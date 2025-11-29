package tn.esprit.project.fragments.adapters;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tn.esprit.project.models.NominatimResponse;

public class NominatimAdapter extends RecyclerView.Adapter<NominatimAdapter.ViewHolder> {

    private List<NominatimResponse> data;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NominatimResponse item);
    }

    public NominatimAdapter(List<NominatimResponse> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    public void updateData(List<NominatimResponse> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NominatimResponse item = data.get(position);
        holder.textView.setText(item.display_name);
        holder.textView.setTextColor(Color.BLACK); // texte en noir
        // Utilise display_name directement, mais assure-toi qu'il n'y a pas de saut de ligne ou de concat
        String address = item.display_name != null ? item.display_name.trim() : "";
        holder.textView.setText(address);
        holder.textView.setTextColor(Color.BLACK);
        holder.textView.setMaxLines(2);
        holder.textView.setEllipsize(TextUtils.TruncateAt.END);

        holder.textView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
