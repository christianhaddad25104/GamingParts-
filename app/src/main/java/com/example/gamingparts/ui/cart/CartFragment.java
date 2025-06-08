package com.example.gamingparts.ui.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gamingparts.Product;
import com.example.gamingparts.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private ListView cartListView;
    private TextView totalAmount;
    private Button buyButton;
    private List<Product> cartItems;
    private CartAdapter adapter;

    public CartFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartListView = view.findViewById(R.id.cartListView);
        totalAmount = view.findViewById(R.id.totalAmount);
        buyButton = view.findViewById(R.id.buyButton);

        cartItems = new ArrayList<>();
        adapter = new CartAdapter(requireContext(), cartItems);
        cartListView.setAdapter(adapter);

        loadCartItems();

        buyButton.setOnClickListener(v -> validateCartBeforeBuy());

        return view;
    }

    private void loadCartItems() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Carts").child(username);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                double total = 0;

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    Product product = itemSnap.getValue(Product.class);
                    if (product != null) {
                        cartItems.add(product);
                        total += product.price * product.quantity;
                    }
                }

                adapter.notifyDataSetChanged();
                totalAmount.setText("Total: $" + total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateCartBeforeBuy() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Products");
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(username);
        List<String> failedItems = new ArrayList<>();
        final int[] counter = {0};

        for (Product cartProduct : cartItems) {
            String productId = cartProduct.productId;

            productsRef.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    counter[0]++;
                    Integer stock = snapshot.child("quantity").getValue(Integer.class);

                    if (stock == null || cartProduct.quantity > stock) {
                        failedItems.add(cartProduct.name + " (available: " + (stock != null ? stock : 0) + ")");
                    }

                    if (counter[0] == cartItems.size()) {
                        if (failedItems.isEmpty()) {
                            for (Product p : cartItems) {
                                int newStock = Math.max(0, snapshot.child("quantity").getValue(Integer.class) - p.quantity);
                                productsRef.child(p.productId).child("quantity").setValue(newStock);
                            }
                            cartRef.removeValue();
                            cartItems.clear();
                            adapter.notifyDataSetChanged();
                            totalAmount.setText("Total: $0.00");
                            Toast.makeText(getContext(), "Purchase completed ✅", Toast.LENGTH_LONG).show();
                        } else {
                            StringBuilder message = new StringBuilder("Insufficient stock:\n");
                            for (String item : failedItems) {
                                message.append("- ").append(item).append("\n");
                            }
                            Toast.makeText(getContext(), message.toString().trim(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    counter[0]++;
                    if (counter[0] == cartItems.size()) {
                        Toast.makeText(getContext(), "Check failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
