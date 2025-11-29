package tn.esprit.project.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tn.esprit.project.R;
import tn.esprit.project.api.NominatimApi;
import tn.esprit.project.fragments.adapters.NominatimAdapter;
import tn.esprit.project.models.Category;
import tn.esprit.project.models.NominatimResponse;
import tn.esprit.project.models.Restaurant;
import tn.esprit.project.utils.MyDatabase;

public class UpdateRestaurantFragment extends Fragment {

    private boolean isSelecting = false;
    private RecyclerView recyclerViewSuggestions;
    private NominatimAdapter nominatimAdapter;
    private NominatimApi nominatimApi;

    private EditText etName, etAddress;
    private Spinner spinnerCategory;
    private Button btnSelectImage, btnUpdate;
    private ImageView ivLogo;
    private Uri selectedImageUri;

    private MyDatabase db;
    private Restaurant restaurant;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    private static final String TAG = "UpdateRestaurantFragment";
    private static final String ARG_RESTAURANT_ID = "restaurant_id";

    public static UpdateRestaurantFragment newInstance(int restaurantId) {
        UpdateRestaurantFragment fragment = new UpdateRestaurantFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RESTAURANT_ID, restaurantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_restaurant, container, false);

        // --- Initialisation des vues ---
        db = MyDatabase.getInstance(requireContext());
        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        ivLogo = view.findViewById(R.id.ivLogo);
        btnUpdate = view.findViewById(R.id.btnAdd);
        btnUpdate.setText("Mettre à jour");

        // --- RecyclerView pour suggestions d'adresse ---
        recyclerViewSuggestions = view.findViewById(R.id.recyclerViewSuggestions);
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        nominatimAdapter = new NominatimAdapter(new ArrayList<>(), item -> {
            isSelecting = true;
            etAddress.setText(item.display_name);
            recyclerViewSuggestions.setVisibility(View.GONE);
            nominatimAdapter.clearData();

            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(etAddress.getWindowToken(), 0);

            etAddress.clearFocus();
            isSelecting = false;
        });
        recyclerViewSuggestions.setAdapter(nominatimAdapter);

        // --- Retrofit pour autocomplete adresse ---
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("User-Agent", "RestaurantApp/1.0 (mariem.bengarfa@gmail.com)")
                            .header("Accept-Language", "fr")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nominatimApi = retrofit.create(NominatimApi.class);

        // --- Spinner catégories avec placeholder ---
        List<Category> categoriesList = new ArrayList<>();
        categoriesList.add(null); // placeholder
        categoriesList.addAll(Arrays.asList(Category.values()));

        ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(requireContext(),
                android.R.layout.simple_spinner_item, categoriesList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view;
                Category c = getItem(position);
                text.setTextColor(Color.BLACK);
                text.setText(c == null ? "Sélectionnez une catégorie" : c.name());
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view;
                Category c = getItem(position);
                text.setTextColor(Color.BLACK);
                text.setText(c == null ? "Sélectionnez une catégorie" : c.name());
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setSelection(0); // placeholder par défaut

        // --- Récupérer restaurant depuis ID ---
        int id = getArguments().getInt(ARG_RESTAURANT_ID, -1);
        restaurant = db.restaurantDAO().getRestaurantById(id);
        if (restaurant != null) {
            etName.setText(restaurant.getName());
            etAddress.setText(restaurant.getAddress());
            selectedImageUri = restaurant.getLogoUri() != null ? Uri.parse(restaurant.getLogoUri()) : null;
            Glide.with(this)
                    .load(selectedImageUri != null ? selectedImageUri : R.mipmap.ic_launcher)
                    .into(ivLogo);

            // --- Sélection spinner catégorie selon restaurant ---
            if (restaurant.getCategory() != null) {
                for (int i = 0; i < categoriesList.size(); i++) {
                    if (restaurant.getCategory() == categoriesList.get(i)) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
        }

        // --- TextWatcher pour autocomplete adresse ---
        etAddress.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isSelecting) return;
                if (s.length() > 2) {
                    recyclerViewSuggestions.setVisibility(View.VISIBLE);
                    nominatimApi.searchAddress(s.toString(), "json", 1, 5, "tn")
                            .enqueue(new Callback<List<NominatimResponse>>() {
                                @Override
                                public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                        nominatimAdapter.updateData(response.body());
                                    } else {
                                        nominatimAdapter.clearData();
                                    }
                                }
                                @Override
                                public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                                    nominatimAdapter.clearData();
                                    Log.e(TAG, "Erreur API Nominatim", t);
                                }
                            });
                } else {
                    nominatimAdapter.clearData();
                }
            }
        });

        // --- Bouton retour ---
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
        });

        // --- Permissions et pick media ---
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openPhotoPicker();
                    else Toast.makeText(requireContext(),
                            "Permission nécessaire pour accéder aux images", Toast.LENGTH_LONG).show();
                }
        );

        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                this::handleImageSelection
        );

        btnSelectImage.setOnClickListener(v -> checkPermissionsAndOpenPicker());
        btnUpdate.setOnClickListener(v -> updateRestaurant());

        return view;
    }

    // ------------------------- Fonctions auxiliaires -------------------------
    private void checkPermissionsAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED)
                openPhotoPicker();
            else requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                openPhotoPicker();
            else requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        } else openPhotoPicker();
    }

    private void openPhotoPicker() {
        pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void handleImageSelection(Uri uri) {
        if (uri != null) {
            grantPersistableUriPermission(uri);
            selectedImageUri = copyImageToInternalStorage(uri);
            if (selectedImageUri == null) selectedImageUri = uri;
            Glide.with(this).load(selectedImageUri)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ivLogo);
        }
    }

    private void grantPersistableUriPermission(Uri uri) {
        try {
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            requireContext().getContentResolver().takePersistableUriPermission(uri, takeFlags);
        } catch (SecurityException e) { e.printStackTrace(); }
    }

    private Uri copyImageToInternalStorage(Uri uri) {
        try (InputStream in = requireContext().getContentResolver().openInputStream(uri)) {
            if (in == null) return null;
            String ext = getExtensionFromUri(uri);
            File imagesDir = new File(requireContext().getFilesDir(), "images");
            if (!imagesDir.exists()) imagesDir.mkdirs();
            File file = new File(imagesDir, "img_" + System.currentTimeMillis() + (ext != null ? "." + ext : ".jpg"));
            try (OutputStream out = new FileOutputStream(file)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }
            return Uri.fromFile(file);
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    private String getExtensionFromUri(Uri uri) {
        try {
            String ext = null;
            String type = requireContext().getContentResolver().getType(uri);
            if (type != null) ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
            if (ext == null && uri.getPath() != null) {
                int dot = uri.getPath().lastIndexOf('.');
                if (dot != -1 && dot < uri.getPath().length() - 1)
                    ext = uri.getPath().substring(dot + 1);
            }
            return ext;
        } catch (Exception e) { return null; }
    }

    private void updateRestaurant() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();

        if (name.isEmpty() || address.isEmpty()) {
            Toast.makeText(requireContext(), "Nom et adresse requis", Toast.LENGTH_SHORT).show();
            return;
        }

        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setLogoUri(selectedImageUri != null ? selectedImageUri.toString() : null);

        // On ne change la catégorie que si l'utilisateur en a sélectionné une
        if (selectedCategory != null) {
            restaurant.setCategory(selectedCategory);
        }

        db.restaurantDAO().update(restaurant);

        Toast.makeText(requireContext(), "Restaurant mis à jour", Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
