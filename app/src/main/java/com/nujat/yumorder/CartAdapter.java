package com.nujat.yumorder;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItem> cartItems;
    private final FirebaseFirestore db;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_row, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.itemName.setText(item.getItemName());
        holder.itemPrice.setText("Price: " + item.getItemPrice());
        holder.itemCount.setText("Quantity: " + item.getCount());

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            db.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("cart")
                    .document(item.getDocumentId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());
                    });
        });

        // Handle update button click
        holder.updateButton.setOnClickListener(v -> {
            // Show a dialog to input new quantity
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Update Quantity");

            // Input field for quantity
            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Enter new quantity");
            builder.setView(input);

            // Set up dialog buttons
            builder.setPositiveButton("Update", (dialog, which) -> {
                String newQuantityStr = input.getText().toString();
                if (!newQuantityStr.isEmpty()) {
                    int newQuantity = Integer.parseInt(newQuantityStr);
                    if (newQuantity > 0) {
                        // Update quantity in Firestore
                        db.collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("cart")
                                .document(item.getDocumentId())
                                .update("count", newQuantity)
                                .addOnSuccessListener(aVoid -> {
                                    // Update local list and notify adapter
                                    item.setCount(newQuantity);
                                    notifyItemChanged(position);
                                });
                    }
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, itemCount;
        Button deleteButton, updateButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.cart_item_name);
            itemPrice = itemView.findViewById(R.id.cart_item_price);
            itemCount = itemView.findViewById(R.id.cart_item_count);
            deleteButton = itemView.findViewById(R.id.btn_delete_item);
            updateButton = itemView.findViewById(R.id.btn_update_item);
        }
    }
}
