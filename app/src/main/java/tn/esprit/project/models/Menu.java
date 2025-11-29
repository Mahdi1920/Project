package tn.esprit.project.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

import java.util.List;

@Entity(tableName = "menus")
public class Menu {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int restaurantId; // FK vers Restaurant
    private String name;
    private String description;
    private String imageUri; // Optionnelle

    @Ignore
    private List<Item> items; // Liste d'items, non persistée directement

    public Menu(int restaurantId, String name, String description, String imageUri) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.imageUri = imageUri;
    }

    // GETTERS & SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}
