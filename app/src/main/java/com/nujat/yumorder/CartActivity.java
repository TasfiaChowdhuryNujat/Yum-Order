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
            // Clear cart and show a detailed dialog for checkout summary
            clearCart();
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
     * Clears all items from the cart in Firestore and shows a dialog with the checkout summary.
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

                    StringBuilder memoMessage = new StringBuilder();

                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        CartItem item = doc.toObject(CartItem.class);

                        double itemTotal = item.calculateTotalPrice();
                        totalAmount += itemTotal;

                        memoMessage.append(item.getItemName())
                                .append(" - Quantity: ").append(item.getCount())
                                .append(" - Amount: $").append(String.format("%.2f", itemTotal))
                                .append("\n");

                        double finalTotalAmount = totalAmount;
                        db.collection("users")
                                .document(auth.getCurrentUser().getUid())
                                .collection("cart")
                                .document(doc.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    deletedCount.getAndIncrement();
                                    if (deletedCount.get() == totalItems) {
                                        showCheckoutDialog(memoMessage.toString(), finalTotalAmount);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("CartActivity", "Error deleting document: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load cart for clearing: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Displays the checkout details in a dialog.
     */
    private void showCheckoutDialog(String memoMessage, double totalAmount) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_checkout_summary, null);

        TextView detailsTextView = dialogView.findViewById(R.id.tv_checkout_details);
        TextView totalAmountTextView = dialogView.findViewById(R.id.tv_total_amount_dialog);
        Button closeButton = dialogView.findViewById(R.id.btn_close_dialog);

        detailsTextView.setText(memoMessage);
        totalAmountTextView.setText(String.format("Total Amount: $%.2f", totalAmount));

        builder.setView(dialogView);
        android.app.AlertDialog dialog = builder.create();

        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "Order confirmed! Thank you for shopping.", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}
