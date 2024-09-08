package com.example.medimap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WorkOutPlaces extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private String selectedWorkoutPlace = ""; // Variable to store the selected workout place

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    private LinearLayout homeLayout, gymLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_out_places);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 9);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Retrieve LinearLayouts for Home and Gym
        homeLayout = findViewById(R.id.homeLayout);
        gymLayout = findViewById(R.id.gymLayout);

        // Set up the workout place selection listeners
        homeLayout.setOnClickListener(v -> {
            selectedWorkoutPlace = "Home"; // Set the selected workout place to "Home"
            toggleSelection(homeLayout, gymLayout);
        });

        gymLayout.setOnClickListener(v -> {
            selectedWorkoutPlace = "Gym"; // Set the selected workout place to "Gym"
            toggleSelection(gymLayout, homeLayout);
        });

        // Set up the "Next" button to navigate to the next activity
        findViewById(R.id.nextButton).setOnClickListener(v -> {
            if (!selectedWorkoutPlace.isEmpty()) {
                saveWorkoutPlace(); // Save the selected workout place
                retrieveAndShowWorkoutPlace(); // Retrieve and show the workout place for verification
                Intent intent = new Intent(WorkOutPlaces.this, WorkOutTime.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(WorkOutPlaces.this, "Please select your workout place", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to toggle the selection between two layouts and update the UI
    private void toggleSelection(LinearLayout selectedLayout, LinearLayout otherLayout) {
        selectedLayout.setBackgroundResource(R.drawable.border_selected); // Apply the selected border to the chosen layout
        otherLayout.setBackgroundResource(R.drawable.border_unselected);  // Apply the unselected border to the other layout
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to save the selected workout place in SharedPreferences
    private void saveWorkoutPlace() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("workoutPlace", selectedWorkoutPlace); // Save the selected workout place
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved workout place in a Toast for verification (optional)
    private void retrieveAndShowWorkoutPlace() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedWorkoutPlace = sharedPreferences.getString("workoutPlace", "No place selected");
        Toast.makeText(this, "Workout Place: " + savedWorkoutPlace, Toast.LENGTH_SHORT).show();
    }
}
