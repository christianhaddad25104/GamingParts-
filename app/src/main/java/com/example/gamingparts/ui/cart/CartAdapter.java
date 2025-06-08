package com.example.gamingparts.ui.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gamingparts.Product;
import com.example.gamingparts.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CartAdapter extends ArrayAdapter<Product> {
    private final Context context;
    private final List<Product> products;

    public CartAdapter(Context context, List<Product> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        Product product = products.get(position);

        TextView name = convertView.findViewById(R.id.cartProductName);
        EditText quantityInput = convertView.findViewById(R.id.cartQuantityInput);
        TextView totalPrice = convertView.findViewById(R.id.cartTotalPrice);
        Button updateBtn = convertView.findViewById(R.id.updateCartBtn);
        Button deleteBtn = convertView.findViewById(R.id.deleteCartBtn);

        name.setText(product.name);
        quantityInput.setText(String.valueOf(product.quantity));
        totalPrice.setText("Total: $" + (product.price * product.quantity));

        SharedPreferences prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);
        if (username == null) {
            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
            return convertView;
        }

        if (product.productId == null) {
            Toast.makeText(context, "Missing product ID", Toast.LENGTH_SHORT).show();
            return convertView;
        }

        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("Carts").child(username).child(product.productId);

        updateBtn.setOnClickListener(v -> {
            String newQtyStr = quantityInput.getText().toString().trim();
            if (TextUtils.isEmpty(newQtyStr)) {
                Toast.makeText(context, "Enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int newQty = Integer.parseInt(newQtyStr);
            if (newQty <= 0) {
                Toast.makeText(context, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            product.quantity = newQty;
            cartRef.setValue(product)
                    .addOnSuccessListener(aVoid -> {
                        notifyDataSetChanged();
                        Toast.makeText(context, "Cart updated", Toast.LENGTH_SHORT).show();
                    });
        });

        deleteBtn.setOnClickListener(v -> {
            cartRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        if (position >= 0 && position < products.size()) {
                            products.remove(position);
                            notifyDataSetChanged();
                        }
                        Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                    });
        });


        return convertView;
    }
}
