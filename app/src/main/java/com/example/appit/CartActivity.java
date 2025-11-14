package com.example.appit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalPriceTextView;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartManager = CartManager.getInstance();

        // --- Setup Toolbar ---
        Toolbar toolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Giỏ hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // --- Setup Views ---
        recyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceTextView = findViewById(R.id.cart_total_price);
        Button checkoutButton = findViewById(R.id.btn_checkout);

        // --- Setup RecyclerView ---
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, cartManager.getCartItems());
        recyclerView.setAdapter(adapter);

        // --- Initial Price Update ---
        updateTotalPrice();

        // --- Checkout Button ---
        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    public void updateTotalPrice() {
        double totalPrice = cartManager.calculateTotalPrice();
        // Format price to be more readable
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        totalPriceTextView.setText(format.format(totalPrice));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Go back to previous activity
        return true;
    }
}
