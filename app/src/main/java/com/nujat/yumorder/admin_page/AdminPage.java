package com.nujat.yumorder.admin_page;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
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
import com.nujat.yumorder.main_page.MainPage;

import java.util.ArrayList;
import java.util.List;

public class AdminPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::loadItems);

        Button addItemButton = findViewById(R.id.add_ItemButton);
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPage.this, AddAdmin.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        loadItems();
    }
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
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close MainPage
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadItems() {
        swipeRefreshLayout.setRefreshing(true); // Show refresh indicator
        db.collection("items")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itemList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String price = document.getString("price");
                        itemList.add(new Item(id, name, price));
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false); // Hide refresh indicator
                })
                .addOnFailureListener(e -> swipeRefreshLayout.setRefreshing(false)); // Hide on failure
    }
}
