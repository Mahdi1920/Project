package tn.esprit.project.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

import tn.esprit.project.R;
import tn.esprit.project.utils.NavigationUtils;
import tn.esprit.project.utils.AppDatabase;
import tn.esprit.project.models.CartItem;

public class SandwichCustomizationFragment extends Fragment {
    private int menuItemId;
    private int quantity = 1;

    public SandwichCustomizationFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sandwich_customization, container, false);
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) menuItemId = getArguments().getInt("menuItemId", 0);

        TextView tv = view.findViewById(R.id.tv_customize_item_name);
        if (tv != null) tv.setText(getString(R.string.personnaliser_sandwich_id, menuItemId));

        CheckBox cbOnion = view.findViewById(R.id.cb_onion);
        CheckBox cbCaramelized = view.findViewById(R.id.cb_caramelized);
        CheckBox cbHarissa = view.findViewById(R.id.cb_harissa);
        CheckBox cbTomato = view.findViewById(R.id.cb_tomato);

        TextView tvQty = view.findViewById(R.id.tv_qty);
        Button btnMinus = view.findViewById(R.id.btn_qty_minus);
        Button btnPlus = view.findViewById(R.id.btn_qty_plus);
        Button add = view.findViewById(R.id.btn_customize_add);
        Button cancel = view.findViewById(R.id.btn_customize_cancel);

        if (tvQty != null) tvQty.setText(String.valueOf(quantity));

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                if (tvQty != null) tvQty.setText(String.valueOf(quantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            quantity++;
            if (tvQty != null) tvQty.setText(String.valueOf(quantity));
        });

        add.setOnClickListener(v -> {
            // Build a readable summary of selected ingredients
            StringBuilder sb = new StringBuilder();
            sb.append(getString(R.string.add_to_cart_french)).append("\n\n");
            sb.append(getString(R.string.item_name)).append(": ").append(menuItemId).append('\n');
            sb.append(getString(R.string.quantity)).append(" : ").append(quantity).append('\n');
            sb.append(getString(R.string.choose_ingredients)).append('\n');
            if (cbOnion != null && cbOnion.isChecked()) sb.append("- ").append(getString(R.string.onion)).append('\n');
            if (cbCaramelized != null && cbCaramelized.isChecked()) sb.append("- ").append(getString(R.string.caramelized_onion)).append('\n');
            if (cbHarissa != null && cbHarissa.isChecked()) sb.append("- ").append(getString(R.string.harissa)).append('\n');
            if (cbTomato != null && cbTomato.isChecked()) sb.append("- ").append(getString(R.string.tomato)).append('\n');

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.add_to_cart_french)
                    .setMessage(sb.toString())
                    // Positive: add and go to cart
                    .setPositiveButton(R.string.go_to_cart, (dialog, which) -> {
                        Bundle result = new Bundle();
                        result.putInt("menuItemId", menuItemId);
                        result.putBoolean("onion", cbOnion != null && cbOnion.isChecked());
                        result.putBoolean("caramelized_onion", cbCaramelized != null && cbCaramelized.isChecked());
                        result.putBoolean("harissa", cbHarissa != null && cbHarissa.isChecked());
                        result.putBoolean("tomato", cbTomato != null && cbTomato.isChecked());
                        result.putInt("quantity", quantity);

                        // Prepare customizations list
                        List<String> customizations = new ArrayList<>();
                        if (cbOnion != null && cbOnion.isChecked()) customizations.add(getString(R.string.onion));
                        if (cbCaramelized != null && cbCaramelized.isChecked()) customizations.add(getString(R.string.caramelized_onion));
                        if (cbHarissa != null && cbHarissa.isChecked()) customizations.add(getString(R.string.harissa));
                        if (cbTomato != null && cbTomato.isChecked()) customizations.add(getString(R.string.tomato));

                        // Insert into DB on background thread (assume userId = 1)
                        try {
                            AppDatabase db = AppDatabase.getInstance(requireContext());
                            CartItem cartItem = new CartItem(menuItemId, quantity, 1, customizations);
                            new Thread(() -> {
                                try { db.cartDAO().insertCartItem(cartItem); } catch (Exception ignored) {}
                            }).start();
                        } catch (Exception ignored) {}

                        // Publish result so caller can update UI
                        getParentFragmentManager().setFragmentResult("sandwich_customized", result);

                        Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show();

                        // navigate to cartFragment
                        try {
                            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container).navigate(R.id.cartFragment);
                        } catch (Exception ex) {
                            NavigationUtils.navigateTo(SandwichCustomizationFragment.this, view, R.id.cartFragment, null);
                        }
                    })
                    // Negative: add and go back to menu (navigateUp)
                    .setNegativeButton(R.string.back_to_menu, (dialog, which) -> {
                        Bundle result = new Bundle();
                        result.putInt("menuItemId", menuItemId);
                        result.putBoolean("onion", cbOnion != null && cbOnion.isChecked());
                        result.putBoolean("caramelized_onion", cbCaramelized != null && cbCaramelized.isChecked());
                        result.putBoolean("harissa", cbHarissa != null && cbHarissa.isChecked());
                        result.putBoolean("tomato", cbTomato != null && cbTomato.isChecked());
                        result.putInt("quantity", quantity);

                        // Prepare customizations list
                        List<String> customizations = new ArrayList<>();
                        if (cbOnion != null && cbOnion.isChecked()) customizations.add(getString(R.string.onion));
                        if (cbCaramelized != null && cbCaramelized.isChecked()) customizations.add(getString(R.string.caramelized_onion));
                        if (cbHarissa != null && cbHarissa.isChecked()) customizations.add(getString(R.string.harissa));
                        if (cbTomato != null && cbTomato.isChecked()) customizations.add(getString(R.string.tomato));

                        // Insert into DB on background thread (assume userId = 1)
                        try {
                            AppDatabase db = AppDatabase.getInstance(requireContext());
                            CartItem cartItem = new CartItem(menuItemId, quantity, 1, customizations);
                            new Thread(() -> {
                                try { db.cartDAO().insertCartItem(cartItem); } catch (Exception ignored) {}
                            }).start();
                        } catch (Exception ignored) {}

                        getParentFragmentManager().setFragmentResult("sandwich_customized", result);
                        Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show();

                        try {
                            // navigate up to return to menuItems
                            Navigation.findNavController(view).navigateUp();
                        } catch (Exception ex) {
                            // fallback: pop back stack
                            try { getParentFragmentManager().popBackStack(); } catch (Exception ignored) {}
                        }
                    });

            builder.create().show();
        });

        cancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }
}
