package tn.esprit.project.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tn.esprit.project.models.MenuItem;

@Dao
public interface MenuItemDAO {
    @Query("SELECT * FROM menu_items WHERE menu_id = :menuId")
    LiveData<List<MenuItem>> getItemsForMenu(int menuId);

    @Query("SELECT * FROM menu_items WHERE menu_id = :menuId")
    List<MenuItem> getItemsForMenuSync(int menuId);

    // Get items for a restaurant by joining menus -> menu_items
    @Query("SELECT mi.* FROM menu_items mi JOIN menus m ON mi.menu_id = m.id WHERE m.restaurant_id = :restaurantId")
    LiveData<List<MenuItem>> getMenuForRestaurant(int restaurantId);

    @Query("SELECT mi.* FROM menu_items mi JOIN menus m ON mi.menu_id = m.id WHERE m.restaurant_id = :restaurantId")
    List<MenuItem> getMenuForRestaurantSync(int restaurantId);

    @Query("SELECT * FROM menu_items WHERE id = :id LIMIT 1")
    MenuItem getById(int id);

    @Query("SELECT * FROM menu_items WHERE id = :id LIMIT 1")
    LiveData<MenuItem> getByIdLive(int id);

    @Insert
    void insertMenuItem(MenuItem item);

    @Delete
    void deleteMenuItem(MenuItem item);
}
