package tn.esprit.project.ui.fragments;

import android.annotation.SuppressLint;
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

import tn.esprit.project.R;

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
            Bundle result = new Bundle();
            result.putInt("menuItemId", menuItemId);
            result.putBoolean("onion", cbOnion != null && cbOnion.isChecked());
            result.putBoolean("caramelized_onion", cbCaramelized != null && cbCaramelized.isChecked());
            result.putBoolean("harissa", cbHarissa != null && cbHarissa.isChecked());
            result.putBoolean("tomato", cbTomato != null && cbTomato.isChecked());
            result.putInt("quantity", quantity);

            // Publish result so caller (menus/menu items fragment) can observe and add to cart
            getParentFragmentManager().setFragmentResult("sandwich_customized", result);
            CartFragment cartFragment = new CartFragment();
            Bundle b = new Bundle();
            b.putInt("menuItemId", menuItemId);
            cartFragment.setArguments(b);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_container, cartFragment)
                    .addToBackStack(null)
                    .commit();
            Toast.makeText(requireContext(), R.string.added_to_cart, Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        });

        cancel.setOnClickListener(v -> getParentFragmentManager().popBackStack());
    }
}
