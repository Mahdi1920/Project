package tn.esprit.project.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import tn.esprit.project.models.Restaurant;
import tn.esprit.project.repository.IClientRepository;

public class RestaurantsViewModel extends ViewModel {
    private final IClientRepository repository;

    public RestaurantsViewModel(IClientRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        return repository.getAllRestaurants();
    }
}

