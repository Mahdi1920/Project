package tn.esprit.project.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "menu_items")
public class MenuItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "menu_id")
    private int menuId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    // No-arg constructor required by Room
    public MenuItem() {
    }

    // Constructor for insertion
    public MenuItem(int menuId, String name, String description, double price, String imageUrl) {
        this.menuId = menuId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Full constructor
    public MenuItem(int id, int menuId, String name, String description, double price, String imageUrl) {
        this.id = id;
        this.menuId = menuId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
