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

public class Goal extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private String selectedGoal = ""; // Variable to store the selected goal

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

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
        currentPage = getIntent().getIntExtra("currentPage", totalPages); // Default to totalPages (12)

        // Initialize the circular progress bar and update it to 100%
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();

        // Set up goal selection listeners
        LinearLayout gainMuscleLayout = findViewById(R.id.option_gain_muscle);
        LinearLayout loseWeightLayout = findViewById(R.id.option_lose_weight);
        LinearLayout healthyLifeLayout = findViewById(R.id.option_healthy_life_style);

        gainMuscleLayout.setOnClickListener(v -> {
            selectedGoal = "Gain Muscle"; // Set the selected goal to "Gain Muscle"
            //Toast.makeText(this, "Gain Muscle selected", Toast.LENGTH_SHORT).show();
        });

        loseWeightLayout.setOnClickListener(v -> {
            selectedGoal = "Weight Loss"; // Set the selected goal to "Weight Loss"
            //Toast.makeText(this, "Weight Loss selected", Toast.LENGTH_SHORT).show();
        });

        healthyLifeLayout.setOnClickListener(v -> {
            selectedGoal = "Healthy Life"; // Set the selected goal to "Healthy Life"
            //Toast.makeText(this, "Healthy Life selected", Toast.LENGTH_SHORT).show();
        });

        // Set up the "Next" button to end the process and navigate to the Home page
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedGoal.isEmpty()) {
                saveGoal(); // Save the selected goal
                retrieveAndShowGoal(); // Retrieve and show the goal for verification (optional)
                // Navigate to the Home page
                Intent intent = new Intent(Goal.this, build_profile.class); // Replace HomeActivity with the actual class for your home page
                startActivity(intent);
                finish(); // Optionally finish this activity so the user cannot go back to it
            } else {
                Toast.makeText(Goal.this, "Please select your goal", Toast.LENGTH_SHORT).show();
            }
        });
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

    // Function to retrieve and show the saved goal in a Toast for verification
    private void retrieveAndShowGoal() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedGoal = sharedPreferences.getString("goal", "No goal selected");

        // Toast.makeText(this, "Goal: " + savedGoal, Toast.LENGTH_SHORT).show();
    }
}
