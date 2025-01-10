package com.nujat.yumorder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nujat.yumorder.R;
import com.nujat.yumorder.add_admin.AddAdmin;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final List<Item> itemList;
    private final Context context;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
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

        holder.itemTitle.setText(item.getName() + " ৳" + item.getPrice());
        // Placeholder for image, set an appropriate source if needed
        holder.itemImage.setImageResource(R.drawable.ic_placeholder);

        holder.updateButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddAdmin.class);
            intent.putExtra("itemId", item.getId());
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("items").document(item.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        itemList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, itemList.size());
                    });
        });
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
