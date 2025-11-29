package tn.esprit.project.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import tn.esprit.project.models.CartItem;
import tn.esprit.project.models.CartItemWithMenuItem;

@Dao
public interface CartDAO {
    @Query("SELECT * FROM cart_items WHERE user_id = :userId")
    List<CartItem> getCartForUser(int userId);

    @Insert
    void insertCartItem(CartItem item);

    @Delete
    void deleteCartItem(CartItem item);

    @Query("DELETE FROM cart_items WHERE user_id = :userId")
    void clearCartForUser(int userId);

    @Transaction
    @Query("SELECT * FROM cart_items WHERE user_id = :userId")
    LiveData<List<CartItemWithMenuItem>> observeCartForUser(int userId);

    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :cartItemId")
    void updateQuantity(int cartItemId, int quantity);
}
