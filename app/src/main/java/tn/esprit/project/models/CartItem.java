package tn.esprit.project.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    // No-arg constructor required by Room
    public CartItem() {
    }

    // Constructor for insertion (no id)
    public CartItem(int menuItemId, int quantity, int userId) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.userId = userId;
    }

    // Full constructor
    public CartItem(int id, int menuItemId, int quantity, int userId) {
        this.id = id;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.userId = userId;
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
}
