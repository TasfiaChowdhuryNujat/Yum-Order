package com.nujat.yumorder.admin_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nujat.yumorder.R;
import com.nujat.yumorder.add_admin.AddAdmin;
import com.nujat.yumorder.login_page.LoginPage;
import com.nujat.yumorder.add_admin.AddAdmin;

public class AdminPage extends AppCompatActivity {
    private LinearLayout itemListLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        itemListLayout = findViewById(R.id.item_ListLayout);
        Button addItemButton = findViewById(R.id.add_ItemButton);
        db = FirebaseFirestore.getInstance();

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPage.this, AddAdmin.class);
            startActivity(intent);
        });

        loadItems();
    }

    private void loadItems() {
        db.collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemListLayout.removeAllViews();
                    int index = 1;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String itemName = document.getString("name");
                        String price = document.getString("price");
                        String itemId = document.getId();

                        TextView itemTextView = new TextView(this);
                        itemTextView.setText(index + ". " + itemName + " - " + price);

                        Button updateButton = new Button(this);
                        updateButton.setText("Update");
                        updateButton.setOnClickListener(v -> {
                            Intent intent = new Intent(AdminPage.this, AddAdmin.class);
                            intent.putExtra("itemId", itemId);
                            startActivity(intent);
                        });

                        Button deleteButton = new Button(this);
                        deleteButton.setText("Delete");
                        deleteButton.setOnClickListener(v -> deleteItem(itemId));

                        LinearLayout rowLayout = new LinearLayout(this);
                        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                        rowLayout.addView(itemTextView);
                        rowLayout.addView(updateButton);
                        rowLayout.addView(deleteButton);

                        itemListLayout.addView(rowLayout);
                        index++;
                    }
                });
    }

    private void deleteItem(String itemId) {
        db.collection("items").document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> loadItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu with the logout option
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item clicks
        if (item.getItemId() == R.id.action_logout) {
            // Log out the user
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();

            // Redirect to LoginPage
            Intent intent = new Intent(AdminPage.this, LoginPage.class);
            startActivity(intent);
            finish(); // Close AdminPage
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}