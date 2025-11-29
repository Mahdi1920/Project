package tn.esprit.project.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import tn.esprit.project.models.CartItem;
import tn.esprit.project.models.CartItemWithMenuItem;
import tn.esprit.project.models.Menu;
import tn.esprit.project.models.MenuItem;
import tn.esprit.project.models.Order;
import tn.esprit.project.models.OrderItem;
import tn.esprit.project.models.Restaurant;
import tn.esprit.project.models.User;
import tn.esprit.project.models.OrderStatusUpdate;
import tn.esprit.project.models.OrderWithItemsAndStatus;
import tn.esprit.project.utils.AppDatabase;

public class ClientRepository implements IClientRepository {
    private final AppDatabase db;

    public ClientRepository(Context context) {
        db = AppDatabase.getInstance(context);
    }

    // Constructor for test injection
    public ClientRepository(AppDatabase database) {
        this.db = database;
    }

    @Override
    public LiveData<List<Restaurant>> getAllRestaurants() {
        return db.restaurantDAO().getAllRestaurants();
    }

    @Override
    public List<Restaurant> getAllRestaurantsSync() {
        return db.restaurantDAO().getAllRestaurantsSync();
    }

    @Override
    public LiveData<List<MenuItem>> getMenuForRestaurant(int restaurantId) {
        return db.menuItemDAO().getMenuForRestaurant(restaurantId);
    }

    @Override
    public List<MenuItem> getMenuForRestaurantSync(int restaurantId) {
        return db.menuItemDAO().getMenuForRestaurantSync(restaurantId);
    }

    @Override
    public LiveData<List<Menu>> getMenusForRestaurant(int restaurantId) {
        return db.menuDAO().getMenusForRestaurant(restaurantId);
    }

    @Override
    public List<Menu> getMenusForRestaurantSync(int restaurantId) {
        return db.menuDAO().getMenusForRestaurantSync(restaurantId);
    }

    @Override
    public LiveData<List<MenuItem>> getItemsForMenu(int menuId) {
        return db.menuItemDAO().getItemsForMenu(menuId);
    }

    @Override
    public List<MenuItem> getItemsForMenuSync(int menuId) {
        return db.menuItemDAO().getItemsForMenuSync(menuId);
    }

    @Override
    public LiveData<MenuItem> getMenuItemById(int id) {
        return db.menuItemDAO().getByIdLive(id);
    }

    @Override
    public void addToCart(int menuItemId, int quantity, int userId) {
        CartItem item = new CartItem(menuItemId, quantity, userId);
        db.cartDAO().insertCartItem(item);
    }

    @Override
    public LiveData<List<CartItemWithMenuItem>> observeCartForUser(int userId) {
        return db.cartDAO().observeCartForUser(userId);
    }

    @Override
    public List<CartItem> getCartSync(int userId) {
        return db.cartDAO().getCartForUser(userId);
    }

    @Override
    public void removeCartItem(CartItem item) {
        db.cartDAO().deleteCartItem(item);
    }

    @Override
    public void clearCart(int userId) {
        db.cartDAO().clearCartForUser(userId);
    }

    @Override
    public void updateCartQuantity(int cartItemId, int quantity) {
        db.cartDAO().updateQuantity(cartItemId, quantity);
    }

    @Override
    public long placeOrderFromCart(int userId) {
        OrderRepository repo = new OrderRepository(db);
        return repo.createOrderFromCart(userId);
    }

    @Override
    public List<Order> getOrders(int userId) {
        return db.orderDAO().getOrdersForUser(userId);
    }

    @Override
    public List<OrderItem> getOrderItems(int orderId) {
        return db.orderDAO().getOrderItems(orderId);
    }

    @Override
    public LiveData<List<OrderWithItemsAndStatus>> getOrdersWithItemsAndStatusLive(int userId) {
        return db.orderDAO().getOrdersWithItemsAndStatusLive(userId);
    }

    @Override
    public LiveData<OrderWithItemsAndStatus> getOrderWithItemsAndStatusLive(int orderId) {
        return db.orderDAO().getOrderWithItemsAndStatusLive(orderId);
    }

    @Override
    public LiveData<List<OrderStatusUpdate>> getStatusUpdatesForOrderLive(int orderId) {
        return db.orderStatusDAO().getStatusUpdatesForOrderLive(orderId);
    }

    @Override
    public User getUser(int id) {
        return db.userDAO().getById(id);
    }

    @Override
    public void updateUser(User user) {
        db.userDAO().update(user);
    }

    @Override
    public void updateOrderStatus(int orderId, String status) {
        db.orderDAO().updateOrderStatus(orderId, status);
        try {
            OrderStatusUpdate update = new OrderStatusUpdate(orderId, status, System.currentTimeMillis(), 14.6937, -17.44406);
            db.orderStatusDAO().insertStatusUpdate(update);
        } catch (Exception ignored) {
            // ignore failures to keep updateOrderStatus best-effort
        }
    }

    @Override
    public long insertOrderStatusUpdate(OrderStatusUpdate update) {
        return db.orderStatusDAO().insertStatusUpdate(update);
    }

    @Override
    public List<OrderStatusUpdate> getStatusUpdatesForOrder(int orderId) {
        return db.orderStatusDAO().getStatusUpdatesForOrder(orderId);
    }
}
