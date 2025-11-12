package com.example.giaytot;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView textName, textPrice, textDescription;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String shoeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        textName = findViewById(R.id.productName);
        textPrice = findViewById(R.id.productPrice);
        textDescription = findViewById(R.id.productDescription);
        progressBar = findViewById(R.id.productProgressBar);

        db = FirebaseFirestore.getInstance();

        // Nhận ID (tên) của sản phẩm từ MainActivity
        shoeId = getIntent().getStringExtra("SHOE_ID");

        if (shoeId != null && !shoeId.isEmpty()) {
            loadProductDetails();
        } else {
            Toast.makeText(this, "Error: No product ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProductDetails() {
        progressBar.setVisibility(View.VISIBLE);

        // Tạo tham chiếu đến document trong collection "shoes"
        DocumentReference docRef = db.collection("shoes").document(shoeId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Lấy dữ liệu từ Firestore và hiển thị
                        String name = document.getString("name");
                        String price = document.getString("price");
                        String description = document.getString("description");

                        textName.setText(name);
                        textPrice.setText(price);
                        textDescription.setText(description);
                    } else {
                        Toast.makeText(ProductDetailActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Get failed with " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}