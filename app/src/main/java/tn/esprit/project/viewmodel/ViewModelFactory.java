package tn.esprit.project.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import tn.esprit.project.repository.IClientRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final IClientRepository repository;

    public ViewModelFactory(IClientRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            // try to create ViewModel with IClientRepository constructor
            return modelClass.getConstructor(IClientRepository.class).newInstance(repository);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
