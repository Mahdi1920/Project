package tn.esprit.project.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import tn.esprit.project.R;
import tn.esprit.project.models.Menu;
import tn.esprit.project.utils.MyDatabase;

public class AddMenuFragment extends Fragment {

    private EditText etTitle, etDescription;
    private Button btnPickImage, btnSaveMenu, btnBack;
    private ImageView ivMenuImage;

    private Uri selectedImageUri;
    private int restaurantId;

    private MyDatabase db;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_menu, container, false);

        db = MyDatabase.getInstance(getContext());

        // Initialisation des views
        etTitle = view.findViewById(R.id.etMenuTitle);
        etDescription = view.findViewById(R.id.etMenuDescription);
        btnPickImage = view.findViewById(R.id.btnPickMenuImage);
        btnSaveMenu = view.findViewById(R.id.btnSaveMenu);
        ivMenuImage = view.findViewById(R.id.ivMenuImage);
        btnBack = view.findViewById(R.id.btnBack);

        // Récupérer l'ID du restaurant
        restaurantId = getArguments() != null ? getArguments().getInt("restaurant_id", -1) : -1;
        if (restaurantId == -1) {
            Toast.makeText(getContext(), "Erreur: restaurant inconnu", Toast.LENGTH_SHORT).show();
        }

        // Bouton Retour
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Limiter la description à 10 mots
        etDescription.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String text = s.toString();
                String[] words = text.trim().split("\\s+");
                if (words.length > 10) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 10; i++) {
                        sb.append(words[i]);
                        if (i != 9) sb.append(" ");
                    }
                    etDescription.setText(sb.toString());
                    etDescription.setSelection(sb.length());
                    Toast.makeText(getContext(), "Description limitée à 10 mots.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Choisir une image depuis la galerie
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null &&
                            result.getData().getData() != null) {

                        selectedImageUri = result.getData().getData();
                        ivMenuImage.setImageURI(selectedImageUri);
                    }
                }
        );

        btnPickImage.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pick);
        });

        btnSaveMenu.setOnClickListener(v -> saveMenu());

        return view;
    }

    private void saveMenu() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            showAlert("Veuillez entrer un titre.");
            return;
        }

        // Image optionnelle
        String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : null;

        // Créer le menu
        Menu menu = new Menu(restaurantId, title, description, imageUriStr);

        // Insérer dans la base
        db.menuDAO().insert(menu);

        Toast.makeText(getContext(), "Menu ajouté avec succès !", Toast.LENGTH_SHORT).show();

        // Réinitialiser le formulaire
        etTitle.setText("");
        etDescription.setText("");
        ivMenuImage.setImageResource(R.mipmap.ic_launcher);
        selectedImageUri = null;

        // Retour au fragment précédent
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void showAlert(String msg) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Attention")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}
