package tn.esprit.project.models;

import androidx.room.Embedded;
import androidx.room.Relation;

public class CartItemWithMenuItem {
    @Embedded
    private CartItem cartItem;

    @Relation(parentColumn = "menu_item_id", entityColumn = "id")
    private MenuItem menuItem;

    public CartItemWithMenuItem() {}

    public CartItemWithMenuItem(CartItem cartItem, MenuItem menuItem) {
        this.cartItem = cartItem;
        this.menuItem = menuItem;
    }

    public CartItem getCartItem() { return cartItem; }
    public void setCartItem(CartItem cartItem) { this.cartItem = cartItem; }

    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
}
