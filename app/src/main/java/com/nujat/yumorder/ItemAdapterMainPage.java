package com.nujat.yumorder;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nujat.yumorder.item_details_page.ItemDetailsPage;

import java.util.List;

public class ItemAdapterMainPage extends RecyclerView.Adapter<ItemAdapterMainPage.ItemViewHolder> {

    private Context context;
    private List<Item> itemList;

    public ItemAdapterMainPage(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.name.setText(item.getName());
       // holder.details.setText(item.getDetails());
        holder.price.setText(item.getPrice());
        holder.image.setImageResource(R.drawable.ic_placeholder); // Dummy image

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailsPage.class);
            intent.putExtra("itemName", item.getName());
            intent.putExtra("itemDetails", item.getDetails());
            intent.putExtra("itemPrice", item.getPrice());
            intent.putExtra("itemImage", R.drawable.ic_placeholder); // Pass actual image resource if available
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name,details, price;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            details = itemView.findViewById(R.id.item_details);
            price = itemView.findViewById(R.id.item_price);
        }
    }
}
