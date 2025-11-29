package tn.esprit.project.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import tn.esprit.project.utils.CategoryConverter;

@Entity(tableName = "restaurants")
public class Restaurant {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String address;
    private String logoUri;

    @TypeConverters(CategoryConverter.class)
    private Category category;

    // Constructeur
    public Restaurant(String name, String address, String logoUri, Category category) {
        this.name = name;
        this.address = address;
        this.logoUri = logoUri;
        this.category = category;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getLogoUri() { return logoUri; }
    public void setLogoUri(String logoUri) { this.logoUri = logoUri; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
