package tn.esprit.project.viewmodel;

import androidx.lifecycle.ViewModel;

import tn.esprit.project.models.User;
import tn.esprit.project.repository.IClientRepository;

public class ProfileViewModel extends ViewModel {
    private final IClientRepository repository;

    public ProfileViewModel(IClientRepository repository) {
        this.repository = repository;
    }

    public User getUser(int id) {
        return repository.getUser(id);
    }

    public void updateUser(User user) {
        repository.updateUser(user);
    }
}

