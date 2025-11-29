package tn.esprit.project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.File;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tn.esprit.project.R;
import tn.esprit.project.adapters.MenuAdapter;
import tn.esprit.project.api.NominatimApi;
import tn.esprit.project.models.Menu;
import tn.esprit.project.models.NominatimResponse;
import tn.esprit.project.models.Restaurant;
import tn.esprit.project.utils.MyDatabase;

public class RestaurantDetailsFragment extends Fragment {

    private ImageView ivLogo;
    private TextView tvName, tvAddress, tvCategory;
    private Button btnAddMenu;
    private RecyclerView rvMenus;
    private MapView mapView;

    private MyDatabase db;
    private Restaurant restaurant;
    private MenuAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_details, container, false);

        // --- Initialisation des vues ---
        Button btnBack = view.findViewById(R.id.btnBack);
        ivLogo = view.findViewById(R.id.ivLogo);
        tvName = view.findViewById(R.id.tvName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvCategory = view.findViewById(R.id.tvCategory);
        btnAddMenu = view.findViewById(R.id.btnAddMenu);
        rvMenus = view.findViewById(R.id.rvMenus);
        rvMenus.setLayoutManager(new LinearLayoutManager(requireContext()));

        mapView = view.findViewById(R.id.mapView);
        Configuration.getInstance().setUserAgentValue("RestaurantApp/1.0 (maryembengarfa@gmail.com)");
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // --- Base de données ---
        db = MyDatabase.getInstance(requireContext());
        int restaurantId = getArguments() != null ? getArguments().getInt("restaurant_id", -1) : -1;

        if (restaurantId != -1) {
            restaurant = db.restaurantDAO().getRestaurantById(restaurantId);
            if (restaurant != null) {
                displayRestaurantInfo();
                loadMenus();
                geocodeAddressAndShowOnMap(restaurant.getAddress());
            }
        }

        btnAddMenu.setOnClickListener(v -> openAddMenuFragment());

        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            } else if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void displayRestaurantInfo() {
        tvName.setText(restaurant.getName());
        tvAddress.setText(restaurant.getAddress());
        tvCategory.setText(restaurant.getCategory().name());

        String logo = restaurant.getLogoUri();
        if (logo != null && !logo.isEmpty()) {
            Glide.with(this)
                    .load(logo.startsWith("/") ? new File(logo) : logo)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ivLogo);
        } else {
            ivLogo.setImageResource(R.mipmap.ic_launcher);
        }
    }

    private void loadMenus() {
        List<Menu> menus = db.menuDAO().getMenusByRestaurant(restaurant.getId());

        menuAdapter = new MenuAdapter(menus, new MenuAdapter.MenuAdapterListener() {
            @Override
            public void onAddItem(Menu menu) {
                AddItemFragment fragment = new AddItemFragment();
                Bundle b = new Bundle();
                b.putInt("menu_id", menu.getId());
                fragment.setArguments(b);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onMenuDetails(Menu menu) {
                MenuDetailsActivity.start(requireContext(), menu.getId());
            }

            @Override
            public void onUpdateMenu(Menu menu) {
                UpdateMenuFragment fragment = UpdateMenuFragment.newInstance(menu.getId());
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        }, db);

        rvMenus.setAdapter(menuAdapter);
    }

    private void openAddMenuFragment() {
        AddMenuFragment fragment = new AddMenuFragment();
        Bundle b = new Bundle();
        b.putInt("restaurant_id", restaurant.getId());
        fragment.setArguments(b);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // -------- Géocodage avec Nominatim --------
    private void geocodeAddressAndShowOnMap(String address) {
        if (address == null || address.isEmpty()) return;

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("User-Agent", "RestaurantApp/1.0")
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

        NominatimApi nominatimApi = retrofit.create(NominatimApi.class);

        nominatimApi.searchAddress(address, "json", 1, 1, "tn") // 1 résultat max
                .enqueue(new Callback<List<NominatimResponse>>() {
                    @Override
                    public void onResponse(Call<List<NominatimResponse>> call, Response<List<NominatimResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            NominatimResponse result = response.body().get(0);
                            double lat = Double.parseDouble(result.lat);
                            double lon = Double.parseDouble(result.lon);
                            showMarkerOnMap(lat, lon);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<NominatimResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void showMarkerOnMap(double lat, double lon) {
        GeoPoint point = new GeoPoint(lat, lon);
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(restaurant.getName());
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(point);
    }

    public void refreshMenus() {
        if (menuAdapter != null) {
            menuAdapter.setMenus(db.menuDAO().getMenusByRestaurant(restaurant.getId()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        refreshMenus();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDetach();
    }
}
