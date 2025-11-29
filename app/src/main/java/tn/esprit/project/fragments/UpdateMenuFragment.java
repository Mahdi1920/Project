package tn.esprit.project.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import tn.esprit.project.R;
import tn.esprit.project.models.Menu;
import tn.esprit.project.utils.MyDatabase;

public class UpdateMenuFragment extends Fragment {

    private EditText etTitle, etDescription;
    private Button btnPickImage, btnUpdateMenu, btnBack;
    private ImageView ivMenuImage;

    private Uri selectedImageUri;
    private int menuId;
    private Menu menu;

    private MyDatabase db;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    public static UpdateMenuFragment newInstance(int menuId) {
        UpdateMenuFragment fragment = new UpdateMenuFragment();
        Bundle b = new Bundle();
        b.putInt("menu_id", menuId);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_menu, container, false);

        db = MyDatabase.getInstance(getContext());

        // init views
        etTitle = view.findViewById(R.id.etMenuTitle);
        etDescription = view.findViewById(R.id.etMenuDescription);
        btnPickImage = view.findViewById(R.id.btnPickMenuImage);
        btnUpdateMenu = view.findViewById(R.id.btnSaveMenu);
        ivMenuImage = view.findViewById(R.id.ivMenuImage);
        btnBack = view.findViewById(R.id.btnBack);

        btnUpdateMenu.setText("Mettre à jour");

        // Récupérer menu_id
        menuId = getArguments() != null ? getArguments().getInt("menu_id", -1) : -1;
        if (menuId != -1) {
            menu = db.menuDAO().getMenuById(menuId);
            fillForm();
        }

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK &&
                            result.getData() != null &&
                            result.getData().getData() != null) {

                        Uri originalUri = result.getData().getData();
                        selectedImageUri = copyImageToInternalStorage(originalUri);
                        if (selectedImageUri != null) {
                            ivMenuImage.setImageURI(selectedImageUri);
                        } else {
                            Toast.makeText(getContext(), "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        btnPickImage.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(pick);
        });

        btnUpdateMenu.setOnClickListener(v -> updateMenu());

        return view;
    }

    private void fillForm() {
        etTitle.setText(menu.getName());
        etDescription.setText(menu.getDescription());

        if (menu.getImageUri() != null) {
            try {
                Uri uri = Uri.parse(menu.getImageUri());
                // juste afficher l'image, ne pas copier
                ivMenuImage.setImageURI(uri);
                selectedImageUri = uri; // pour updateMenu(), on garde le chemin existant
            } catch (Exception e) {
                e.printStackTrace();
                ivMenuImage.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            ivMenuImage.setImageResource(R.mipmap.ic_launcher);
        }
    }


    private void updateMenu() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Titre requis", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : null;

        menu.setName(title);
        menu.setDescription(description);
        menu.setImageUri(imageUriStr);

        db.menuDAO().update(menu);

        Toast.makeText(getContext(), "Menu mis à jour !", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    // Copier l'image sélectionnée dans le stockage interne pour éviter SecurityException
    private Uri copyImageToInternalStorage(Uri originalUri) {
        try {
            ContentResolver resolver = requireContext().getContentResolver();
            InputStream in = resolver.openInputStream(originalUri);
            if (in == null) return null;

            File dir = requireContext().getFilesDir();
            String fileName = "menu_" + System.currentTimeMillis() + ".png";
            File file = new File(dir, fileName);
            OutputStream out = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();

            return Uri.fromFile(file);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
