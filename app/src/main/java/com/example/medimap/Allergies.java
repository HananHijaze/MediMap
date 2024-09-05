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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class Allergies extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;

    private Set<String> selectedAllergies = new HashSet<>();
    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_allergies);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 7);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Set up the allergy buttons
        findViewById(R.id.button_dairy).setOnClickListener(v -> toggleAllergySelection("Dairy"));
        findViewById(R.id.button_gluten).setOnClickListener(v -> toggleAllergySelection("Gluten"));
        findViewById(R.id.button_nuts).setOnClickListener(v -> toggleAllergySelection("Nuts"));
        findViewById(R.id.button_seafood).setOnClickListener(v -> toggleAllergySelection("Seafood"));
        findViewById(R.id.button_soy).setOnClickListener(v -> toggleAllergySelection("Soy"));
        findViewById(R.id.button_eggs).setOnClickListener(v -> toggleAllergySelection("Eggs"));

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedAllergies.isEmpty()) {
                saveAllergies(); // Save the selected allergies
                retrieveAndShowAllergies(); // Retrieve and show the allergies for verification (optional, for debugging)
                Intent intent = new Intent(Allergies.this, FrequencyMeal.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(Allergies.this, "Please select at least one allergy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to toggle the selection of an allergy
    private void toggleAllergySelection(String allergy) {
        if (selectedAllergies.contains(allergy)) {
            selectedAllergies.remove(allergy);
        } else {
            selectedAllergies.add(allergy);
        }
    }

    // Function to save the selected allergies in SharedPreferences
    private void saveAllergies() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putStringSet("allergies", selectedAllergies); // Save the selected allergies
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved allergies in a Toast for verification (optional)
    private void retrieveAndShowAllergies() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> savedAllergies = sharedPreferences.getStringSet("allergies", new HashSet<>());

        // Optional: Display the saved allergies in a Toast
        // Toast.makeText(this, "Allergies: " + savedAllergies.toString(), Toast.LENGTH_SHORT).show();
    }
}