package tn.esprit.project.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RestaurantWithMenu {
    @Embedded
    private Restaurant restaurant;

    @Relation(parentColumn = "id", entityColumn = "restaurant_id")
    private List<Menu> menus;

    public RestaurantWithMenu() {}

    public RestaurantWithMenu(Restaurant restaurant, List<Menu> menus) {
        this.restaurant = restaurant;
        this.menus = menus;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
