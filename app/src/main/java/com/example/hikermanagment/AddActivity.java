package com.example.hikermanagment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

public class AddActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    TextView dobPicker;
    private int year; // Declare year as a class-level variable
    private int month; // Declare month as a class-level variable
    private int day; // Declare day as a class-level variable
    public String parking_available;

    private Spinner spinnerDifficulty;
    private String selectedDifficulty;

    private List<Hiker> hikers;
    NavigationBarView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

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
                    return true;
                } else if (item.getItemId() == R.id.home) {
                    startActivity(new Intent(getApplicationContext(), HikersListActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
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

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "hiker_db")
                .allowMainThreadQueries().build();

        Button saveDetailsBtn = findViewById(R.id.saveBtn);
        dobPicker = findViewById(R.id.dateTxt);

        saveDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHikers();
            }
        });

        dobPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.difficulty_levels,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDifficulty.setAdapter(adapter);

        // Set up a listener to capture the selected difficulty level
        spinnerDifficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedDifficulty = parentView.getItemAtPosition(position).toString();
                // You can save the selectedDifficulty to your entity class or perform other actions as needed.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle when nothing is selected.
            }
        });


    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    // Define a DatePickerDialog.OnDateSetListener to handle the selected date
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            // Update the DOB EditText with the selected date
            String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, day);
            dobPicker.setText(formattedDate);
        }
    };

    private boolean isHikerExist(Hiker hiker){
        List<Hiker> hikerList=appDatabase.hikerDao().checkHiker(hiker.name);
        return hikerList!= null && !hikerList.isEmpty();
    }

    private void saveHikers() {

        EditText nameTxt = findViewById(R.id.nameTxt);
        EditText locationTxt= findViewById(R.id.locationTxt);
        EditText dateText = findViewById(R.id.dateTxt);
        EditText lengthText = findViewById(R.id.lenghtTxt);
        EditText descriptionTxt = findViewById(R.id.descriptionTxt);


        String name = nameTxt.getText().toString();
        String location=locationTxt.getText().toString();
        String date = dateText.getText().toString();
        String length = lengthText.getText().toString();
        String description = descriptionTxt.getText().toString();

        RadioButton rBtn1 = findViewById(R.id.radioYes);
        RadioButton rBtn2 = findViewById(R.id.radioNo);



        if (rBtn1.isChecked()){
            parking_available="Yes";
        }else if (rBtn2.isChecked()){
            parking_available="No";}

        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || length.isEmpty()){
            Toast.makeText(this,"Name, location, date or length cannot be empty",Toast.LENGTH_LONG).show();
            return;
        }

        Hiker hiker = new Hiker();
        hiker.name=name;
        hiker.location=location;
        hiker.date=date;
        hiker.park_available=parking_available;
        hiker.length= Integer.parseInt(length);
        hiker.diff_level=selectedDifficulty;
        hiker.description= description;

        if (isHikerExist(hiker)){
            Toast.makeText(this,"Hiker exist",Toast.LENGTH_LONG).show();
            return;
        }

        long hikerId = appDatabase.hikerDao().insertHiker(hiker);

        Toast.makeText(this,"Hiker has been create with id: "+hikerId,Toast.LENGTH_LONG)
                .show();

        //Launch Activity
        Intent intent = new Intent(this, HikersListActivity.class);
        startActivity(intent);
    }

}
