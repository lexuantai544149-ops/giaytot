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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setupToolbar();
        setupViews();
        loadCart();

        Button checkoutButton = findViewById(R.id.btn_checkout);
        checkoutButton.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Giỏ hàng");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceTextView = findViewById(R.id.cart_total_price);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadCart() {
        CartManager.getInstance().loadCartFromFirebase(new CartManager.CartListener() {
            @Override
            public void onCartUpdated() {
                if(adapter == null) {
                    adapter = new CartAdapter(CartActivity.this, CartManager.getInstance().getCartItems());
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                updateTotalPrice();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CartActivity.this, "Lỗi tải giỏ hàng: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateTotalPrice() {
        double totalPrice = CartManager.getInstance().calculateTotalPrice();
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        totalPriceTextView.setText(format.format(totalPrice));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
