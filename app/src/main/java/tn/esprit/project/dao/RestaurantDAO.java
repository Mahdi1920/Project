package tn.esprit.project.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


import tn.esprit.project.models.Category;
import tn.esprit.project.models.Restaurant;

@Dao
public interface RestaurantDAO {

    @Insert
    long insert(Restaurant restaurant);

    @Update
    void update(Restaurant restaurant);

    @Delete
    void delete(Restaurant restaurant);

    @Query("SELECT * FROM restaurants ORDER BY id DESC")
    List<Restaurant> getAllRestaurants();
    @Query("SELECT * FROM restaurants WHERE category = :category ORDER BY id DESC")
    List<Restaurant> getByCategory(Category category);

    @Query("SELECT * FROM restaurants WHERE id = :id")
    Restaurant getRestaurantById(int id);
}
