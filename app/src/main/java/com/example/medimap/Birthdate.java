package com.example.medimap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Birthdate extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage = 1;
    private CalendarView calendarView;
    private long selectedDate = -1; // Store the selected date as a timestamp

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_birthdate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();

        // Initialize the CalendarView and set a date change listener
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Store the selected date as a timestamp (number of milliseconds since 1970)
            selectedDate = calendarView.getDate();
        });

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (selectedDate != -1) {
                // If a date is selected, save it and proceed
                saveBirthdate();
                retrieveAndShowBirthdate(); // Retrieve and show the birthdate for verification
                Intent intent = new Intent(Birthdate.this, Gender.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                // If no date is selected, show a Toast message
                Toast.makeText(Birthdate.this, "Please select your birthdate", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;
        circularProgressBar.setProgress(progress);
    }

    // Function to save the selected birthdate in SharedPreferences
    private void saveBirthdate() {
        // Access SharedPreferences to save the selected birthdate
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save the selected date (timestamp) to SharedPreferences
        editor.putLong("birthdate", selectedDate);
        editor.apply(); // Apply the changes asynchronously
    }

  /**/  // Function to retrieve and show the saved birthdate in a Toast for verification
    private void retrieveAndShowBirthdate() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve the birthdate from SharedPreferences
        long birthdateTimestamp = sharedPreferences.getLong("birthdate", -1);

        if (birthdateTimestamp != -1) {
            // Convert the timestamp to a Date object and format it as needed
            java.util.Date birthdate = new java.util.Date(birthdateTimestamp);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(birthdate);

            // Show the retrieved birthdate in a Toast message
          /*  Toast.makeText(this, "Birthdate: " + formattedDate, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No birthdate found", Toast.LENGTH_SHORT).show();
        }*/
    }
}
}
