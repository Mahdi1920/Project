package tn.esprit.project.utils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import tn.esprit.project.dao.ItemDAO;
import tn.esprit.project.dao.MenuDAO;
import tn.esprit.project.dao.RestaurantDAO;
import tn.esprit.project.models.Item;
import tn.esprit.project.models.Menu;
import tn.esprit.project.models.Restaurant;

@Database(entities = {Restaurant.class, Menu.class, Item.class}, version = 3)
public abstract class MyDatabase extends RoomDatabase {

    public abstract RestaurantDAO restaurantDAO();
    public abstract MenuDAO menuDAO();
    public abstract ItemDAO itemDAO();

    private static volatile MyDatabase instance;

    public static MyDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (MyDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    MyDatabase.class,
                                    "delivery_food_db"
                            )
                            .fallbackToDestructiveMigration() // supprime et recrée la DB si version change
                            .allowMainThreadQueries()       // uniquement pour dev
                            .build();
                }
            }
        }
        return instance;
    }
}
