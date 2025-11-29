package tn.esprit.project.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import tn.esprit.project.R;
import tn.esprit.project.models.Item;
import tn.esprit.project.fragments.MenuDetailsActivity;
import tn.esprit.project.utils.MyDatabase;

public class AddItemFragment extends Fragment {

    private EditText etName, etDescription, etPrice;
    private ImageView ivImage;
    private Button btnSelectImage, btnAddItem;
    private Uri selectedImageUri;

    private int menuId;
    private MyDatabase db;

    private ActivityResultLauncher<String> pickImageLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etPrice = view.findViewById(R.id.etPrice);
        ivImage = view.findViewById(R.id.ivImage);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnAddItem = view.findViewById(R.id.btnAddItem);

        db = MyDatabase.getInstance(requireContext());

        // Récupération menuId
        if (getArguments() != null) {
            menuId = getArguments().getInt("menu_id", -1);
        }

        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Sélecteur d'image (aucune permission requise)
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleImageSelection
        );

        btnSelectImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnAddItem.setOnClickListener(v -> addItem());

        return view;
    }

    private void handleImageSelection(Uri uri) {
        if (uri == null) return;

        selectedImageUri = copyImageToInternalStorage(uri);

        Glide.with(this)
                .load(selectedImageUri != null ? selectedImageUri : uri)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(ivImage);
    }

    private Uri copyImageToInternalStorage(Uri uri) {
        try {
            InputStream in = requireContext().getContentResolver().openInputStream(uri);
            if (in == null) return null;

            File dir = new File(requireContext().getFilesDir(), "images");
            if (!dir.exists()) dir.mkdirs();

            String ext = getExtensionFromUri(uri);
            if (ext == null) ext = "jpg";

            File file = new File(dir, "item_" + System.currentTimeMillis() + "." + ext);
            OutputStream out = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            out.close();

            return Uri.fromFile(file);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getExtensionFromUri(Uri uri) {
        try {
            String type = requireContext().getContentResolver().getType(uri);
            if (type != null) {
                return MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void addItem() {
        String name = etName.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty()) {
            showAlert("Veuillez saisir le nom de l'item");
            return;
        }
        if (priceStr.isEmpty()) {
            showAlert("Veuillez saisir le prix");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            showAlert("Prix invalide");
            return;
        }

        String imageUri = selectedImageUri != null ? selectedImageUri.toString() : null;

        Item item = new Item(menuId, name, desc, price, imageUri);
        db.itemDAO().insert(item);

        Toast.makeText(requireContext(), "Item ajouté avec succès", Toast.LENGTH_SHORT).show();

        // Redirection vers MenuDetailsActivity
        MenuDetailsActivity.start(requireContext(), menuId);

        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void showAlert(String msg) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Attention")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}
