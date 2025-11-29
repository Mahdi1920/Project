package tn.esprit.project.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int menuId; // FK vers Menu
    private String name;
    private String description;
    private double price;
    private String imageUri; // Optionnelle

    public Item(int menuId, String name, String description, double price, String imageUri) {
        this.menuId = menuId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUri = imageUri;
    }

    // GETTERS & SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}
