package com.example.project.models;
import com.google.firebase.Timestamp;
import java.util.List;

public class Commande {
    private String commandeId;
    private String clientId;
    private String clientName;
    private String clientPhone;
    private String clientAddress;
    private double clientLatitude;
    private double clientLongitude;

    private String restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private double restaurantLatitude;
    private double restaurantLongitude;

    private String livreurId;
    private String livreurName;
    private String livreurPhone;

    private String status; // pending, accepted, picked_up, delivered, cancelled
    private List<OrderItem> items;
    private double totalPrice;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Commande() {
        // Required empty constructor for Firebase
    }

    // Getters and Setters
    public String getCommandeId() {
        return commandeId;
    }

    public void setCommandeId(String commandeId) {
        this.commandeId = commandeId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public double getClientLatitude() {
        return clientLatitude;
    }

    public void setClientLatitude(double clientLatitude) {
        this.clientLatitude = clientLatitude;
    }

    public double getClientLongitude() {
        return clientLongitude;
    }

    public void setClientLongitude(double clientLongitude) {
        this.clientLongitude = clientLongitude;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public double getRestaurantLatitude() {
        return restaurantLatitude;
    }

    public void setRestaurantLatitude(double restaurantLatitude) {
        this.restaurantLatitude = restaurantLatitude;
    }

    public double getRestaurantLongitude() {
        return restaurantLongitude;
    }

    public void setRestaurantLongitude(double restaurantLongitude) {
        this.restaurantLongitude = restaurantLongitude;
    }

    public String getLivreurId() {
        return livreurId;
    }

    public void setLivreurId(String livreurId) {
        this.livreurId = livreurId;
    }

    public String getLivreurName() {
        return livreurName;
    }

    public void setLivreurName(String livreurName) {
        this.livreurName = livreurName;
    }

    public String getLivreurPhone() {
        return livreurPhone;
    }

    public void setLivreurPhone(String livreurPhone) {
        this.livreurPhone = livreurPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}