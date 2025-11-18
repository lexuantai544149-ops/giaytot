package com.example.appit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Product> productList;
    private List<Product> fullProductList;
    private GridAdapter adapter;
    private SearchView searchView;
    private TextView cartBadgeTextView;

    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupToolbar();
        setupDrawer();
        updateNavHeader();
        setupRecyclerView();

        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        updateNavHeader(); // Cập nhật lại header khi quay lại
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Appit Store");
    }

    private void setupDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.product_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        fullProductList = new ArrayList<>();
        adapter = new GridAdapter(this, productList);
        recyclerView.setAdapter(adapter);
    }

    private void loadProducts() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fullProductList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setDocumentId(document.getId());
                            fullProductList.add(product);
                        }
                        filterProducts();
                    } else {
                        Toast.makeText(MainActivity.this, "Error loading products.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterProducts() {
        productList.clear();
        if (currentSearchQuery.isEmpty()) {
            productList.addAll(fullProductList);
        } else {
            String lowerCaseQuery = currentSearchQuery.toLowerCase();
            for (Product product : fullProductList) {
                boolean titleMatches = product.getTitle().toLowerCase().contains(lowerCaseQuery);
                boolean tagsMatch = product.getTags() != null && product.getTags().toString().toLowerCase().contains(lowerCaseQuery);
                if (titleMatches || tagsMatch) {
                    productList.add(product);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        
        MenuItem cartItem = menu.findItem(R.id.action_cart);
        View actionView = cartItem.getActionView();
        if (actionView != null) {
            cartBadgeTextView = actionView.findViewById(R.id.cart_badge);
            actionView.setOnClickListener(v -> onOptionsItemSelected(cartItem));
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                filterProducts();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText;
                filterProducts();
                return true;
            }
        });

        return true;
    }

    private void updateCartBadge() {
        if (cartBadgeTextView == null) return;
        int cartItemCount = CartManager.getInstance().getCartItems().size();
        if (cartItemCount > 0) {
            cartBadgeTextView.setVisibility(View.VISIBLE);
            cartBadgeTextView.setText(String.valueOf(cartItemCount));
        } else {
            cartBadgeTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = headerView.findViewById(R.id.nav_user_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_user_email);
        ImageView navProfileImage = headerView.findViewById(R.id.nav_profile_image);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navUserEmail.setText(currentUser.getEmail());
            db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(doc -> {
                if(doc.exists()) {
                    User user = doc.toObject(User.class);
                    if (user != null) { // SỬA LỖI Ở ĐÂY
                        navUserName.setText(user.getDisplayName());
                        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                            Glide.with(this).load(user.getProfileImageUrl()).into(navProfileImage);
                        } else {
                            navProfileImage.setImageResource(R.drawable.ic_default_profile);
                        }
                    }
                }
            });
        } else {
            navUserName.setText("Appit Store");
            navUserEmail.setText("");
            navProfileImage.setImageResource(R.drawable.ic_default_profile);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (searchView != null && !searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
        } else if (id == R.id.nav_filter) {
            startActivity(new Intent(this, FilterActivity.class));
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        mAuth.signOut();
        CartManager.getInstance().clearCartOnLogout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
