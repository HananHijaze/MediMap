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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.util.HashSet;
import java.util.Set;

public class TrainingDays extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private Set<String> selectedDays = new HashSet<>(); // Set to store selected days

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_training_days);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 11);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Set up the day selection listeners
        setupDaySelection(R.id.sunday, "Sunday");
        setupDaySelection(R.id.monday, "Monday");
        setupDaySelection(R.id.tuesday, "Tuesday");
        setupDaySelection(R.id.wednesday, "Wednesday");
        setupDaySelection(R.id.thursday, "Thursday");
        setupDaySelection(R.id.friday, "Friday");
        setupDaySelection(R.id.saturday, "Saturday");

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedDays.isEmpty()) {
                saveTrainingDays(); // Save the selected training days
                retrieveAndShowTrainingDays(); // Retrieve and show the training days for verification (optional)
                Intent intent = new Intent(TrainingDays.this, Goal.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(TrainingDays.this, "Please select your preferred training days", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to handle day selection
    private void setupDaySelection(int cardViewId, String day) {
        CardView dayCardView = findViewById(cardViewId);
        dayCardView.setOnClickListener(v -> {
            if (selectedDays.contains(day)) {
                selectedDays.remove(day); // Unselect the day if it's already selected
                dayCardView.setBackgroundResource(R.drawable.border_unselected); // Reset to unselected border
            } else {
                selectedDays.add(day); // Select the day
                dayCardView.setBackgroundResource(R.drawable.border_selected); // Change to selected border
            }
        });
    }

    // Function to save the selected training days in SharedPreferences
    private void saveTrainingDays() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putStringSet("trainingDays", selectedDays); // Save the selected training days as a Set
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved training days in a Toast for verification
    private void retrieveAndShowTrainingDays() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> savedDays = sharedPreferences.getStringSet("trainingDays", new HashSet<>());

        // Optional: Show selected days in a Toast
        // Toast.makeText(this, "Training Days: " + savedDays.toString(), Toast.LENGTH_SHORT).show();
    }
}
