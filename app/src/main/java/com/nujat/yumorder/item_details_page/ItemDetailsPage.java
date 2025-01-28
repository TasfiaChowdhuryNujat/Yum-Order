// ItemDetailsPage.java
package com.nujat.yumorder.item_details_page;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nujat.yumorder.R;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailsPage extends AppCompatActivity {
    private int itemCount = 1;// Start with 1 item

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_page);

        ImageView itemImage = findViewById(R.id.item_detail_image);
        TextView itemName = findViewById(R.id.item_detail_name);
        TextView itemDetails = findViewById(R.id.item_details);
        TextView itemPrice = findViewById(R.id.item_detail_price);
        Button addToCartButton = findViewById(R.id.btn_add_to_cart);
        Button increaseButton = findViewById(R.id.btn_increase_count);
        Button decreaseButton = findViewById(R.id.btn_decrease_count);
        TextView itemCountView = findViewById(R.id.item_count);

        String name = getIntent().getStringExtra("itemName");
        String details = getIntent().getStringExtra("itemDetails");
        String price = getIntent().getStringExtra("itemPrice");
        String imageUrl = getIntent().getStringExtra("itemImageUrl");

        itemName.setText(name);
        itemDetails.setText(details);
        itemPrice.setText(price);

        // Load image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(itemImage);
        } else {
            itemImage.setImageResource(R.drawable.ic_placeholder);
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        addToCartButton.setOnClickListener(view -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Please log in to add items to your cart.", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = auth.getCurrentUser().getUid();

            db.collection("users")
                    .document(userId)
                    .collection("cart")
                    .whereEqualTo("itemName", name)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentReference docRef = querySnapshot.getDocuments().get(0).getReference();
                            int previousCount = querySnapshot.getDocuments().get(0).getLong("count").intValue();
                            int newCount = previousCount + itemCount;

                            docRef.update("count", newCount)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(this, "Item count updated! Total: " + newCount, Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to update count: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        } else {
                            Map<String, Object> cartItem = new HashMap<>();
                            cartItem.put("itemName", name);
                            cartItem.put("itemDetails", details);
                            cartItem.put("itemPrice", price);
                            cartItem.put("itemImageUrl", imageUrl);
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

        increaseButton.setOnClickListener(view -> {
            itemCount++;
            itemCountView.setText(String.valueOf(itemCount));
        });

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