package com.nujat.yumorder;

public class Item {
    private String name;
    private String price;
    private String details;
    private String id;
    private String imageUrl;

    // No-argument constructor required for Firebase
    public Item() {}

    public Item(String name, String price, String details, String imageUrl) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.imageUrl = imageUrl;
    }

    public Item(String name, String details, String price) {
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

    // Add this method to fix the error
    public void setId(String id) {
        this.id = id;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}