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

public class FrequencyMeal extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 11;
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

        currentPage = getIntent().getIntExtra("currentPage", 8);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();

        setupSelection(findViewById(R.id.button_Two_Meals), "2 Meals", true);
        setupSelection(findViewById(R.id.button_Three_Meals), "3 Meals", true);
        setupSelection(findViewById(R.id.button_Four_Meals), "4 Meals", true);

        setupSelection(findViewById(R.id.button_zero_snacks), "0 Snacks", false);
        setupSelection(findViewById(R.id.button_one_snack), "1 Snack", false);
        setupSelection(findViewById(R.id.button_Two_Snacks), "2 Snacks", false);

       Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedMeals.isEmpty() && !selectedSnacks.isEmpty()) {
                saveFrequency();
                retrieveAndShowFrequency();
                Intent intent = new Intent(FrequencyMeal.this, WorkOutPlaces.class);
                startActivity(intent);
            } else {
                Toast.makeText(FrequencyMeal.this, "Please select both meal and snack frequencies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSelection(LinearLayout layout, String type, boolean isMeal) {
        layout.setOnClickListener(v -> {
            if (isMeal) {
                selectedMeals = type;
                resetSelections(true);
            } else {
                selectedSnacks = type;
                resetSelections(false);
            }
            layout.setBackgroundResource(R.drawable.border_selected);
            Toast.makeText(FrequencyMeal.this, type + " selected", Toast.LENGTH_SHORT).show();
        });
    }

    private void resetSelections(boolean isMeal) {
        int[] mealIds = {R.id.button_Two_Meals, R.id.button_Three_Meals, R.id.button_Four_Meals};
        int[] snackIds = {R.id.button_zero_snacks, R.id.button_one_snack, R.id.button_Two_Snacks};

        for (int id : (isMeal ? mealIds : snackIds)) {
            LinearLayout layout = findViewById(id);
            layout.setBackgroundResource(R.drawable.border_unselected);
        }
    }

    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;
        circularProgressBar.setProgress(progress);
    }

    private void saveFrequency() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("meals", selectedMeals);
        editor.putString("snacks", selectedSnacks);
        editor.apply();
    }

    private void retrieveAndShowFrequency() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedMeals = sharedPreferences.getString("meals", "No meals selected");
        String savedSnacks = sharedPreferences.getString("snacks", "No snacks selected");
        Toast.makeText(this, "Meals: " + savedMeals + "\nSnacks: " + savedSnacks, Toast.LENGTH_LONG).show();
    }
}
