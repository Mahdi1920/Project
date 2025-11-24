package com.example.project.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private String userType; // livreur, manager, client
    private String restaurantId; // for managers only

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String userId, String name, String email, String phone, String userType) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
    }

    public User(String userId, String name, String email, String phone, String userType, String restaurantId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
        this.restaurantId = restaurantId;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}