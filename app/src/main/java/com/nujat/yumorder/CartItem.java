package com.nujat.yumorder;

import android.util.Log;

public class CartItem {
    private String itemName;
    private String itemPrice;  // This is a string; we will convert it to double for calculations
    private int count;
    private String documentId; // For identifying the Firestore document

    public CartItem() {
        // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
    }

    public CartItem(String itemName, String itemPrice, int count, String documentId) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.count = count;
        this.documentId = documentId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    /**
     * Helper method to get item price as a double for calculations.
     * @return the price as double.
     */
    public double getPriceAsDouble() {
        try {
            return Double.parseDouble(itemPrice);
        } catch (NumberFormatException e) {
            return 0.0;  // Return 0.0 if the price is invalid
        }
    }

    /**
     * Method to calculate total price of the item based on its count and price.
     * @return total price for this item (price * count).
     */
    public double calculateTotalPrice() {
        double price = getPriceAsDouble();
        double totalPrice = price * count;

        // Log for debugging
        Log.d("CartItem", "Price per item: " + price + ", Quantity: " + count + ", Total: " + totalPrice);

        return totalPrice;
    }
}
