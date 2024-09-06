package com.example.medimap;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DietType extends AppCompatActivity {
    private String selectedDietType = ""; // Variable to store the selected diet type

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_type);

        // Set up the diet type selection listeners
        LinearLayout vegetarianButton = findViewById(R.id.button_vegetarian);
        LinearLayout veganButton = findViewById(R.id.button_vegan);
        LinearLayout ketoButton = findViewById(R.id.button_keto);
        LinearLayout glutenFreeButton = findViewById(R.id.button_glutenfree);
        LinearLayout dairyFreeButton = findViewById(R.id.button_dairyfree);
        LinearLayout pescatarianButton = findViewById(R.id.button_pescatarian);

        vegetarianButton.setOnClickListener(v -> selectDietType("Vegetarian", vegetarianButton, veganButton, ketoButton, glutenFreeButton, dairyFreeButton, pescatarianButton));
        veganButton.setOnClickListener(v -> selectDietType("Vegan", veganButton, vegetarianButton, ketoButton, glutenFreeButton, dairyFreeButton, pescatarianButton));
        ketoButton.setOnClickListener(v -> selectDietType("Keto", ketoButton, vegetarianButton, veganButton, glutenFreeButton, dairyFreeButton, pescatarianButton));
        glutenFreeButton.setOnClickListener(v -> selectDietType("Gluten-Free", glutenFreeButton, vegetarianButton, veganButton, ketoButton, dairyFreeButton, pescatarianButton));
        dairyFreeButton.setOnClickListener(v -> selectDietType("Dairy-Free", dairyFreeButton, vegetarianButton, veganButton, ketoButton, glutenFreeButton, pescatarianButton));
        pescatarianButton.setOnClickListener(v -> selectDietType("Pescatarian", pescatarianButton, vegetarianButton, veganButton, ketoButton, glutenFreeButton, dairyFreeButton));

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedDietType.isEmpty()) {
                saveDietType(); // Save the selected diet type
                Intent intent = new Intent(DietType.this, Allergies.class);
                startActivity(intent);
            } else {
                Toast.makeText(DietType.this, "Please select your diet type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to save the selected diet type in SharedPreferences
    private void saveDietType() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dietType", selectedDietType);
        editor.apply();
    }

    // Function to handle the selection of a diet type and update UI accordingly
    private void selectDietType(String dietType, LinearLayout selectedLayout, LinearLayout... others) {
        selectedDietType = dietType;
        selectedLayout.setBackgroundResource(R.drawable.border_selected); // Set selected border

        // Reset other buttons to unselected border
        for (LinearLayout layout : others) {
            layout.setBackgroundResource(R.drawable.border_unselected);
        }
    }
}
