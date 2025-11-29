package tn.esprit.project.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "cart_items")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "menu_item_id")
    private int menuItemId;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "user_id")
    private int userId; // owner of the cart

    @ColumnInfo(name = "customizations")
    private List<String> customizations; // sandwich customizations (e.g., onion, harissa ...)

    // No-arg constructor required by Room
    public CartItem() {
    }

    // Constructor for insertion (no id)
    public CartItem(int menuItemId, int quantity, int userId) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.userId = userId;
    }

    // Constructor for insertion with customizations
    public CartItem(int menuItemId, int quantity, int userId, List<String> customizations) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.userId = userId;
        this.customizations = customizations;
    }

    // Full constructor
    public CartItem(int id, int menuItemId, int quantity, int userId) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.userId = userId;
    }

    // Full constructor with customizations
    public CartItem(int id, int menuItemId, int quantity, int userId, List<String> customizations) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.userId = userId;
        this.customizations = customizations;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<String> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<String> customizations) {
        this.customizations = customizations;
    }
}
