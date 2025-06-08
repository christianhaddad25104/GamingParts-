package com.example.gamingparts.ui.Store;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoreAdapter extends ArrayAdapter<Product> {
    private final Context context;
    private final List<Product> products;

    public StoreAdapter(Context context, List<Product> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.product_store, parent, false);
        }

        Product product = products.get(position);

        TextView name = convertView.findViewById(R.id.productName);
        TextView company = convertView.findViewById(R.id.productCompany);
        TextView desc = convertView.findViewById(R.id.productDescription);
        TextView price = convertView.findViewById(R.id.productPrice);
        EditText quantityInput = convertView.findViewById(R.id.quantityInput);
        Button addToCart = convertView.findViewById(R.id.AddToCartButton);

        name.setText(product.name);
        company.setText(product.company);
        desc.setText(product.description);
        price.setText("Price: $" + product.price);

        addToCart.setOnClickListener(v -> {
            String quantityText = quantityInput.getText().toString().trim();

            if (TextUtils.isEmpty(quantityText)) {
                Toast.makeText(context, "Please enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantityToAdd = Integer.parseInt(quantityText);
            if (quantityToAdd <= 0) {
                Toast.makeText(context, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE);
            String userName = prefs.getString("username", null);
            if (userName == null) {
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            if (product.productId == null) {
                Toast.makeText(context, "Error: productId is null", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference cartRef = FirebaseDatabase.getInstance()
                    .getReference("Carts").child(userName).child(product.productId);

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int existingQuantity = 0;

                    if (snapshot.exists()) {
                        Integer value = snapshot.child("quantity").getValue(Integer.class);
                        if (value != null) existingQuantity = value;
                    }

                    int totalQuantity = existingQuantity + quantityToAdd;

                    if (totalQuantity > product.quantity) {
                        Toast.makeText(context, "Not enough stock available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Product updatedProduct = new Product(
                            product.productId,
                            product.name,
                            product.price,
                            totalQuantity,
                            product.company,
                            product.description
                    );

                    cartRef.setValue(updatedProduct)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return convertView;
    }
}
