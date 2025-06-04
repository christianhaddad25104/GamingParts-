package com.example.gamingparts;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mydata-86e0f-default-rtdb.firebaseio.com/");

     Context context;
     ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = products.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }

        TextView tvProductInfo = convertView.findViewById(R.id.tvProductInfo);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        tvProductInfo.setText(product.name + " | Price: " + product.price + " | Qty: " + product.quantity);

        btnEdit.setOnClickListener(v -> {
            showEditDialog(product);
        });

        btnDelete.setOnClickListener(v -> {
            databaseReference.child(product.productId).removeValue();
            Toast.makeText(context, "Product Deleted", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    private void showEditDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Product");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);

        EditText etPrice = new EditText(context);
        etPrice.setHint("Price");
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etPrice.setText(String.valueOf(product.price));
        layout.addView(etPrice);

        EditText etQuantity = new EditText(context);
        etQuantity.setHint("Quantity");
        etQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        etQuantity.setText(String.valueOf(product.quantity));
        layout.addView(etQuantity);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            product.price = Double.parseDouble(etPrice.getText().toString());
            product.quantity = Integer.parseInt(etQuantity.getText().toString());
            databaseReference.child(product.productId).setValue(product);
            Toast.makeText(context, "Product Updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
