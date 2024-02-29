package com.example.hikermanagment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.List;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;

public class HikersListActivity extends AppCompatActivity {

    NavigationBarView nav;
    private AppDatabase appDatabase;
    private RecyclerView recyclerView;
    private HikerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hikers_list);
        nav = findViewById(R.id.bottomNavigation);

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()== R.id.account){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                }
                else if (item.getItemId() == R.id.add) {
                    startActivity(new Intent(getApplicationContext(), AddActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.home) {
                    return true;
                } else if (item.getItemId() == R.id.search) {
                    startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return true;
                }
                return false;
            }
        });

        appDatabase= Room.databaseBuilder(getApplicationContext(),AppDatabase.class,"hiker_db")
                .allowMainThreadQueries().build();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button delAll = findViewById(R.id.DelAllBtn);

        delAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDeleteAll();
            }
        });

        loadHikers();

        List<Hiker> hikers = appDatabase.hikerDao().getAllHikers();

        // Initialize the adapter and set it to the RecyclerView
        adapter = new HikerAdapter(new HikerAdapter.IClickItemHiker() {
            @Override
            public void updateHiker(Hiker hiker) {
                clickUpdateHiker(hiker);
            }

            @Override
            public void deleteHiker(Hiker hiker) {
                clickDeleteHiker(hiker);
            }
        });

        // Load the initial data into the adapter
        adapter.setData(hikers);

        recyclerView.setAdapter(adapter);


    }

    private void clickUpdateHiker(Hiker hiker) {
        Intent intent = new Intent(HikersListActivity.this, UpdateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_hiker", hiker);
        intent.putExtras(bundle);
        startActivityForResult(intent,1); // Use a specific request code
    }

    private void loadData() {
        // Fetch the updated data from the database
        List<Hiker> updatedHikers = appDatabase.hikerDao().getAllHikers();
        adapter.setData(updatedHikers);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Reload the hiker data when returning from the UpdateActivity
            loadData();
        }
    }

    private void clickDeleteHiker(Hiker hiker){
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete hiker")
                .setMessage("Are you sure")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Delete user
                        appDatabase.hikerDao().deleteHiker(hiker);
                        Toast.makeText(HikersListActivity.this,"Delete User Successfully", Toast.LENGTH_LONG)
                                .show();
                        loadHikers();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    private void clickDeleteAll(){
        new AlertDialog.Builder(this)
                .setTitle("Confirm delete all hikers")
                .setMessage("Are you sure")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Delete user
                        appDatabase.hikerDao().deleteAllHiker();
                        Toast.makeText(HikersListActivity.this,"Delete All Hikers Successfully", Toast.LENGTH_LONG)
                                .show();
                        loadHikers();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    private void loadHikers() {
        // Fetch the updated data from the database
        List<Hiker> hikers = appDatabase.hikerDao().getAllHikers();

        // Initialize the adapter and set it to the RecyclerView
        adapter = new HikerAdapter(new HikerAdapter.IClickItemHiker() {
            @Override
            public void updateHiker(Hiker hiker) {
                clickUpdateHiker(hiker);
            }

            @Override
            public void deleteHiker(Hiker hiker) {
                clickDeleteHiker(hiker);
            }
        });

        // Load the data into the adapter
        adapter.setData(hikers);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);
    }
}