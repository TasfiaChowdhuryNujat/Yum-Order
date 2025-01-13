package com.nujat.yumorder.item_details_page;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nujat.yumorder.R;

public class ItemDetailsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details_page);

        // Get references to UI components
        ImageView itemImage = findViewById(R.id.item_detail_image);
        TextView itemName = findViewById(R.id.item_detail_name);
        TextView itemDetails = findViewById(R.id.item_details);
        TextView itemPrice = findViewById(R.id.item_detail_price);

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
    }
}
