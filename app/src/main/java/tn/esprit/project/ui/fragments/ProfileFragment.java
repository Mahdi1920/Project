package tn.esprit.project.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import tn.esprit.project.R;
import tn.esprit.project.models.User;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.utils.NavigationUtils;
import tn.esprit.project.viewmodel.ProfileViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;

public class ProfileFragment extends Fragment {

    private static final int USER_ID = 1; // demo user id

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView name = view.findViewById(R.id.tv_profile_name);
        TextView email = view.findViewById(R.id.tv_profile_email);
        Button edit = view.findViewById(R.id.btn_edit_profile);

        // obtain repository & viewmodel
        ClientRepository repo = new ClientRepository(requireContext());
        ProfileViewModel vm = new ViewModelProvider(this, new ViewModelFactory(repo)).get(ProfileViewModel.class);

        // load user synchronously (repository returns User)
        User user = vm.getUser(USER_ID);
        if (user != null) {
            name.setText(user.getName() != null ? user.getName() : "Utilis. Exemple");
            email.setText(user.getEmail() != null ? user.getEmail() : "user@example.com");
        } else {
            name.setText("Utilisateur Exemple");
            email.setText("user@example.com");
        }

        edit.setOnClickListener(v -> NavigationUtils.navigateTo(ProfileFragment.this, v, R.id.editProfileFragment, null));
    }
}
