package com.example.medimap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.Toast;

public class WorkOutTime extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private String selectedWorkoutTime = ""; // Variable to store the selected workout time

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_out_time);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 10);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Initialize the RadioGroup and set up a listener for the selected option
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            selectedWorkoutTime = selectedRadioButton.getText().toString(); // Get the selected workout time
        });

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedWorkoutTime.isEmpty()) {
                saveWorkoutTime(); // Save the selected workout time
                retrieveAndShowWorkoutTime(); // Retrieve and show the workout time for verification (optional)
                Intent intent = new Intent(WorkOutTime.this, TrainingDays.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(WorkOutTime.this, "Please select your preferred workout time", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to save the selected workout time in SharedPreferences
    private void saveWorkoutTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("workoutTime", selectedWorkoutTime); // Save the selected workout time
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved workout time in a Toast for verification
    private void retrieveAndShowWorkoutTime() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedWorkoutTime = sharedPreferences.getString("workoutTime", "No workout time selected");

      //  Toast.makeText(this, "Workout Time: " + savedWorkoutTime, Toast.LENGTH_SHORT).show();
    }
}
