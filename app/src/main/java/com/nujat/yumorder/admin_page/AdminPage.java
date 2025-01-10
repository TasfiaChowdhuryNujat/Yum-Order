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
import com.nujat.yumorder.Item;
import com.nujat.yumorder.ItemAdapter;
import com.nujat.yumorder.R;
import com.nujat.yumorder.add_admin.AddAdmin;
import com.nujat.yumorder.login_page.LoginPage;
import com.nujat.yumorder.add_admin.AddAdmin;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Other imports...

public class AdminPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);

        Toolbar toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

    private void loadItems() {
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
                });
    }
}
