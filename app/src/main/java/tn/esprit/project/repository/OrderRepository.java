package tn.esprit.project.repository;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.project.utils.AppDatabase;
import tn.esprit.project.models.CartItem;
import tn.esprit.project.models.Menu;
import tn.esprit.project.models.MenuItem;
import tn.esprit.project.models.Order;
import tn.esprit.project.models.OrderItem;
import tn.esprit.project.models.OrderWithItemsAndStatus;
import tn.esprit.project.models.OrderStatusUpdate;

public class OrderRepository {
    private final AppDatabase db;

    public OrderRepository(AppDatabase db) {
        this.db = db;
    }

    /**
     * Create an order from the cart of a given user.
     * This method is executed transactionally: creates Order, OrderItems (snapshot prices) and clears cart.
     * Returns the created order id, or -1 if cart is empty.
     */
    public long createOrderFromCart(final int userId) {
        Long result = db.runInTransaction(() -> {
            List<CartItem> cart = db.cartDAO().getCartForUser(userId);
            if (cart == null || cart.isEmpty()) {
                return -1L;
            }

            // For safety handle possible nulls from DAOs
            CartItem firstCart = cart.get(0);
            int firstMenuItemId = firstCart != null ? firstCart.getMenuItemId() : -1;
            MenuItem firstMenuItem = firstMenuItemId != -1 ? db.menuItemDAO().getById(firstMenuItemId) : null;
            int restaurantId = -1;
            if (firstMenuItem != null) {
                int menuId = firstMenuItem.getMenuId();
                Menu menu = db.menuDAO().getById(menuId);
                if (menu != null) {
                    restaurantId = menu.getRestaurantId();
                }
            }

            double total = 0.0;
            for (CartItem c : cart) {
                if (c == null) continue;
                MenuItem mi = db.menuItemDAO().getById(c.getMenuItemId());
                double price = (mi != null) ? mi.getPrice() : 0.0;
                int qty = c.getQuantity();
                total += price * qty;
            }

            Order order = new Order(userId, restaurantId, "PENDING", total);
            long orderId = db.orderDAO().insertOrder(order);

            // Insert initial status update for the newly created order
            try {
                OrderStatusUpdate initial = new OrderStatusUpdate((int) orderId, order.getStatus(), System.currentTimeMillis(), null, null);
                db.orderStatusDAO().insertStatusUpdate(initial);
            } catch (Exception ignore) {
                // if for any reason inserting the initial status fails, we still proceed with order creation
            }

            for (CartItem c : cart) {
                if (c == null) continue;
                MenuItem mi = db.menuItemDAO().getById(c.getMenuItemId());
                int menuItemId = (mi != null) ? mi.getId() : c.getMenuItemId();
                double price = (mi != null) ? mi.getPrice() : 0.0;
                OrderItem oi = new OrderItem((int) orderId, menuItemId, c.getQuantity(), price);
                db.orderDAO().insertOrderItem(oi);
            }

            db.cartDAO().clearCartForUser(userId);
            return orderId;
        });

        return result == null ? -1L : result;
    }

    /**
     * Retourne la liste des commandes pour un utilisateur (synchrones).
     */
    public List<Order> getOrdersForUser(int userId) {
        return db.orderDAO().getOrdersForUser(userId);
    }

    /**
     * Retourne la liste des OrderItem pour une commande donnée.
     */
    public List<OrderItem> getOrderItems(int orderId) {
        return db.orderDAO().getOrderItems(orderId);
    }

    /**
     * Retourne la liste des commandes avec leurs items et l'historique des statuts.
     * Construit des objets OrderWithItemsAndStatus à partir des DAOs existants.
     */
    public List<OrderWithItemsAndStatus> getOrdersWithItemsAndStatusForUser(int userId) {
        List<OrderWithItemsAndStatus> result = new ArrayList<>();
        List<Order> orders = getOrdersForUser(userId);
        if (orders == null || orders.isEmpty()) return result;

        for (Order o : orders) {
            OrderWithItemsAndStatus ow = new OrderWithItemsAndStatus();
            ow.setOrder(o);
            List<OrderItem> items = getOrderItems(o.getId());
            ow.setItems(items != null ? items : new ArrayList<>());
            List<OrderStatusUpdate> updates = db.orderStatusDAO().getStatusUpdatesForOrder(o.getId());
            ow.setStatusUpdates(updates != null ? updates : new ArrayList<>());
            result.add(ow);
        }
        return result;
    }

    /**
     * Retourne un OrderWithItemsAndStatus pour une commande donnée (ou null si non trouvée).
     */
    public OrderWithItemsAndStatus getOrderWithItemsAndStatus(int orderId) {
        Order o = db.orderDAO().getOrderById(orderId);
        if (o == null) return null;
        OrderWithItemsAndStatus ow = new OrderWithItemsAndStatus();
        ow.setOrder(o);
        List<OrderItem> items = getOrderItems(o.getId());
        ow.setItems(items != null ? items : new ArrayList<>());
        List<OrderStatusUpdate> updates = db.orderStatusDAO().getStatusUpdatesForOrder(o.getId());
        ow.setStatusUpdates(updates != null ? updates : new ArrayList<>());
        return ow;
    }
}
