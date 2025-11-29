package tn.esprit.project.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import tn.esprit.project.models.Menu;
import tn.esprit.project.models.MenuItem;
import tn.esprit.project.repository.IClientRepository;

public class MenuViewModel extends ViewModel {
    private final IClientRepository repository;

    public MenuViewModel(IClientRepository repository) {
        this.repository = repository;
    }

    // Backward-compatible: returns menu items for a restaurant (via repository join)
    public LiveData<List<MenuItem>> getMenuForRestaurant(int restaurantId) {
        return repository.getMenuForRestaurant(restaurantId);
    }

    public LiveData<List<Menu>> getMenusForRestaurant(int restaurantId) {
        return repository.getMenusForRestaurant(restaurantId);
    }

    public LiveData<List<MenuItem>> getItemsForMenu(int menuId) {
        return repository.getItemsForMenu(menuId);
    }

    // Added: expose single menu item as LiveData
    public LiveData<MenuItem> getMenuItemById(int id) {
        return repository.getMenuItemById(id);
    }
}
