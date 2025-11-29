package tn.esprit.project.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.project.models.Order;
import tn.esprit.project.models.OrderWithItemsAndStatus;
import tn.esprit.project.repository.IClientRepository;

public class OrdersViewModel extends ViewModel {
    private final IClientRepository repository;

    public OrdersViewModel(IClientRepository repository) {
        this.repository = repository;
    }

    public static class OrderDisplay {
        public final Order order;
        public final int itemCount;
        public OrderDisplay(Order order, int itemCount) { this.order = order; this.itemCount = itemCount; }
    }

    public LiveData<List<OrderDisplay>> getOrderDisplaysLive(int userId) {
        LiveData<List<OrderWithItemsAndStatus>> src = repository.getOrdersWithItemsAndStatusLive(userId);
        return Transformations.map(src, list -> {
            List<OrderDisplay> out = new ArrayList<>();
            if (list != null) {
                for (OrderWithItemsAndStatus ow : list) {
                    int count = 0;
                    if (ow.getItems() != null) count = ow.getItems().size();
                    out.add(new OrderDisplay(ow.getOrder(), count));
                }
            }
            return out;
        });
    }
}
