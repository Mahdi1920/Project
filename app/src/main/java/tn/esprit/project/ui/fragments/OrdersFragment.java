package tn.esprit.project.ui.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import tn.esprit.project.R;
import tn.esprit.project.models.OrderStatusUpdate;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.ui.adapters.OrdersListAdapter;
import tn.esprit.project.viewmodel.OrdersViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;
import tn.esprit.project.utils.NavigationUtils;

public class OrdersFragment extends Fragment {

    private OrdersListAdapter adapter;
    private static final int USER_ID = 1; // demo user
    private static final String TAG = "OrdersFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.rv_orders);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrdersListAdapter();
        rv.setAdapter(adapter);

        ClientRepository repo = new ClientRepository(requireContext());
        ViewModelFactory factory = new ViewModelFactory(repo);
        OrdersViewModel viewModel = new ViewModelProvider(this, factory).get(OrdersViewModel.class);

        viewModel.getOrderDisplaysLive(USER_ID).observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                List<OrdersListAdapter.OrderDisplay> displays = new ArrayList<>();
                for (OrdersViewModel.OrderDisplay od : list) {
                    displays.add(new OrdersListAdapter.OrderDisplay(od.order, od.itemCount));
                }
                adapter.setItems(displays);
            } else {
                adapter.setItems(new ArrayList<>());
            }
        });

        adapter.setOnOrderActionListener(orderId -> {
            String[] statuses = new String[]{"PENDING", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"};
            new AlertDialog.Builder(requireContext())
                    .setTitle("Changer le statut")
                    .setItems(statuses, (dialog, which) -> {
                        String chosen = statuses[which];
                        long timestamp = System.currentTimeMillis();
                        // use repository to update status and insert status update
                        new Thread(() -> {
                            repo.updateOrderStatus(orderId, chosen);
                            repo.insertOrderStatusUpdate(new tn.esprit.project.models.OrderStatusUpdate(orderId, chosen, timestamp, null, null));
                        }).start();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        adapter.setOnOrderClickListener(orderId -> {
            Bundle args = new Bundle();
            args.putInt("orderId", orderId);
            NavigationUtils.navigateTo(OrdersFragment.this, getView(), R.id.orderDetailsFragment, args);
        });

        adapter.setOnOrderTimelineListener(orderId -> {
            // navigate to details (timeline)
            Bundle args = new Bundle();
            args.putInt("orderId", orderId);
            NavigationUtils.navigateTo(OrdersFragment.this, getView(), R.id.orderDetailsFragment, args);
        });

        adapter.setOnOrderMapListener(orderId -> {
            // find latest status update with coords and open map
            new Thread(() -> {
                List<OrderStatusUpdate> updates = repo.getStatusUpdatesForOrder(orderId);
                OrderStatusUpdate latest = null;
                if (updates != null) {
                    for (int i = updates.size() - 1; i >= 0; i--) {
                        OrderStatusUpdate u = updates.get(i);
                        if (u != null && u.getLatitude() != null && u.getLongitude() != null) {
                            latest = u;
                            break;
                        }
                    }
                }
                if (latest != null) {
                    double lat = latest.getLatitude();
                    double lon = latest.getLongitude();
                    String uri = String.format(java.util.Locale.getDefault(), "geo:%f,%f?q=%f,%f(Order+Location)", lat, lon, lat, lon);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    try {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(fallback);
                            }
                        });
                    } catch (Exception e) {
                        // If we can't post to UI thread, fallback to navigating to details
                        requireActivity().runOnUiThread(() -> {
                            Bundle args = new Bundle();
                            args.putInt("orderId", orderId);
                            NavigationUtils.navigateTo(OrdersFragment.this, getView(), R.id.orderDetailsFragment, args);
                        });
                    }
                } else {
                    // no coords available; show details instead
                    requireActivity().runOnUiThread(() -> {
                        Bundle args = new Bundle();
                        args.putInt("orderId", orderId);
                        NavigationUtils.navigateTo(OrdersFragment.this, getView(), R.id.orderDetailsFragment, args);
                    });
                }
            }).start();
        });
    }

}
