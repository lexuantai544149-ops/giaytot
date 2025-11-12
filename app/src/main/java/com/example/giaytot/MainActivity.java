package com.example.giaytot;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// File mới phải implements NavigationView.OnNavigationItemSelectedListener
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    GridView gridView;
    FirebaseAuth mAuth;

    // Dữ liệu tĩnh (như cũ)
    String[] shoeNames = {
            "Nike Air Force 1",
            "Adidas Stan Smith",
            "Puma Suede",
            "Converse Chuck"
    };

    int[] shoeImages = {
            R.drawable.shoe_placeholder,
            R.drawable.shoe_placeholder,
            R.drawable.shoe_placeholder,
            R.drawable.shoe_placeholder
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Dùng layout activity_main mới

        mAuth = FirebaseAuth.getInstance();

        //--- Cài đặt Toolbar ---
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GiayTot Store");

        //--- Cài đặt DrawerLayout (Sidebar) ---
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Nút hamburger để mở/đóng sidebar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //--- Cập nhật Email trên Header của Sidebar ---
        updateNavHeader();

        //--- Cài đặt GridView (nằm trong content_main.xml) ---
        // Chú ý: File này không còn R.id.textEmail nữa
        gridView = findViewById(R.id.gridView); // ID này từ content_main.xml

        GridAdapter adapter = new GridAdapter(MainActivity.this, shoeNames, shoeImages);
        gridView.setAdapter(adapter);

        // Xử lý khi bấm vào một item
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedShoeName = shoeNames[position];
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("SHOE_ID", selectedShoeName);
                startActivity(intent);
            }
        });
    }

    // Cập nhật email trên nav header
    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderEmail = headerView.findViewById(R.id.nav_header_email);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navHeaderEmail.setText(currentUser.getEmail());
        } else {
            navHeaderEmail.setText("Vui lòng đăng nhập");
        }
    }

    // Xử lý khi bấm nút back (nếu sidebar đang mở thì đóng lại)
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Xử lý khi bấm vào các icon trên Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Toast.makeText(this, "Chức năng tìm kiếm", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Xử lý khi bấm vào các mục trong Sidebar
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Đang ở home rồi
        } else if (id == R.id.nav_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START); // Đóng sidebar
        return true;
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}