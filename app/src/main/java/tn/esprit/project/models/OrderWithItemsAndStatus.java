package tn.esprit.project.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class OrderWithItemsAndStatus {
    @Embedded
    public Order order;

    @Relation(parentColumn = "id", entityColumn = "order_id")
    public List<OrderItem> items;

    @Relation(parentColumn = "id", entityColumn = "order_id")
    public List<OrderStatusUpdate> statusUpdates;

    public OrderWithItemsAndStatus() {}

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public List<OrderStatusUpdate> getStatusUpdates() { return statusUpdates; }
    public void setStatusUpdates(List<OrderStatusUpdate> statusUpdates) { this.statusUpdates = statusUpdates; }
}

