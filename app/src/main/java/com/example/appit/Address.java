package com.example.appit;

public class Address {
    private String recipientName;
    private String phone;
    private String street;
    private String district;
    private String city;
    private boolean isDefault;

    public Address() {}

    // Getters
    public String getRecipientName() { return recipientName; }
    public String getPhone() { return phone; }
    public String getStreet() { return street; }
    public String getDistrict() { return district; }
    public String getCity() { return city; }
    public boolean isDefault() { return isDefault; }

    // Setters
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setStreet(String street) { this.street = street; }
    public void setDistrict(String district) { this.district = district; }
    public void setCity(String city) { this.city = city; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
