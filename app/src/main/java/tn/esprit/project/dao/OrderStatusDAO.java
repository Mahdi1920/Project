package tn.esprit.project.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tn.esprit.project.models.OrderStatusUpdate;

@Dao
public interface OrderStatusDAO {
    @Insert
    long insertStatusUpdate(OrderStatusUpdate update);

    @Query("SELECT * FROM order_status_updates WHERE order_id = :orderId ORDER BY timestamp ASC")
    List<OrderStatusUpdate> getStatusUpdatesForOrder(int orderId);

    @Query("SELECT * FROM order_status_updates WHERE order_id = :orderId ORDER BY timestamp ASC")
    LiveData<List<OrderStatusUpdate>> getStatusUpdatesForOrderLive(int orderId);
}
