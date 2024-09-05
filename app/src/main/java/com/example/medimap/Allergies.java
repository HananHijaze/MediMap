package com.example.medimap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

import java.util.HashSet;
import java.util.Set;

public class Allergies extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;

    private Set<String> selectedAllergies = new HashSet<>();
    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    private CardView dairyCard, glutenCard, nutsCard, seafoodCard, soyCard, eggsCard, noneCard;

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

        // Initialize card views for each allergy
        dairyCard = findViewById(R.id.card_dairy);
        glutenCard = findViewById(R.id.card_gluten);
        nutsCard = findViewById(R.id.card_nuts);
        seafoodCard = findViewById(R.id.card_seafood);
        soyCard = findViewById(R.id.card_soy);
        eggsCard = findViewById(R.id.card_eggs);
        noneCard = findViewById(R.id.card_none);

        // Set up allergy button click listeners
        dairyCard.setOnClickListener(v -> toggleAllergySelection("Dairy", dairyCard));
        glutenCard.setOnClickListener(v -> toggleAllergySelection("Gluten", glutenCard));
        nutsCard.setOnClickListener(v -> toggleAllergySelection("Nuts", nutsCard));
        seafoodCard.setOnClickListener(v -> toggleAllergySelection("Seafood", seafoodCard));
        soyCard.setOnClickListener(v -> toggleAllergySelection("Soy", soyCard));
        eggsCard.setOnClickListener(v -> toggleAllergySelection("Eggs", eggsCard));
        noneCard.setOnClickListener(v -> clearAllergiesSelection());  // Handle "None" button functionality

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedAllergies.isEmpty()) {
                saveAllergies(); // Save the selected allergies
                Intent intent = new Intent(Allergies.this, FrequencyMeal.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(Allergies.this, "Please select at least one allergy or 'None'", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to toggle the selection of an allergy and update the UI
    private void toggleAllergySelection(String allergy, CardView selectedCard) {
        selectedAllergies.remove("None"); // Remove "None" if other allergies are selected
        if (selectedAllergies.contains(allergy)) {
            selectedAllergies.remove(allergy);
            selectedCard.setBackgroundResource(R.drawable.border_unselected); // Set unselected border
        } else {
            selectedAllergies.add(allergy);
            selectedCard.setBackgroundResource(R.drawable.border_selected); // Set selected border
        }
        noneCard.setBackgroundResource(R.drawable.border_unselected); // Always unselect "None"
    }

    // Function to clear all selected allergies when "None" is selected and update the UI
    private void clearAllergiesSelection() {
        selectedAllergies.clear();  // Clear all selections
        selectedAllergies.add("None");  // Add "None" to the selected allergies

        // Reset all allergy buttons to unselected
        dairyCard.setBackgroundResource(R.drawable.border_unselected);
        glutenCard.setBackgroundResource(R.drawable.border_unselected);
        nutsCard.setBackgroundResource(R.drawable.border_unselected);
        seafoodCard.setBackgroundResource(R.drawable.border_unselected);
        soyCard.setBackgroundResource(R.drawable.border_unselected);
        eggsCard.setBackgroundResource(R.drawable.border_unselected);
        noneCard.setBackgroundResource(R.drawable.border_selected); // Highlight the "None" button
    }

    // Function to save the selected allergies in SharedPreferences
    private void saveAllergies() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("allergies", selectedAllergies); // Save the selected allergies
        editor.apply(); // Apply the changes asynchronously
    }
}
