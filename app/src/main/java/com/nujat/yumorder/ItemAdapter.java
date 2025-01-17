// ItemAdapter.java
package com.nujat.yumorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nujat.yumorder.add_admin.AddAdmin;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final List<Item> itemList;
    private final Context context;
    private final FirebaseFirestore db;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_page_item_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        // Set item title and price
        holder.itemTitle.setText(item.getName() + " ৳" + item.getPrice());

        // Load image using Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_placeholder);
        }

        // Update button click listener
        holder.updateButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddAdmin.class);
            intent.putExtra("itemId", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("price", item.getPrice());
            intent.putExtra("details", item.getDetails());
            intent.putExtra("imageUrl", item.getImageUrl());
            context.startActivity(intent);
        });

        // Delete button click listener with confirmation
        holder.deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(item, position));
    }

    private void showDeleteConfirmationDialog(Item item, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteItem(item, position))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteItem(Item item, int position) {
        db.collection("items").document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    itemList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, itemList.size());
                    Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error deleting item: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        Button updateButton;
        Button deleteButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            updateButton = itemView.findViewById(R.id.button_update);
            deleteButton = itemView.findViewById(R.id.button_delete);
        }
    }
}