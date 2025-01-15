package com.nujat.yumorder;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItems;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView totalAmountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_view_cart);
        Button checkoutButton = findViewById(R.id.btn_checkout);
        totalAmountTextView = findViewById(R.id.tv_total_amount);

        cartItems = new ArrayList<>();
        adapter = new CartAdapter(this, cartItems);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadCartItems();

        checkoutButton.setOnClickListener(v -> {
            // Show a simple memo in a Toast
            Toast.makeText(this, "Checkout successful! Your cart is cleared.", Toast.LENGTH_SHORT).show();

            // Clear items from the cart in Firestore
            clearCart();

            // Optionally, clear items from the UI as well
            cartItems.clear();
            adapter.notifyDataSetChanged();

            // Optionally, update total amount to 0
            totalAmountTextView.setText("Total: $0.00");
        });
    }

    private void loadCartItems() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to view your cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        CartItem item = doc.toObject(CartItem.class);
                        item.setDocumentId(doc.getId());

                        // Debug log
                        Log.d("CartItem", "Item: " + item.getItemName() + ", Price: " + item.getItemPrice() + ", Count: " + item.getCount());

                        cartItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    updateTotalAmount();  // Ensure total is updated after loading items
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load cart: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private double calculateTotalAmount() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.calculateTotalPrice();  // Use calculateTotalPrice to get the total for each item
        }
        return total;
    }

    private void updateTotalAmount() {
        double totalAmount = calculateTotalAmount();
        totalAmountTextView.setText(String.format("Total: $%.2f", totalAmount));
    }

    /**
     * Clears all items from the cart in Firestore.
     */
    private void clearCart() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please log in to clear your cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalItems = queryDocumentSnapshots.size();
                    AtomicInteger deletedCount = new AtomicInteger();
                    double totalAmount = 0.0;

                    // StringBuilder to hold the memo details for the Toast message
                    StringBuilder memoMessage = new StringBuilder("Checkout Details:\n");

                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        CartItem item = doc.toObject(CartItem.class);

                        // Calculate total amount for this item (quantity * price)
                        double itemTotal = item.calculateTotalPrice();
                        totalAmount += itemTotal;

                        // Add item details to the memo message
                        memoMessage.append(item.getItemName())
                                .append(" - Quantity: ").append(item.getCount())
                                .append(" - Amount: $").append(String.format("%.2f", itemTotal))
                                .append("\n");

                        // Delete the item from the Firestore cart
                        double finalTotalAmount = totalAmount;
                        db.collection("users")
                                .document(auth.getCurrentUser().getUid())
                                .collection("cart")
                                .document(doc.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    deletedCount.getAndIncrement();
                                    if (deletedCount.get() == totalItems) {
                                        // After all items are deleted, add the total amount to the memo
                                        memoMessage.append("\nTotal Amount: $").append(String.format("%.2f", finalTotalAmount));

                                        // Show the Toast with the detailed checkout summary
                                        Toast.makeText(this, memoMessage.toString(), Toast.LENGTH_LONG).show();

                                        // Clear local cart items and update UI
                                        cartItems.clear();
                                        adapter.notifyDataSetChanged();
                                        totalAmountTextView.setText("Total: $0.00");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Handle error
                                    Log.e("CartActivity", "Error deleting document: " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load cart for clearing: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
