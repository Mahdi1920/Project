package tn.esprit.project.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import tn.esprit.project.models.Menu;
import tn.esprit.project.models.MenuWithItems;

@Dao
public interface MenuDAO {
    @Query("SELECT * FROM menus WHERE restaurant_id = :restaurantId")
    LiveData<List<Menu>> getMenusForRestaurant(int restaurantId);

    @Query("SELECT * FROM menus WHERE restaurant_id = :restaurantId")
    List<Menu> getMenusForRestaurantSync(int restaurantId);

    @Insert
    void insertMenu(Menu menu);

    @Transaction
    @Query("SELECT * FROM menus WHERE id = :menuId LIMIT 1")
    LiveData<MenuWithItems> getMenuWithItems(int menuId);

    @Transaction
    @Query("SELECT * FROM menus WHERE id = :menuId LIMIT 1")
    MenuWithItems getMenuWithItemsSync(int menuId);

    @Query("SELECT * FROM menus WHERE id = :menuId LIMIT 1")
    Menu getById(int menuId);
}
