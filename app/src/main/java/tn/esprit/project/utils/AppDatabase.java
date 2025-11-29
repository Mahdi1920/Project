package tn.esprit.project.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tn.esprit.project.dao.CartDAO;
import tn.esprit.project.dao.MenuItemDAO;
import tn.esprit.project.dao.MenuDAO;
import tn.esprit.project.dao.OrderDAO;
import tn.esprit.project.dao.RestaurantDAO;
import tn.esprit.project.dao.UserDAO;
import tn.esprit.project.dao.OrderStatusDAO;
import tn.esprit.project.dao.FavoriteDAO;

import tn.esprit.project.models.CartItem;
import tn.esprit.project.models.MenuItem;
import tn.esprit.project.models.Menu;
import tn.esprit.project.models.Order;
import tn.esprit.project.models.OrderItem;
import tn.esprit.project.models.Restaurant;
import tn.esprit.project.models.User;
import tn.esprit.project.models.OrderStatusUpdate;
import tn.esprit.project.models.Favorite;

// Increment version to include new columns
@Database(entities = {Restaurant.class, User.class, Menu.class, MenuItem.class, CartItem.class, Order.class, OrderItem.class, OrderStatusUpdate.class, Favorite.class}, version = 6, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = "AppDatabase";

    private static volatile AppDatabase INSTANCE = null;

    public abstract UserDAO userDAO();
    public abstract RestaurantDAO restaurantDAO();
    public abstract MenuItemDAO menuItemDAO();
    public abstract MenuDAO menuDAO();
    public abstract CartDAO cartDAO();
    public abstract OrderDAO orderDAO();
    public abstract OrderStatusDAO orderStatusDAO();
    public abstract FavoriteDAO favoriteDAO();

    // Single thread executor for DB pre-population
    private static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    // Helper: check if column exists in table
    private static boolean columnExists(@NonNull SupportSQLiteDatabase db, @NonNull String tableName, @NonNull String columnName) {
        Cursor cursor = null;
        try {
            cursor = db.query("PRAGMA table_info('" + tableName + "')");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int nameIdx = cursor.getColumnIndex("name");
                    if (nameIdx >= 0) {
                        String col = cursor.getString(nameIdx);
                        if (columnName.equalsIgnoreCase(col)) return true;
                    }
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }

    // Migration 5 -> 6: add customizations column to cart_items (if missing)
    private static final androidx.room.migration.Migration MIGRATION_5_6 = new androidx.room.migration.Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            if (!columnExists(database, "cart_items", "customizations")) {
                try {
                    database.execSQL("ALTER TABLE cart_items ADD COLUMN customizations TEXT DEFAULT NULL");
                    Log.d(TAG, "MIGRATION_5_6: added customizations column to cart_items");
                } catch (Exception e) {
                    Log.w(TAG, "MIGRATION_5_6: failed to add customizations column", e);
                }
            } else {
                Log.d(TAG, "MIGRATION_5_6: customizations column already exists");
            }
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Use application context to avoid leaking an Activity
                    Context appContext = context.getApplicationContext();
                    INSTANCE = Room
                            .databaseBuilder(
                                    appContext,
                                    AppDatabase.class, "FoodDeliveryDb")
                            .addMigrations(MIGRATION_5_6)
                            .addCallback(sRoomDatabaseCallback)
                            .allowMainThreadQueries()
                            // During development prefer destructive fallback to avoid migration breakage
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Callback pour pré-peupler la base lors de sa création
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Log.d(TAG, "onCreate: database created — starting pre-population");

            // Insérer des données d'exemple via SQL direct pour éviter d'utiliser des DAO pendant onCreate
            databaseWriteExecutor.execute(() -> {
                try {
                    // Utilisateur exemple (id auto gen)
                    db.execSQL("INSERT INTO users (name, email, address, phoneNumber, status, avatar_url) VALUES ('Mahdi Chabbouh','mahdichabbouh98@gmail.com','Soukra','+2128249582','ENABLED','@mipmap/profile_round')");

                    // Restaurants
                    db.execSQL("INSERT INTO restaurants (name, address, phone_number, image_url) VALUES ('Pizzeria Roma','123 Main St','+21612345678','@drawable/pizza')"); // Example with local image
                    db.execSQL("INSERT INTO restaurants (name, address, phone_number, image_url) VALUES ('Sushi House','45 Ocean Ave','+21687654321','@drawable/sushi')");
                    db.execSQL("INSERT INTO restaurants (name, address, phone_number, image_url) VALUES ('Tunisian Delights','7 Medina Rd','+21611223344','@drawable/tunisian')");

                    // Menus: for each restaurant create menus (ids will be autogenerated starting at 1)
                    // For restaurant 1 (Pizzeria Roma)
                    db.execSQL("INSERT INTO menus (restaurant_id, name, description, image_url) VALUES (1,'Pizzas','All kinds of pizzas','@drawable/pizza_roma')");
                    db.execSQL("INSERT INTO menus (restaurant_id, name, description, image_url) VALUES (1,'Sandwiches','Fresh sandwiches','@drawable/sandwich')");
                    db.execSQL("INSERT INTO menus (restaurant_id, name, description, image_url) VALUES (1,'Plats','Main dishes','@drawable/plat')");

                    // For restaurant 2 (Sushi House)
                    db.execSQL("INSERT INTO menus (restaurant_id, name, description, image_url) VALUES (2,'Sushi','Assorted sushi','@drawable/assorted_sushi')");
                    db.execSQL("INSERT INTO menus (restaurant_id, name, description, image_url) VALUES (2,'Rolls','Special rolls','@drawable/special_rolls')");

                    // For restaurant 3 (Tunisian Delights)
                    db.execSQL("INSERT INTO menus (restaurant_id, name, description, image_url) VALUES (3,'Starters','Traditional starters','')");
                    db.execSQL("INSERT INTO menus (restaurant_id, name, description, image_url) VALUES (3,'Main','Couscous and more','')");

                    // Menu items referencing menu_id (we assume insertion order maps ids sequentially)
                    // Pizzas menu (menu_id = 1)
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (1,'Margherita','Classic tomato & cheese',6.5,'')");
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (1,'Neptune','Seafood pizza',8.5,'')");
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (1,'4 Saisons','Mixed toppings',8.0,'')");

                    // Sandwiches menu (menu_id = 2)
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (2,'Club Sandwich','Chicken, lettuce, tomato',5.5,'')");
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (2,'Veggie Sandwich','Fresh veggies',4.5,'')");

                    // Set available_customizations for sandwich menu (menu_id = 2)
                    String sandwichCustomJson = "[\"Oignon\",\"Oignon caramélisé\",\"Harissa\",\"Tomate\"]";
                    try {
                        db.execSQL("UPDATE menu_items SET available_customizations = '" + sandwichCustomJson + "' WHERE menu_id = 2");
                    } catch (Exception ignored) {}

                    // Plats menu (menu_id = 3)
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (3,'Lasagna','Baked lasagna',9.0,'')");

                    // Sushi House menus: menu_id = 4 and 5
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (4,'California Roll','Crab, avocado',5.0,'')");
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (4,'Salmon Nigiri','Fresh salmon',4.0,'')");
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (5,'Spicy Roll','Spicy tuna roll',6.0,'')");

                    // Tunisian Delights menus: menu_id = 6 and 7
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (6,'Brik','Tunisian pastry with egg',3.0,'')");
                    db.execSQL("INSERT INTO menu_items (menu_id, name, description, price, image_url) VALUES (7,'Couscous','Traditional couscous',8.0,'')");

                    //db.execSQL("INSERT INTO OrderStatusUpdate (order_id, status, timestamp, latitude, longitude) VALUES (1, 'PENDING', '2025-11-28T10:00:00Z', 14.6937, -17.44406)");
                    //db.execSQL("INSERT INTO OrderStatusUpdate (order_id, status, timestamp, latitude, longitude) VALUES (1, 'PREPARING', '2025-11-28T10:05:00Z', 14.6940, -17.44450)");
                    //db.execSQL("INSERT INTO OrderStatusUpdate (order_id, status, timestamp, latitude, longitude) VALUES (1, 'OUT_FOR_DELIVERY', '2025-11-28T10:20:00Z', 14.6950, -17.44500)");
                    //db.execSQL("INSERT INTO OrderStatusUpdate (order_id, status, timestamp, latitude, longitude) VALUES (1, 'DELIVERED', '2025-11-28T10:30:00Z', 14.6960, -17.44600)");


                    Log.d(TAG, "Pre-population completed successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Pre-population failed", e);
                }
            });
        }
    };
}
