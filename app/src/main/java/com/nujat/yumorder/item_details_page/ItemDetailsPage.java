package com.nujat.yumorder.item_details_page;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nujat.yumorder.R;

import java.util.HashMap;
import java.util.Map;

public class ItemDetailsPage extends AppCompatActivity {

    private int itemCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_page);

        // Get references to UI components
        ImageView itemImage = findViewById(R.id.item_detail_image);
        TextView itemName = findViewById(R.id.item_detail_name);
        TextView itemDetails = findViewById(R.id.item_details);
        TextView itemPrice = findViewById(R.id.item_detail_price);
        Button addToCartButton = findViewById(R.id.btn_add_to_cart);
        Button increaseButton = findViewById(R.id.btn_increase_count);
        Button decreaseButton = findViewById(R.id.btn_decrease_count);
        TextView itemCountView = findViewById(R.id.item_count);

        // Retrieve data from Intent
        String name = getIntent().getStringExtra("itemName");
        String details = getIntent().getStringExtra("itemDetails");
        String price = getIntent().getStringExtra("itemPrice");
        int imageRes = getIntent().getIntExtra("itemImage", R.drawable.ic_placeholder);

        // Set data to views
        itemName.setText(name);
        itemDetails.setText(details);
        itemPrice.setText(price);
        itemImage.setImageResource(imageRes);

        // Firebase setup
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Add to cart functionality
        addToCartButton.setOnClickListener(view -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Please log in to add items to your cart.", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = auth.getCurrentUser().getUid();

            // Check if the item already exists in the cart
            db.collection("users")
                    .document(userId)
                    .collection("cart")
                    .whereEqualTo("itemName", name)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            // Item exists: Sum previous count with the current itemCount
                            DocumentReference docRef = querySnapshot.getDocuments().get(0).getReference();
                            int previousCount = querySnapshot.getDocuments().get(0).getLong("count").intValue();
                            int newCount = previousCount + itemCount;

                            docRef.update("count", newCount)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(this, "Item count updated! Total: " + newCount, Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to update count: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            // Item doesn't exist: Add new document
                            Map<String, Object> cartItem = new HashMap<>();
                            cartItem.put("itemName", name);
                            cartItem.put("itemDetails", details);
                            cartItem.put("itemPrice", price);
                            cartItem.put("itemImage", imageRes);
                            cartItem.put("count", itemCount);

                            db.collection("users")
                                    .document(userId)
                                    .collection("cart")
                                    .add(cartItem)
                                    .addOnSuccessListener(documentReference ->
                                            Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to add to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to check cart: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // Increase button functionality
        increaseButton.setOnClickListener(view -> {
            itemCount++;
            itemCountView.setText(String.valueOf(itemCount));
        });

        // Decrease button functionality
        decreaseButton.setOnClickListener(view -> {
            if (itemCount > 1) {
                itemCount--;
                itemCountView.setText(String.valueOf(itemCount));
            } else {
                Toast.makeText(this, "Count cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
