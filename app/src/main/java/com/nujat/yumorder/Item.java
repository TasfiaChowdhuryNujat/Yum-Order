package com.nujat.yumorder;

public class Item {
    private String name;
    private String price;
    private String details;
    private String id;

    // No-argument constructor required for Firebase
    public Item() {}

    // Constructor with parameters
    public Item(String name, String details,String price) {
        this.name = name;
        this.price = price;
        this.details = details;
    }

    public Item(String name, String price) {
        this.name = name;
        this.price = price;
    }
    public String getId() {
        return id;
    }
    // Getters and Setters (required by Firebase)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}