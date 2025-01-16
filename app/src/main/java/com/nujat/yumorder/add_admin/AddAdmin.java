package com.nujat.yumorder.add_admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nujat.yumorder.R;
import com.nujat.yumorder.Item;

import java.util.HashMap;
import java.util.Map;

public class AddAdmin extends AppCompatActivity {
    private EditText nameField, priceField, detailsField;
    private Button addItemButton, uploadImageButton;
    private ImageView imageView;
    private FirebaseFirestore db;
    private String itemId;
    private Uri selectedImageUri;
    private String uploadedImageUrl;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Show preview
                    Glide.with(this)
                            .load(selectedImageUri)
                            .centerCrop()
                            .into(imageView);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_admin);

        // Initialize UI components
        nameField = findViewById(R.id.name_Field);
        priceField = findViewById(R.id.price_Field);
        detailsField = findViewById(R.id.details_Field);
        addItemButton = findViewById(R.id.add_ItemButton);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        imageView = findViewById(R.id.imageView);
        db = FirebaseFirestore.getInstance();

        // Check if we are updating an item
        itemId = getIntent().getStringExtra("itemId");

        if (itemId != null) {
            loadItemData(itemId);
            addItemButton.setText("Update Item");
        }

        uploadImageButton.setOnClickListener(v -> openImagePicker());

        addItemButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImageToCloudinary();
            } else if (uploadedImageUrl != null) {
                // If we already have an uploaded image URL (from loading existing data)
                if (itemId != null) {
                    updateItem();
                } else {
                    addItem();
                }
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImage.launch(intent);
    }

    private void uploadImageToCloudinary() {
        if (selectedImageUri == null) return;

        String timestamp = String.valueOf(System.currentTimeMillis());
        String publicId = "item_" + timestamp;

        MediaManager.get().upload(selectedImageUri)
                .option("public_id", publicId)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(AddAdmin.this, "Upload started...", Toast.LENGTH_SHORT).show();
                        addItemButton.setEnabled(false); // Disable button during upload
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        double progress = (bytes / (double) totalBytes) * 100;
                        // You could update a progress bar here if you add one
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        uploadedImageUrl = (String) resultData.get("secure_url");
                        Toast.makeText(AddAdmin.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                        addItemButton.setEnabled(true);
                        if (itemId != null) {
                            updateItem();
                        } else {
                            addItem();
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(AddAdmin.this, "Upload failed: " + error.getDescription(), Toast.LENGTH_LONG).show();
                        addItemButton.setEnabled(true);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Toast.makeText(AddAdmin.this, "Upload rescheduled", Toast.LENGTH_SHORT).show();
                    }
                })
                .dispatch();
    }

    private void loadItemData(String itemId) {
        db.collection("items").document(itemId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameField.setText(documentSnapshot.getString("name"));
                        priceField.setText(documentSnapshot.getString("price"));
                        detailsField.setText(documentSnapshot.getString("details"));
                        uploadedImageUrl = documentSnapshot.getString("imageUrl");
                        if (uploadedImageUrl != null) {
                            Glide.with(this)
                                    .load(uploadedImageUrl)
                                    .centerCrop()
                                    .into(imageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading item: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
    }

    private void addItem() {
        if (!validateFields()) return;

        Item item = new Item();
        item.setName(nameField.getText().toString().trim());
        item.setPrice(priceField.getText().toString().trim());
        item.setDetails(detailsField.getText().toString().trim());

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("name", item.getName());
        itemMap.put("price", item.getPrice());
        itemMap.put("details", item.getDetails());
        itemMap.put("imageUrl", uploadedImageUrl);

        db.collection("items")
                .add(itemMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddAdmin.this, "Item Added Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAdmin.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateItem() {
        if (!validateFields()) return;

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("name", nameField.getText().toString().trim());
        itemMap.put("price", priceField.getText().toString().trim());
        itemMap.put("details", detailsField.getText().toString().trim());
        itemMap.put("imageUrl", uploadedImageUrl);

        db.collection("items").document(itemId)
                .set(itemMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddAdmin.this, "Item Updated Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAdmin.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

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