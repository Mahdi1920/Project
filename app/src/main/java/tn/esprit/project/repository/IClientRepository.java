package tn.esprit.project.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import tn.esprit.project.models.CartItem;
import tn.esprit.project.models.CartItemWithMenuItem;
import tn.esprit.project.models.Menu;
import tn.esprit.project.models.MenuItem;
import tn.esprit.project.models.Order;
import tn.esprit.project.models.OrderItem;
import tn.esprit.project.models.OrderWithItemsAndStatus;
import tn.esprit.project.models.OrderStatusUpdate;
import tn.esprit.project.models.Restaurant;
import tn.esprit.project.models.User;

public interface IClientRepository {
    LiveData<List<Restaurant>> getAllRestaurants();
    List<Restaurant> getAllRestaurantsSync();

    LiveData<List<MenuItem>> getMenuForRestaurant(int restaurantId);
    List<MenuItem> getMenuForRestaurantSync(int restaurantId);

    // New: menus and items by menu
    LiveData<List<Menu>> getMenusForRestaurant(int restaurantId);
    List<Menu> getMenusForRestaurantSync(int restaurantId);

    LiveData<List<MenuItem>> getItemsForMenu(int menuId);
    List<MenuItem> getItemsForMenuSync(int menuId);

    LiveData<MenuItem> getMenuItemById(int id);

    void addToCart(int menuItemId, int quantity, int userId);
    LiveData<List<CartItemWithMenuItem>> observeCartForUser(int userId);
    List<CartItem> getCartSync(int userId);
    void removeCartItem(CartItem item);
    void clearCart(int userId);

    // new: update quantity
    void updateCartQuantity(int cartItemId, int quantity);

    long placeOrderFromCart(int userId);
    List<Order> getOrders(int userId);
    List<OrderItem> getOrderItems(int orderId);

    // LiveData versions
    LiveData<List<OrderWithItemsAndStatus>> getOrdersWithItemsAndStatusLive(int userId);
    LiveData<OrderWithItemsAndStatus> getOrderWithItemsAndStatusLive(int orderId);

    LiveData<List<OrderStatusUpdate>> getStatusUpdatesForOrderLive(int orderId);

    User getUser(int id);
    void updateUser(User user);

    // Update order status for an order (convenience passthrough to OrderDAO)
    void updateOrderStatus(int orderId, String status);

    // New: order status updates history (sync)
    long insertOrderStatusUpdate(OrderStatusUpdate update);
    List<OrderStatusUpdate> getStatusUpdatesForOrder(int orderId);
}
