package com.example.appit;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;
    private final List<Product> cartItems = new ArrayList<>();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Product product) {
        cartItems.add(product);
    }

    public void removeProduct(Product product) {
        cartItems.remove(product);
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (Product product : cartItems) {
            try {
                // SỬA LỖI: Loại bỏ tất cả các ký tự không phải là số
                String priceString = product.getPrice().replaceAll("\\D", "");
                total += Double.parseDouble(priceString);
            } catch (NumberFormatException e) {
                // Bỏ qua sản phẩm nếu giá không hợp lệ
                e.printStackTrace();
            }
        }
        return total;
    }
}
