// java
package tn.esprit.project.fragments.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.models.Restaurant;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private final Context context;
    private final List<Restaurant> list;

    private static final String TAG = "RestaurantAdapter";

    public RestaurantAdapter(Context context, List<Restaurant> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant r = list.get(position);
        Log.d(TAG, "Binding restaurant: " + r.getName() + " with URI: " + r.getLogoUri());

        holder.tvName.setText(r.getName());
        holder.tvAddress.setText(r.getAddress());
        // Conversion de l'enum/objet category en String, protection contre null
        holder.tvCategory.setText(r.getCategory() != null ? r.getCategory().name() : "");

        String logo = r.getLogoUri();
        if (logo != null && !logo.isEmpty()) {
            try {
                // Manage different possible representations: file://, content://, absolute path
                if (logo.startsWith("/")) {
                    // Absolute path on device
                    File f = new File(logo);
                    if (f.exists()) {
                        Glide.with(context)
                                .load(f)
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .centerCrop()
                                .into(holder.ivLogo);
                        Log.d(TAG, "Glide loaded from absolute path: " + logo);
                        return;
                    } else {
                        Log.w(TAG, "File path does not exist: " + logo);
                    }
                }

                Uri uri = Uri.parse(logo);

                if (uri.getScheme() == null) {
                    // Treat as file path without scheme
                    File f = new File(logo);
                    if (f.exists()) {
                        Glide.with(context)
                                .load(f)
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .centerCrop()
                                .into(holder.ivLogo);
                        Log.d(TAG, "Glide loaded from file path (no scheme): " + logo);
                        return;
                    }
                }

                // Default: try loading the Uri (content:// or file://)
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .centerCrop()
                        .into(holder.ivLogo);
                Log.d(TAG, "Glide loaded URI: " + logo);

            } catch (Exception e) {
                Log.e(TAG, "Error loading image, attempting content fallback. " + e.getMessage(), e);
                // try to fallback for content URIs by copying to cache and loading from file
                try {
                    Uri uri = Uri.parse(logo);
                    if ("content".equals(uri.getScheme())) {
                        File cached = copyContentUriToCache(uri);
                        if (cached != null && cached.exists()) {
                            Glide.with(context)
                                    .load(cached)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.mipmap.ic_launcher)
                                    .centerCrop()
                                    .into(holder.ivLogo);
                            Log.d(TAG, "Glide loaded image from cached file after content fallback: " + cached.getAbsolutePath());
                            return;
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "Content fallback failed: " + ex.getMessage(), ex);
                }

                // final fallback
                try {
                    holder.ivLogo.setImageURI(Uri.parse(logo));
                } catch (Exception ex) {
                    holder.ivLogo.setImageResource(R.mipmap.ic_launcher);
                }
            }
        } else {
            Log.d(TAG, "No logo URI, using default");
            holder.ivLogo.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Restaurant> getList() {
        return list;
    }

    private File copyContentUriToCache(Uri uri) {
        InputStream in = null;
        File outFile = null;
        try {
            in = context.getContentResolver().openInputStream(uri);
            if (in == null) return null;
            outFile = new File(context.getCacheDir(), "img_" + System.currentTimeMillis() + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) fos.write(buf, 0, len);
            }
            return outFile;
        } catch (Exception e) {
            Log.e(TAG, "Failed to copy content URI to cache: " + e.getMessage(), e);
            if (outFile != null && outFile.exists()) outFile.delete();
            return null;
        } finally {
            try {
                if (in != null) in.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvName, tvAddress, tvCategory;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.ivItemLogo);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}