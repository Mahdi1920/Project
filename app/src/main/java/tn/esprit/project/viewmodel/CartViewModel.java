package tn.esprit.project.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import tn.esprit.project.models.CartItem;
import tn.esprit.project.models.CartItemWithMenuItem;
import tn.esprit.project.repository.IClientRepository;

public class CartViewModel extends ViewModel {
    private final IClientRepository repository;

    public CartViewModel(IClientRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<CartItemWithMenuItem>> observeCart(int userId) {
        return repository.observeCartForUser(userId);
    }

    public void addToCart(int menuItemId, int quantity, int userId) {
        repository.addToCart(menuItemId, quantity, userId);
    }

    public void removeCartItem(CartItem item) {
        repository.removeCartItem(item);
    }

    public void clearCart(int userId) {
        repository.clearCart(userId);
    }

    public void updateQuantity(int cartItemId, int quantity) {
        repository.updateCartQuantity(cartItemId, quantity);
    }

    public long placeOrder(int userId) {
        return repository.placeOrderFromCart(userId);
    }
}
