package tn.esprit.project.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.project.models.OrderStatusUpdate;
import tn.esprit.project.models.OrderWithItemsAndStatus;
import tn.esprit.project.repository.IClientRepository;

public class OrderDetailsViewModel extends ViewModel {
    private final IClientRepository repository;

    public OrderDetailsViewModel(IClientRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<OrderStatusUpdate>> getStatusUpdatesForOrder(int orderId) {
        return repository.getStatusUpdatesForOrderLive(orderId);
    }

    public LiveData<OrderWithItemsAndStatus> getOrderWithItemsAndStatusLive(int orderId) {
        return repository.getOrderWithItemsAndStatusLive(orderId);
    }

    /**
     * Combined LiveData: prefer real status updates if present, otherwise produce a synthetic list
     * from the Order.status field (so UI never shows empty timeline).
     */
    public LiveData<List<OrderStatusUpdate>> getCombinedStatusLive(int orderId) {
        MediatorLiveData<List<OrderStatusUpdate>> mediator = new MediatorLiveData<>();

        LiveData<List<OrderStatusUpdate>> updatesLive = repository.getStatusUpdatesForOrderLive(orderId);
        LiveData<OrderWithItemsAndStatus> orderLive = repository.getOrderWithItemsAndStatusLive(orderId);

        mediator.addSource(updatesLive, updates -> {
            if (updates != null && !updates.isEmpty()) {
                mediator.setValue(updates);
            } else {
                // if updates empty, fallback to reading orderLive value (if available)
                OrderWithItemsAndStatus ow = orderLive.getValue();
                List<OrderStatusUpdate> fallback = new ArrayList<>();
                if (ow != null && ow.getOrder() != null) {
                    String status = ow.getOrder().getStatus();
                    if (status == null || status.trim().isEmpty()) status = "UNKNOWN";
                    fallback.add(new OrderStatusUpdate(ow.getOrder().getId(), status, System.currentTimeMillis(), null, null));
                }
                mediator.setValue(fallback);
            }
        });

        mediator.addSource(orderLive, ow -> {
            List<OrderStatusUpdate> current = updatesLive.getValue();
            if (current != null && !current.isEmpty()) {
                mediator.setValue(current);
            } else {
                List<OrderStatusUpdate> fallback = new ArrayList<>();
                if (ow != null && ow.getOrder() != null) {
                    String status = ow.getOrder().getStatus();
                    if (status == null || status.trim().isEmpty()) status = "UNKNOWN";
                    fallback.add(new OrderStatusUpdate(ow.getOrder().getId(), status, System.currentTimeMillis(), null, null));
                }
                mediator.setValue(fallback);
            }
        });

        return mediator;
    }
}
