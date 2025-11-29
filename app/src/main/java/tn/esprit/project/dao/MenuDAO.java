package tn.esprit.project.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.project.models.Item;
import tn.esprit.project.models.Menu;

@Dao
public interface MenuDAO {

    @Insert
    long insert(Menu menu);

    @Update
    void update(Menu menu);

    @Delete
    void delete(Menu menu);




    @Query("SELECT * FROM menus WHERE id = :menuId")
    Menu getMenuById(int menuId);

    @Query("SELECT * FROM menus WHERE restaurantId = :restaurantId")
    List<Menu> getMenusByRestaurant(int restaurantId);


}
