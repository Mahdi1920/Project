package tn.esprit.project;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import tn.esprit.project.fragments.AddRestaurantFragment;
import tn.esprit.project.fragments.HomeFragment;
import tn.esprit.project.fragments.RestaurantListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Charger HomeFragment au lancement
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

}
