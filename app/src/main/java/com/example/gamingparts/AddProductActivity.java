package com.example.gamingparts;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReferenceFromUrl("https://mydata-86e0f-default-rtdb.firebaseio.com/");
    EditText etName, etPrice, etQuantity, etCompany, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etCompany = findViewById(R.id.etCompany);
        etDescription = findViewById(R.id.etDescription);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);

        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        btnSave.setOnClickListener(v -> saveProduct());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveProduct() {
        String id = databaseReference.push().getKey();
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || company.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(quantityStr);

        Product p = new Product(id, name, price, quantity, company, description);
        databaseReference.child(id).setValue(p);

        Toast.makeText(this, "com.example.gamingparts.Product Saved Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
