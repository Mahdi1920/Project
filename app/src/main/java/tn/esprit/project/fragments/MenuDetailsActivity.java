package tn.esprit.project.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.models.Item;
import tn.esprit.project.models.Menu;
import tn.esprit.project.utils.MyDatabase;

public class MenuDetailsActivity extends AppCompatActivity {

    private TextView tvMenuTitle, tvMenuDescription;
    private ImageView ivMenuImage;
    private Button btnAddItem;
    private LinearLayout itemsContainer;

    private Menu menu;
    private MyDatabase db;

    // URI de l'image sélectionnée pour le dialog
    private Uri selectedImageUri;
    private ImageView imgPreviewDialog;

    private static final int PICK_IMAGE_REQUEST = 200;

    // --- Méthode utilitaire pour lancer cette Activity ---
    public static void start(Context context, int menuId) {
        Intent intent = new Intent(context, MenuDetailsActivity.class);
        intent.putExtra("menu_id", menuId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_details);

        // --- Bouton Retour ---
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // --- Initialisation des vues ---
        tvMenuTitle = findViewById(R.id.tvMenuTitle);
        tvMenuDescription = findViewById(R.id.tvMenuDescription);
        ivMenuImage = findViewById(R.id.ivMenuImage);
        btnAddItem = findViewById(R.id.btnAddItem);
        itemsContainer = findViewById(R.id.itemsContainer);

        db = MyDatabase.getInstance(this);

        int menuId = getIntent().getIntExtra("menu_id", -1);
        menu = db.menuDAO().getMenuById(menuId);

        if (menu != null) {
            tvMenuTitle.setText(menu.getName());
            tvMenuDescription.setText(menu.getDescription());

            // --- Charger image du menu ---
            if (menu.getImageUri() != null && !menu.getImageUri().isEmpty()) {
                String uri = menu.getImageUri();
                Glide.with(this)
                        .load(uri.startsWith("/") ? new File(uri) : uri)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(ivMenuImage);
            } else {
                ivMenuImage.setImageResource(R.mipmap.ic_launcher);
            }

            loadItems();
        }

        btnAddItem.setOnClickListener(v -> showAddItemDialog());
    }

    // --- Charger tous les items du menu ---
    private void loadItems() {
        itemsContainer.removeAllViews();
        List<Item> items = db.itemDAO().getItemsByMenu(menu.getId());

        for (Item item : items) {
            LinearLayout itemView = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.item_item, itemsContainer, false);

            TextView tvName = itemView.findViewById(R.id.tvItemName);
            TextView tvDesc = itemView.findViewById(R.id.tvItemDescription);
            TextView tvPrice = itemView.findViewById(R.id.tvItemPrice);
            ImageView ivImage = itemView.findViewById(R.id.ivItemImage);

            tvName.setText(item.getName());
            tvDesc.setText(item.getDescription());
            tvPrice.setText(String.format("%.2f €", item.getPrice()));

            // --- Charger image de l'item ---
            if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
                String uri = item.getImageUri();
                Glide.with(this)
                        .load(uri.startsWith("/") ? new File(uri) : uri)
                        .placeholder(R.mipmap.ic_launcher)
                        .error(R.mipmap.ic_launcher)
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.mipmap.ic_launcher);
            }

            itemsContainer.addView(itemView);
        }
    }

    // --- Dialog pour ajouter un item ---
    private void showAddItemDialog() {
        selectedImageUri = null; // réinitialiser à chaque ouverture
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_item);
        // --- Changer le fond du dialog en blanc ---
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
        EditText etName = dialog.findViewById(R.id.etItemName);
        EditText etDesc = dialog.findViewById(R.id.etItemDescription);
        EditText etPrice = dialog.findViewById(R.id.etItemPrice);
        Button btnSave = dialog.findViewById(R.id.btnSaveItem);
        Button btnChooseImage = dialog.findViewById(R.id.btnChooseImage);
        imgPreviewDialog = dialog.findViewById(R.id.ivImage);
        if (imgPreviewDialog == null) {
            imgPreviewDialog = dialog.findViewById(R.id.imgPreview); // selon ton layout
        }

        // --- Sélection d'image ---
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // --- Bouton enregistrer ---
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (name.isEmpty()) { etName.setError("Nom requis"); return; }
            if (priceStr.isEmpty()) { etPrice.setError("Prix requis"); return; }

            double price;
            try { price = Double.parseDouble(priceStr); }
            catch (NumberFormatException e) { etPrice.setError("Prix invalide"); return; }

            String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : null;

            Item newItem = new Item(menu.getId(), name, desc, price, imageUriStr);
            db.itemDAO().insert(newItem);

            loadItems();
            dialog.dismiss();
        });

        dialog.show();
    }

    // --- Récupération image depuis galerie ---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (imgPreviewDialog != null && selectedImageUri != null) {
                imgPreviewDialog.setImageURI(selectedImageUri);
            }
        }
    }
}
