package tn.esprit.project.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import tn.esprit.project.models.User;

@Dao
public interface UserDAO {
    @Insert
    long insert(User user);

    @Update
    int update(User user);

    @Delete
    int delete(User user);

    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getById(int id);
}
