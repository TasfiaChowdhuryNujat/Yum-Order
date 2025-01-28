package com.nujat.yumorder.main_page;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nujat.yumorder.CartActivity;
import com.nujat.yumorder.MainActivity;
import com.nujat.yumorder.R;
import com.nujat.yumorder.ItemAdapterMainPage;
import com.nujat.yumorder.Item;
import java.util.ArrayList;
import java.util.List;

public class MainPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ItemAdapterMainPage adapter;
    private List<Item> itemList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView_items);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        itemList = new ArrayList<>();
        adapter = new ItemAdapterMainPage(this, itemList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();// Initializes Firestore
        loadItems();// Loads data from Firestore

        swipeRefreshLayout.setOnRefreshListener(this::loadItems);// Refresh data on swipe
    }

    private void loadItems() {
        swipeRefreshLayout.setRefreshing(true);// Start showing refresh animation
        db.collection("items")// Access the "items" collection from Firestore
                .get()// Get all documents in the collection
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();// Clear the previous data
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String details = document.getString("details");
                        String price = document.getString("price");
                        String imageUrl = document.getString("imageUrl"); // Get image URL from Firebase
                        // Add new item to the list
                        itemList.add(new Item(name, price, details, imageUrl));
                    }
                    adapter.notifyDataSetChanged(); // Notify adapter that the data has changed
                    swipeRefreshLayout.setRefreshing(false);// Stop refresh animation
                })
                .addOnFailureListener(e -> swipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);// Inflate the menu from XML
        return true;// Return true to display the menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); // Get the ID of the clicked menu item
        if (id == R.id.action_cart) {
            Intent cartIntent = new Intent(MainPage.this, CartActivity.class);
            startActivity(cartIntent);
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainPage.this, MainActivity.class);
            startActivity(intent);
            finish(); finish();  // Finish the current activity so the user can't go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}