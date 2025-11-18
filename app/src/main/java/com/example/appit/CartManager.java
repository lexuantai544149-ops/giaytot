package com.example.appit;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    public interface CartListener {
        void onCartUpdated();
        void onError(String message);
    }

    private static CartManager instance;
    private final List<Product> cartItems = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<Product> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void loadCartFromFirebase(CartListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            if (listener != null) listener.onError("User not logged in");
            return;
        }

        db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                List<Long> cartIds = (List<Long>) userDoc.get("cart");
                if (cartIds != null && !cartIds.isEmpty()) {
                    fetchProductDetails(cartIds, listener);
                } else {
                    cartItems.clear();
                    if (listener != null) listener.onCartUpdated();
                }
            }
        }).addOnFailureListener(e -> {
            if (listener != null) listener.onError(e.getMessage());
        });
    }

    private void fetchProductDetails(List<Long> productIds, CartListener listener) {
        cartItems.clear();
        if (productIds.isEmpty()) {
            if (listener != null) listener.onCartUpdated();
            return;
        }
        db.collection("products").whereIn("id", productIds).get().addOnSuccessListener(productDocs -> {
            cartItems.clear();
            for (QueryDocumentSnapshot doc : productDocs) {
                Product p = doc.toObject(Product.class);
                p.setDocumentId(doc.getId()); // SỬA LỖI: Gán Document ID
                cartItems.add(p);
            }
            if (listener != null) listener.onCartUpdated();
        }).addOnFailureListener(e -> {
            if (listener != null) listener.onError(e.getMessage());
        });
    }

    public void addProductToCart(Product product, CartListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            if (listener != null) listener.onError("User not logged in");
            return;
        }
        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.update("cart", FieldValue.arrayUnion(product.getId()))
            .addOnSuccessListener(aVoid -> {
                boolean exists = false;
                for (Product p : cartItems) {
                    if (p.getId() == product.getId()) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    cartItems.add(product);
                }
                if (listener != null) listener.onCartUpdated();
            })
            .addOnFailureListener(e -> {
                if (listener != null) listener.onError(e.getMessage());
            });
    }

    public void removeProductFromCart(Product product, CartListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        DocumentReference userDocRef = db.collection("users").document(currentUser.getUid());
        userDocRef.update("cart", FieldValue.arrayRemove(product.getId()))
            .addOnSuccessListener(aVoid -> {
                cartItems.removeIf(p -> p.getId() == product.getId());
                if (listener != null) listener.onCartUpdated();
            })
            .addOnFailureListener(e -> {
                 if (listener != null) listener.onError(e.getMessage());
            });
    }
    
    public void clearCartOnLogout() {
        cartItems.clear();
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (Product product : cartItems) {
            if (product.isSelected()) {
                try {
                    String priceString = product.getPrice().replaceAll("\\D", "");
                    total += Double.parseDouble(priceString);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return total;
    }
}
