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
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class WorkOutPlaces extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private String selectedWorkoutPlace = ""; // Variable to store the selected workout place

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

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

        // Retrieve TextView elements and their parent LinearLayouts
        TextView homeTextView = findViewById(R.id.home);
        TextView gymTextView = findViewById(R.id.gym);

        LinearLayout homeLayout = (LinearLayout) homeTextView.getParent();
        LinearLayout gymLayout = (LinearLayout) gymTextView.getParent();

        // Set up the workout place selection listeners
        homeLayout.setOnClickListener(v -> {
            selectedWorkoutPlace = "Home"; // Set the selected workout place to "Home"
            //Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
        });

        gymLayout.setOnClickListener(v -> {
            selectedWorkoutPlace = "Dedicated place"; // Set the selected workout place to "Dedicated place"
           // Toast.makeText(this, "Dedicated place selected", Toast.LENGTH_SHORT).show();
        });

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedWorkoutPlace.isEmpty()) {
                saveWorkoutPlace(); // Save the selected workout place
                retrieveAndShowWorkoutPlace(); // Retrieve and show the workout place for verification (optional)
                Intent intent = new Intent(WorkOutPlaces.this, WorkOutTime.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(WorkOutPlaces.this, "Please select your workout place", Toast.LENGTH_SHORT).show();
            }
        });
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

    // Function to retrieve and show the saved workout place in a Toast for verification
    private void retrieveAndShowWorkoutPlace() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedWorkoutPlace = sharedPreferences.getString("workoutPlace", "No place selected");

       // Toast.makeText(this, "Workout Place: " + savedWorkoutPlace, Toast.LENGTH_SHORT).show();
    }
}
