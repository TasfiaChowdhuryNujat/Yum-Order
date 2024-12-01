package com.nujat.yumorder.add_admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nujat.yumorder.R;
import java.util.HashMap;
import java.util.Map;

public class AddAdmin extends AppCompatActivity {
    private EditText nameField, priceField, detailsField;
    private Button addItemButton;
    private FirebaseFirestore db;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_admin);

        // Initialize UI components
        nameField = findViewById(R.id.name_Field);
        priceField = findViewById(R.id.price_Field);
        detailsField = findViewById(R.id.details_Field);
        addItemButton = findViewById(R.id.add_ItemButton);
        db = FirebaseFirestore.getInstance();

        // Check if we are updating an item (via intent)
        itemId = getIntent().getStringExtra("itemId");

        // If itemId is not null, load existing data to update
        if (itemId != null) {
            loadItemData(itemId);
            addItemButton.setText("Update Item");
        }

        // Set OnClickListener for the button
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemId != null) {
                    updateItem();  // Update existing item
                } else {
                    addItem();  // Add new item
                }
            }
        });
    }

    // Load data from Firestore for update
    private void loadItemData(String itemId) {
        db.collection("items").document(itemId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameField.setText(documentSnapshot.getString("name"));
                        priceField.setText(documentSnapshot.getString("price"));
                        detailsField.setText(documentSnapshot.getString("details"));
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    // Add new item to Firestore
    private void addItem() {
        if (!validateFields()) return;

        String name = nameField.getText().toString().trim();
        String price = priceField.getText().toString().trim();
        String details = detailsField.getText().toString().trim();

        // Create a map to hold the data
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", price);
        item.put("details", details);

        // Save data in Firestore under 'items' collection
        db.collection("items")
                .add(item)  // Automatically generates a unique document ID
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddAdmin.this, "Item Added Successfully!", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAdmin.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Update existing item in Firestore
    private void updateItem() {
        if (!validateFields()) return;

        String name = nameField.getText().toString().trim();
        String price = priceField.getText().toString().trim();
        String details = detailsField.getText().toString().trim();

        // Create a map to hold the data
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", price);
        item.put("details", details);

        // Update the document with the given itemId
        db.collection("items").document(itemId)
                .set(item)  // Replaces the entire document with new data
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddAdmin.this, "Item Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();  // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAdmin.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Validate input fields to ensure they are not empty
    private boolean validateFields() {
        boolean isValid = true;
        if (nameField.getText().toString().trim().isEmpty()) {
            nameField.setError("Name is required");
            isValid = false;
        }
        if (priceField.getText().toString().trim().isEmpty()) {
            priceField.setError("Price is required");
            isValid = false;
        }
        if (detailsField.getText().toString().trim().isEmpty()) {
            detailsField.setError("Details are required");
            isValid = false;
        }
        return isValid;
    }
}
