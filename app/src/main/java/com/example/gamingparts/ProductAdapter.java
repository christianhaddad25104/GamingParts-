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
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");

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


        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvPrice = convertView.findViewById(R.id.tvPrice);
        TextView tvQuantity = convertView.findViewById(R.id.tvQuantity);
        TextView tvCompany = convertView.findViewById(R.id.tvCompany);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        tvName.setText(product.name);
        tvPrice.setText("Price: $" + product.price);
        tvQuantity.setText("Quantity: " + product.quantity);
        tvCompany.setText("Company: " + product.company);
        tvDescription.setText("Description: " + product.description);

        btnEdit.setOnClickListener(v -> {
            showEditDialog(product);
        });

        btnDelete.setOnClickListener(v -> {
            databaseReference.child(product.productId).removeValue();
            Toast.makeText(context, "com.example.gamingparts.Product Deleted", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    private void showEditDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit com.example.gamingparts.Product");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 20, 30, 20);

        EditText etName = new EditText(context);
        etName.setHint("com.example.gamingparts.Product Name");
        etName.setText(product.name);
        layout.addView(etName);

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

        EditText etCompany = new EditText(context);
        etCompany.setHint("Company");
        etCompany.setText(product.company);
        layout.addView(etCompany);

        EditText etDescription = new EditText(context);
        etDescription.setHint("Description");
        etDescription.setText(product.description);
        layout.addView(etDescription);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            product.name = etName.getText().toString();
            product.price = Double.parseDouble(etPrice.getText().toString());
            product.quantity = Integer.parseInt(etQuantity.getText().toString());
            product.company = etCompany.getText().toString();
            product.description = etDescription.getText().toString();

            databaseReference.child(product.productId).setValue(product);
            Toast.makeText(context, "com.example.gamingparts.Product Updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
