package com.example.hikermanagment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class UpdateActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private int year; // Declare year as a class-level variable
    private int month; // Declare month as a class-level variable
    private int day; // Declare day as a class-level variable
    public String parking_available;
    private Spinner spinnerDifficulty;
    private String selectedDifficulty;
    private Hiker hikers;
    TextView dobPicker;
    RadioGroup radioGroupParking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "hiker_db")
                .allowMainThreadQueries().build();

        EditText nameTxt = findViewById(R.id.nameTxt);
        EditText locationTxt = findViewById(R.id.locationTxt);
        EditText dateText = findViewById(R.id.dateTxt);
        EditText lengthText = findViewById(R.id.lenghtTxt);
        EditText descriptionTxt = findViewById(R.id.descriptionTxt);
        Button updateHiker = findViewById(R.id.updateHiker);
        Button goBack = findViewById(R.id.goBack);
        spinnerDifficulty=findViewById(R.id.spinnerDifficulty);
        radioGroupParking=findViewById(R.id.radioGroupParking);
        dobPicker = findViewById(R.id.dateTxt);

        hikers = (Hiker) getIntent().getExtras().get("object_hiker");
        if (hikers != null) {
            nameTxt.setText(hikers.name);
            locationTxt.setText(hikers.location);
            dateText.setText(hikers.date);
            lengthText.setText(String.valueOf(hikers.length)); //convert into String
            descriptionTxt.setText(hikers.description);

            // Set the selected parking availability based on hikers.park_available
            if ("Yes".equals(hikers.park_available)) {
                radioGroupParking.check(R.id.radioYes);
            } else if ("No".equals(hikers.park_available)) {
                radioGroupParking.check(R.id.radioNo);
            }

            // Set the selected difficulty level based on hikers.diff_level
            int difficultyPosition = getDifficultyPosition(hikers.diff_level);
            if (difficultyPosition >= 0) {
                spinnerDifficulty.setSelection(difficultyPosition);
            }


        }

        dobPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

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

        updateHiker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateHiker();
            }
        });

        goBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToLists();
            }
        }));

    }

    private void backToLists() {
        Intent intent;
        String sourceActivity = getIntent().getStringExtra("source_activity");

        if ("SearchActivity".equals(sourceActivity)) {
            intent = new Intent(UpdateActivity.this, SearchActivity.class);
        } else {
            intent = new Intent(UpdateActivity.this, HikersListActivity.class);
        }

        startActivity(intent);
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

    private void updateHiker() {

        EditText nameTxt = findViewById(R.id.nameTxt);
        EditText locationTxt = findViewById(R.id.locationTxt);
        EditText dateText = findViewById(R.id.dateTxt);
        EditText lengthText = findViewById(R.id.lenghtTxt);
        EditText descriptionTxt = findViewById(R.id.descriptionTxt);


        String name = nameTxt.getText().toString();
        String location = locationTxt.getText().toString();
        String date = dateText.getText().toString();
        String length = lengthText.getText().toString();
        String description = descriptionTxt.getText().toString();

        RadioButton rBtn1 = findViewById(R.id.radioYes);
        RadioButton rBtn2 = findViewById(R.id.radioNo);

        int selectedParkingRadioButtonId = radioGroupParking.getCheckedRadioButtonId();
        RadioButton selectedParkingRadioButton = findViewById(selectedParkingRadioButtonId);

        if (selectedParkingRadioButton != null) {
            parking_available = selectedParkingRadioButton.getText().toString();
        }

        int difficultyPosition = spinnerDifficulty.getSelectedItemPosition();
        if (difficultyPosition >= 0) {
            selectedDifficulty = spinnerDifficulty.getSelectedItem().toString();
        }

        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || length.isEmpty()){
            Toast.makeText(this,"Name, location, date or length cannot be empty",Toast.LENGTH_LONG).show();
            return;
        }

        if (hikers != null) {
            // Update the existing hikers object with the new data
            hikers.name = name;
            hikers.location = location;
            hikers.date = date;
            hikers.park_available = parking_available;
            hikers.length = Integer.parseInt(length);
            hikers.diff_level = selectedDifficulty;
            hikers.description = description;

            // Update the hikers object in the database
            int hikerId = appDatabase.hikerDao().updateHiker(hikers);

            Toast.makeText(this, "Update hiker successfully: " + hikerId, Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();

        } else {
            // Handle the case where hikers is null (optional)
            Toast.makeText(this, "Hiker data not found for update.", Toast.LENGTH_LONG).show();
        }
    }
    private int getDifficultyPosition(String difficulty) {
        // Find the position of the selected difficulty in the Spinner adapter
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerDifficulty.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equals(difficulty)) {
                    return i;
                }
            }
        }
        return -1;
    }

}