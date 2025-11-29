package tn.esprit.project.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import tn.esprit.project.R;
import tn.esprit.project.models.OrderStatusUpdate;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.ui.adapters.OrderStatusAdapter;
import tn.esprit.project.viewmodel.OrderDetailsViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;

public class OrderDetailsFragment extends Fragment {

    private OrderStatusAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.rv_status_updates);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrderStatusAdapter();
        rv.setAdapter(adapter);

        TextView titleTv = view.findViewById(R.id.tv_order_details_title);
        TextView coordsTv = view.findViewById(R.id.tv_order_coords);
        Button btnMap = view.findViewById(R.id.btn_show_on_map);

        int orderId = getArguments() != null ? getArguments().getInt("orderId", -1) : -1;

        ClientRepository repo = new ClientRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repo);
        OrderDetailsViewModel viewModel = new ViewModelProvider(this, factory).get(OrderDetailsViewModel.class);

        viewModel.getCombinedStatusLive(orderId).observe(getViewLifecycleOwner(), updates -> {
            if (updates == null) updates = new ArrayList<>();
            adapter.setItems(updates);

            titleTv.setText(String.format(Locale.getDefault(), "Order #%d status history", orderId));

            // find latest with coords
            OrderStatusUpdate latestWithCoords = null;
            for (int i = updates.size() - 1; i >= 0; i--) {
                OrderStatusUpdate u = updates.get(i);
                if (u != null && u.getLatitude() != null && u.getLongitude() != null) {
                    latestWithCoords = u;
                    break;
                }
            }

            if (latestWithCoords != null) {
                String coordsText = String.format(Locale.getDefault(), "Lat: %.6f, Lon: %.6f", latestWithCoords.getLatitude(), latestWithCoords.getLongitude());
                coordsTv.setText(coordsText);
                coordsTv.setVisibility(View.VISIBLE);
                btnMap.setVisibility(View.VISIBLE);

                final double lat = latestWithCoords.getLatitude();
                final double lon = latestWithCoords.getLongitude();
                btnMap.setOnClickListener(v -> {
                    String uri = String.format(Locale.getDefault(), "geo:%f,%f?q=%f,%f(Order+Location)", lat, lon, lat, lon);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(fallback);
                    }
                });
            } else {
                coordsTv.setVisibility(View.GONE);
                btnMap.setVisibility(View.GONE);
            }
        });
    }
}
