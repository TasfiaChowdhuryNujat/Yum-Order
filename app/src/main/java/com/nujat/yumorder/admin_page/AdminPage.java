// AdminPage.java
package com.nujat.yumorder.admin_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nujat.yumorder.Item;
import com.nujat.yumorder.ItemAdapter;
import com.nujat.yumorder.MainActivity;
import com.nujat.yumorder.R;
import com.nujat.yumorder.add_admin.AddAdmin;
import java.util.ArrayList;
import java.util.List;

public class AdminPage extends AppCompatActivity {
    private RecyclerView recyclerView;// RecyclerView to display the list of items
    private ItemAdapter adapter;// Adapter to manage the items in the RecyclerView
    private List<Item> itemList;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadItems);

        // Setup Add Item Button
        Button addItemButton = findViewById(R.id.add_ItemButton);
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPage.this, AddAdmin.class);
            startActivity(intent);
        });

        // Initialize Firebase and RecyclerView components
        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        // Load initial data
        loadItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadItems() {
        swipeRefreshLayout.setRefreshing(true);
        db.collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Item item = document.toObject(Item.class);
                        item.setId(document.getId());
                        itemList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(AdminPage.this,
                            "Error loading items: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
