package com.example.appit;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Spinner categorySpinner, brandSpinner;
    private GridAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> fullProductList = new ArrayList<>();

    private String currentCategory = "All";
    private String currentBrand = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        setupToolbar();
        setupRecyclerView();
        setupSpinners();

        loadProducts();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.filter_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.filtered_product_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GridAdapter(this, productList);
        recyclerView.setAdapter(adapter);
    }

    private void setupSpinners() {
        categorySpinner = findViewById(R.id.spinner_category);
        brandSpinner = findViewById(R.id.spinner_brand);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == R.id.spinner_category) {
                    currentCategory = parent.getItemAtPosition(position).toString();
                } else if (parent.getId() == R.id.spinner_brand) {
                    currentBrand = parent.getItemAtPosition(position).toString();
                }
                filterProducts();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        categorySpinner.setOnItemSelectedListener(listener);
        brandSpinner.setOnItemSelectedListener(listener);
    }

    private void loadProducts() {
        FirebaseFirestore.getInstance().collection("products").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fullProductList.clear();
                        Set<String> categories = new HashSet<>();
                        Set<String> brands = new HashSet<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setDocumentId(document.getId()); // SỬA LỖI Ở ĐÂY
                            fullProductList.add(product);
                            if (product.getCategory() != null) categories.add(product.getCategory());
                            if (product.getBrand() != null) brands.add(product.getBrand());
                        }

                        updateSpinners(categories, brands);
                        filterProducts();
                    } else {
                        Toast.makeText(this, "Error loading products.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateSpinners(Set<String> categories, Set<String> brands) {
        List<String> categoryList = new ArrayList<>(categories);
        categoryList.add(0, "All");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        List<String> brandList = new ArrayList<>(brands);
        brandList.add(0, "All");
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brandList);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandSpinner.setAdapter(brandAdapter);
    }

    private void filterProducts() {
        productList.clear();
        for (Product product : fullProductList) {
            boolean categoryMatches = currentCategory.equals("All") || currentCategory.equals(product.getCategory());
            boolean brandMatches = currentBrand.equals("All") || currentBrand.equals(product.getBrand());

            if (categoryMatches && brandMatches) {
                productList.add(product);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
