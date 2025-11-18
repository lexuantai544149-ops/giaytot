package com.example.appit;

import java.util.List;

public class User {
    private String uid;
    private String email;
    private String displayName;
    private String phone;
    private String profileImageUrl; // THÊM TRƯỜNG CÒN THIẾU
    private List<Address> shippingAddresses;
    private List<Long> cart;
    private List<String> favorites; // THÊM TRƯỜNG CÒN THIẾU

    public User() {}

    // Getters
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public String getPhone() { return phone; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public List<Address> getShippingAddresses() { return shippingAddresses; }
    public List<Long> getCart() { return cart; }
    public List<String> getFavorites() { return favorites; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setShippingAddresses(List<Address> shippingAddresses) { this.shippingAddresses = shippingAddresses; }
    public void setCart(List<Long> cart) { this.cart = cart; }
    public void setFavorites(List<String> favorites) { this.favorites = favorites; }
}
