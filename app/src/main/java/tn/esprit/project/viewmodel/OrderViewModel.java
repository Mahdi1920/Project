package tn.esprit.project.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import tn.esprit.project.models.Order;
import tn.esprit.project.models.OrderItem;
import tn.esprit.project.models.OrderWithItemsAndStatus;
import tn.esprit.project.repository.IClientRepository;

public class OrderViewModel extends ViewModel {
    private final IClientRepository repository;

    public OrderViewModel(IClientRepository repository) {
        this.repository = repository;
    }

    public long placeOrderFromCart(int userId) {
        return repository.placeOrderFromCart(userId);
    }

    public List<Order> getOrders(int userId) {
        return repository.getOrders(userId);
    }

    public List<OrderItem> getOrderItems(int orderId) {
        return repository.getOrderItems(orderId);
    }
}

