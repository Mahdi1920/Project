package tn.esprit.project.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import tn.esprit.project.models.Order;
import tn.esprit.project.models.OrderItem;
import tn.esprit.project.models.OrderWithItemsAndStatus;

@Dao
public interface OrderDAO {
    @Query("SELECT * FROM orders WHERE user_id = :userId")
    List<Order> getOrdersForUser(int userId);

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    Order getOrderById(int orderId);

    @Insert
    long insertOrder(Order order);

    @Delete
    void deleteOrder(Order order);

    @Insert
    void insertOrderItem(OrderItem item);

    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    List<OrderItem> getOrderItems(int orderId);

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    void updateOrderStatus(int orderId, String status);

    // LiveData versions using Room relations to return orders with their items and status history
    @Transaction
    @Query("SELECT * FROM orders WHERE user_id = :userId")
    LiveData<List<OrderWithItemsAndStatus>> getOrdersWithItemsAndStatusLive(int userId);

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    LiveData<OrderWithItemsAndStatus> getOrderWithItemsAndStatusLive(int orderId);
}
