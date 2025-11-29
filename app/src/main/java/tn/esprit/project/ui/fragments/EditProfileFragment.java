package tn.esprit.project.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import tn.esprit.project.R;
import tn.esprit.project.models.User;
import tn.esprit.project.repository.ClientRepository;
import tn.esprit.project.utils.NavigationUtils;
import tn.esprit.project.viewmodel.ProfileViewModel;
import tn.esprit.project.viewmodel.ViewModelFactory;

public class EditProfileFragment extends Fragment {
    private static final int USER_ID = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText etName = view.findViewById(R.id.et_name);
        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPhone = view.findViewById(R.id.et_phone);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_profile);

        ClientRepository repo = new ClientRepository(requireContext());
        ProfileViewModel vm = new ViewModelProvider(this, new ViewModelFactory(repo)).get(ProfileViewModel.class);

        // Load user synchronously
        User user = vm.getUser(USER_ID);
        if (user != null) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
                Toast.makeText(requireContext(), "Le nom et l'email sont requis", Toast.LENGTH_SHORT).show();
                return;
            }

            User u = new User();
            u.setId(USER_ID);
            u.setName(name);
            u.setEmail(email);
            u.setPhoneNumber(phone);

            // update user in DB
            vm.updateUser(u);
            Toast.makeText(requireContext(), "Profil mis à jour", Toast.LENGTH_SHORT).show();

            // navigate back to profile (use NavController if available)
            androidx.navigation.NavController nav = NavigationUtils.findNavController(EditProfileFragment.this, v);
            if (nav != null) {
                try {
                    nav.navigateUp();
                } catch (Exception e) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            } else {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
