package tn.esprit.project.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "favorites", indices = {@Index(value = {"user_id","restaurant_id"}, unique = true)})
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "restaurant_id")
    private int restaurantId;

    public Favorite() {}

    public Favorite(int userId, int restaurantId) {
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public Favorite(int id, int userId, int restaurantId) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
}

