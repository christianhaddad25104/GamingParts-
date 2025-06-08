package com.example.gamingparts.ui.Store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

public class StoreFragment extends Fragment {

    private ListView productListView;
    private List<Product> productList;
    private StoreAdapter adapter;

    public StoreFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_store, container, false);

        productListView = view.findViewById(R.id.productListView);
        productList = new ArrayList<>();
        adapter = new StoreAdapter(requireContext(), productList);
        productListView.setAdapter(adapter);

        loadProductsFromFirebase();

        return view;
    }

    private void loadProductsFromFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Products");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productSnap : snapshot.getChildren()) {
                    Product product = productSnap.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
