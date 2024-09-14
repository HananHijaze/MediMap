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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Goal extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 11;
    private int currentPage;
    private String selectedGoal = ""; // Variable to store the selected goal

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    private LinearLayout gainMuscleLayout, loseWeightLayout, healthyLifeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_goal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", totalPages); // Default to totalPages (11)

        // Initialize the circular progress bar and update it to 100%
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();

        // Initialize layouts for each goal option
        gainMuscleLayout = findViewById(R.id.option_gain_muscle);
        loseWeightLayout = findViewById(R.id.option_lose_weight);
        healthyLifeLayout = findViewById(R.id.option_healthy_life_style);

        // Set up goal selection listeners
        gainMuscleLayout.setOnClickListener(v -> {
            selectedGoal = "Gain Muscle"; // Set the selected goal to "Gain Muscle"
            toggleSelection(gainMuscleLayout, loseWeightLayout, healthyLifeLayout);
        });

        loseWeightLayout.setOnClickListener(v -> {
            selectedGoal = "Weight Loss"; // Set the selected goal to "Weight Loss"
            toggleSelection(loseWeightLayout, gainMuscleLayout, healthyLifeLayout);
        });

        healthyLifeLayout.setOnClickListener(v -> {
            selectedGoal = "Healthy Life"; // Set the selected goal to "Healthy Life"
            toggleSelection(healthyLifeLayout, gainMuscleLayout, loseWeightLayout);
        });

        // Set up the "Next" button to navigate to the Home page
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedGoal.isEmpty()) {
                saveGoal(); // Save the selected goal
                retrieveAndShowGoal(); // Retrieve and show the goal for verification (optional)
                Intent intent = new Intent(Goal.this, build_profile.class); // Replace with actual next activity
               // intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity

                startActivity(intent);
                finish(); // Optionally finish this activity so the user cannot go back to it
            } else {
                Toast.makeText(Goal.this, "Please select your goal", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to toggle the selection between layouts and apply the selected/unselected border
    private void toggleSelection(LinearLayout selectedLayout, LinearLayout... otherLayouts) {
        selectedLayout.setBackgroundResource(R.drawable.border_selected); // Apply selected border
        for (LinearLayout layout : otherLayouts) {
            layout.setBackgroundResource(R.drawable.border_unselected); // Apply unselected border
        }
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Final page should show 100%
        circularProgressBar.setProgress(progress);
    }

    // Function to save the selected goal in SharedPreferences
    private void saveGoal() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("goal", selectedGoal); // Save the selected goal
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved goal in a Toast for verification (optional)
    private void retrieveAndShowGoal() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedGoal = sharedPreferences.getString("goal", "No goal selected");
        Toast.makeText(this, "Goal: " + savedGoal, Toast.LENGTH_SHORT).show();
    }
}
