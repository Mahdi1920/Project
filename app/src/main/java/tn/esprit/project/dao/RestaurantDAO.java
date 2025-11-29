package tn.esprit.project.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import tn.esprit.project.models.Restaurant;
import tn.esprit.project.models.RestaurantWithMenu;

@Dao
public interface RestaurantDAO {
    @Query("SELECT * FROM restaurants")
    LiveData<List<Restaurant>> getAllRestaurants();

    // synchronous version used by repository/tests
    @Query("SELECT * FROM restaurants")
    List<Restaurant> getAllRestaurantsSync();

    @Insert
    void insertRestaurant(Restaurant restaurant);

    @Delete
    void deleteRestaurant(Restaurant restaurant);

    @Transaction
    @Query("SELECT * FROM restaurants WHERE id = :restaurantId LIMIT 1")
    LiveData<RestaurantWithMenu> getRestaurantWithMenu(int restaurantId);
}
