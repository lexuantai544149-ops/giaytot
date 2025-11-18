package com.example.appit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ImageView profileImage;
    private EditText profileNameEdit, profilePhoneEdit;
    private TextView profileEmail;
    private RecyclerView addressRecyclerView;
    private AddressAdapter addressAdapter;

    private Uri imageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    profileImage.setImageURI(imageUri);
                    uploadImageToFirebase();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        setupToolbar();
        setupViews();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadUserProfile(currentUser.getUid());
        } else {
            goToLogin();
        }

        findViewById(R.id.btn_edit_profile_image).setOnClickListener(v -> openImageChooser());
        findViewById(R.id.btn_edit_name).setOnClickListener(v -> enableEdit(profileNameEdit));
        findViewById(R.id.btn_edit_phone).setOnClickListener(v -> enableEdit(profilePhoneEdit));
        findViewById(R.id.btn_save_profile).setOnClickListener(v -> saveProfileChanges());
        findViewById(R.id.btn_logout).setOnClickListener(v -> logoutUser());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViews() {
        profileImage = findViewById(R.id.profile_image);
        profileNameEdit = findViewById(R.id.profile_name_edit);
        profileEmail = findViewById(R.id.profile_email);
        profilePhoneEdit = findViewById(R.id.profile_phone_edit);
        addressRecyclerView = findViewById(R.id.address_recycler_view);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void enableEdit(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(intent);
    }

    private void loadUserProfile(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            populateUI(user);
                        }
                    }
                });
    }

    private void populateUI(User user) {
        profileNameEdit.setText(user.getDisplayName());
        profileNameEdit.setEnabled(false);
        profileEmail.setText(user.getEmail());
        profilePhoneEdit.setText(user.getPhone());
        profilePhoneEdit.setEnabled(false);

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(this).load(user.getProfileImageUrl()).into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.ic_default_profile);
        }

        if (user.getShippingAddresses() != null) {
            addressAdapter = new AddressAdapter(this, user.getShippingAddresses());
            addressRecyclerView.setAdapter(addressAdapter);
        }
    }

    private void saveProfileChanges() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String newName = profileNameEdit.getText().toString().trim();
        String newPhone = profilePhoneEdit.getText().toString().trim();

        if (newName.isEmpty()) {
            profileNameEdit.setError("Tên không được để trống");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("displayName", newName);
        updates.put("phone", newPhone);

        db.collection("users").document(currentUser.getUid()).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    profileNameEdit.setEnabled(false);
                    profilePhoneEdit.setEnabled(false);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            Toast.makeText(this, "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();
            StorageReference storageRef = storage.getReference().child("profile_images/" + UUID.randomUUID().toString());
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(this::updateUserProfileImage))
                    .addOnFailureListener(e -> Toast.makeText(this, "Tải ảnh thất bại", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateUserProfileImage(Uri downloadUri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .update("profileImageUrl", downloadUri.toString())
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show());
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        CartManager.getInstance().clearCartOnLogout();
        goToLogin();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
