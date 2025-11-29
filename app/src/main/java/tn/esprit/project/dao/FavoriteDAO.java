package tn.esprit.project.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tn.esprit.project.models.Favorite;

@Dao
public interface FavoriteDAO {
    @Query("SELECT * FROM favorites WHERE user_id = :userId")
    LiveData<List<Favorite>> getFavoritesForUser(int userId);

    @Insert
    long insertFavorite(Favorite favorite);

    @Delete
    void deleteFavorite(Favorite favorite);
}

