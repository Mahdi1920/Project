package tn.esprit.project.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "restaurant_id")
    private int restaurantId;

    @ColumnInfo(name = "status")
    private String status; // e.g., PENDING, PREPARING, OUT_FOR_DELIVERY, DELIVERED

    @ColumnInfo(name = "total_price")
    private double totalPrice;

    // Constructeur sans-argument requis par Room
    public Order() {
    }

    // Constructeur pour insertion
    public Order(int userId, int restaurantId, String status, double totalPrice) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    // Constructeur complet
    public Order(int id, int userId, int restaurantId, String status, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
