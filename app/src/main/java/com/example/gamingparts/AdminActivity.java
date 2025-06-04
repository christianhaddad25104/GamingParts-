package com.example.gamingparts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

     ListView listView;
     ArrayList<Product> productList;
     ProductAdapter adapter;
     DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listView = findViewById(R.id.listViewProducts);
        Button btnAdd = findViewById(R.id.btnAddProduct);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        loadProducts();

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    private void loadProducts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Product p = data.getValue(Product.class);
                    if (p != null) {
                        productList.add(p);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminActivity.this, "Error loading products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
