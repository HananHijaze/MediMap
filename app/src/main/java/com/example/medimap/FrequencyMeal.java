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

public class FrequencyMeal extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private String selectedMeals = "";
    private String selectedSnacks = "";

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_frequency_meal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 8);
        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Set up the meal selection listeners
        Button button2Meals = findViewById(R.id.button_2_meals);
        Button button3Meals = findViewById(R.id.button_3_meals);
        Button button4Meals = findViewById(R.id.button_4_meals);

        button2Meals.setOnClickListener(v -> selectedMeals = "2 Meals");
        button3Meals.setOnClickListener(v -> selectedMeals = "3 Meals");
        button4Meals.setOnClickListener(v -> selectedMeals = "4 Meals");

        // Set up the snack selection listeners
        Button button0Snacks = findViewById(R.id.button_0_snacks);
        Button button1Snack = findViewById(R.id.button_1_snack);
        Button button2Snacks = findViewById(R.id.button_2_snacks);

        button0Snacks.setOnClickListener(v -> selectedSnacks = "0 Snacks");
        button1Snack.setOnClickListener(v -> selectedSnacks = "1 Snack");
        button2Snacks.setOnClickListener(v -> selectedSnacks = "2 Snacks");

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedMeals.isEmpty() && !selectedSnacks.isEmpty()) {
                saveFrequency(); // Save the selected meal and snack frequency
                retrieveAndShowFrequency(); // Retrieve and show the frequency for verification (optional)
                Intent intent = new Intent(FrequencyMeal.this, WorkOutPlaces.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(FrequencyMeal.this, "Please select both meal and snack frequencies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to save the meal and snack frequency in SharedPreferences
    private void saveFrequency() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("meals", selectedMeals); // Save the selected meal frequency
        editor.putString("snacks", selectedSnacks); // Save the selected snack frequency
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved frequency in a Toast for verification
    private void retrieveAndShowFrequency() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedMeals = sharedPreferences.getString("meals", "No meals selected");
        String savedSnacks = sharedPreferences.getString("snacks", "No snacks selected");

        //Toast.makeText(this, "Meals: " + savedMeals + "\nSnacks: " + savedSnacks, Toast.LENGTH_SHORT).show();
    }
}