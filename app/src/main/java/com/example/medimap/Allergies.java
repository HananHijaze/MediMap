package com.example.medimap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class Allergies extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 11;
    private int currentPage;
    private Set<String> selectedAllergies = new HashSet<>();
    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentPage = getIntent().getIntExtra("currentPage", 7);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();

        // Set up the allergy options with click listeners
        setupAllergyOption(findViewById(R.id.button_dairy), "Dairy");
        setupAllergyOption(findViewById(R.id.button_gluten), "Gluten");
        setupAllergyOption(findViewById(R.id.button_nuts), "Nuts");
        setupAllergyOption(findViewById(R.id.button_seafood), "Seafood");
        setupAllergyOption(findViewById(R.id.button_soy), "Soy");
        setupAllergyOption(findViewById(R.id.button_eggs), "Eggs");
        setupAllergyOption(findViewById(R.id.button_none), "None");

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedAllergies.isEmpty()) {
                saveAllergies();
                retrieveAndShowAllergies(); // Optional for debugging
                Intent intent = new Intent(Allergies.this, FrequencyMeal.class);
                intent.putExtra("currentPage", currentPage + 1);
                startActivity(intent);
            } else {
                Toast.makeText(Allergies.this, "Please select at least one allergy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;
        circularProgressBar.setProgress(progress);
    }

    private void setupAllergyOption(LinearLayout layout, String allergy) {
        layout.setOnClickListener(v -> {
            if ("None".equals(allergy)) {
                selectedAllergies.clear();
                selectedAllergies.add(allergy);
                refreshAllergySelections();
            } else {
                toggleAllergySelection(allergy, layout);
            }
        });
        updateSelectionState(layout, allergy);
    }

    private void toggleAllergySelection(String allergy, View layout) {
        if (selectedAllergies.contains(allergy)) {
            selectedAllergies.remove(allergy);
            layout.setBackgroundResource(R.drawable.border_unselected);
        } else {
            selectedAllergies.remove("None");
            selectedAllergies.add(allergy);
            refreshAllergySelections();
        }
    }

    private void updateSelectionState(View layout, String allergy) {
        if (selectedAllergies.contains(allergy)) {
            layout.setBackgroundResource(R.drawable.border_selected);
        } else {
            layout.setBackgroundResource(R.drawable.border_unselected);
        }
    }

    private void refreshAllergySelections() {
        updateSelectionState(findViewById(R.id.button_dairy), "Dairy");
        updateSelectionState(findViewById(R.id.button_gluten), "Gluten");
        updateSelectionState(findViewById(R.id.button_nuts), "Nuts");
        updateSelectionState(findViewById(R.id.button_seafood), "Seafood");
        updateSelectionState(findViewById(R.id.button_soy), "Soy");
        updateSelectionState(findViewById(R.id.button_eggs), "Eggs");
        updateSelectionState(findViewById(R.id.button_none), "None");
    }

    private void saveAllergies() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("allergies", selectedAllergies);
        editor.apply();
    }

    private void retrieveAndShowAllergies() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> savedAllergies = sharedPreferences.getStringSet("allergies", new HashSet<>());
        // Toast.makeText(this, "Allergies: " + savedAllergies.toString(), Toast.LENGTH_SHORT).show();
    }
}
