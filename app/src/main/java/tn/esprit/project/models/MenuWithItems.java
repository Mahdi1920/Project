package tn.esprit.project.models;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class MenuWithItems {
    @Embedded
    private Menu menu;

    @Relation(parentColumn = "id", entityColumn = "menu_id")
    private List<MenuItem> items;

    public MenuWithItems() {
    }

    public MenuWithItems(Menu menu, List<MenuItem> items) {
        this.menu = menu;
        this.items = items;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }
}

