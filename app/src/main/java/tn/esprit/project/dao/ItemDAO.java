package tn.esprit.project.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.project.models.Item;

@Dao
public interface ItemDAO {

    @Insert
    long insert(Item item);

    @Update
    void update(Item item);

    @Delete
    void delete(Item item);

    @Query("SELECT * FROM items WHERE id = :itemId")
    Item getItemById(int itemId);

    @Query("SELECT * FROM items WHERE menuId = :menuId")
    List<Item> getItemsByMenu(int menuId);


}
