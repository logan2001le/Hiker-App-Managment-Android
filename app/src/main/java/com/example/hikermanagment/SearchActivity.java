package com.example.hikermanagment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    NavigationBarView nav;
    private EditText searchEditText;
    private Button searchButton;
    private AppDatabase appDatabase;
    private HikerAdapter hikerAdapter;
    private List<Hiker> ListHiker;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "hiker_db")
                .allowMainThreadQueries().build();

        nav = findViewById(R.id.bottomNavigation);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.account) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.add) {
                    startActivity(new Intent(getApplicationContext(), AddActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    return true;
                }
                return false;
            }
        });

        searchEditText = findViewById(R.id.editSearch);
        searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchName();
            }
        });

        // Fetch the updated data from the database
        loadData();
    }

    private void searchName() {
        String strKeyword = searchEditText.getText().toString().trim();
        ListHiker = appDatabase.hikerDao().searchHiker(strKeyword);

        // Initialize the adapter and set it to the RecyclerView after the search
        hikerAdapter = new HikerAdapter(new HikerAdapter.IClickItemHiker() {
            @Override
            public void updateHiker(Hiker hiker) {
                clickUpdateHiker(hiker);
            }

            @Override
            public void deleteHiker(Hiker hiker) {
                clickDeleteHiker(hiker);
            }
        });

        recyclerView.setAdapter(hikerAdapter);

        // Update the adapter with the search results
        hikerAdapter.setData(ListHiker);
    }

    private void clickUpdateHiker(Hiker hiker) {
        Intent intent = new Intent(SearchActivity.this, UpdateActivity.class);
        intent.putExtra("source_activity", "SearchActivity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_hiker", hiker);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1); // Use a specific request code
    }

    private void loadData() {
        // Fetch the updated data from the database
        List<Hiker> updatedHikers = appDatabase.hikerDao().getAllHikers();

        // Initialize the adapter and set it to the RecyclerView during the initial setup
        hikerAdapter = new HikerAdapter(new HikerAdapter.IClickItemHiker() {
            @Override
            public void updateHiker(Hiker hiker) {
                clickUpdateHiker(hiker);
            }

            @Override
            public void deleteHiker(Hiker hiker) {
                clickDeleteHiker(hiker);
            }
        });

        recyclerView.setAdapter(hikerAdapter);

        // Load the initial data into the adapter
        hikerAdapter.setData(updatedHikers);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Reload the hiker data when returning from the UpdateActivity
            loadData();
        }
    }

    private void clickDeleteHiker(Hiker hiker) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete hiker")
                .setMessage("Are you sure")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Delete user
                        appDatabase.hikerDao().deleteHiker(hiker);
                        Toast.makeText(SearchActivity.this, "Delete User Successfully", Toast.LENGTH_LONG)
                                .show();
                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clickDeleteAll() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete all hikers")
                .setMessage("Are you sure")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Delete user
                        appDatabase.hikerDao().deleteAllHiker();
                        Toast.makeText(SearchActivity.this, "Delete All Hikers Successfully", Toast.LENGTH_LONG)
                                .show();
                        loadData();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
