package tn.esprit.project.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "menus")
public class Menu {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "restaurant_id")
    private int restaurantId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    // No-arg constructor
    public Menu() {
    }

    // Constructor for insertion
    public Menu(int restaurantId, String name, String description, String imageUrl) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Full constructor
    public Menu(int id, int restaurantId, String name, String description, String imageUrl) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

