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

public class DietType extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage = 1;
    private String selectedDietType = ""; // Variable to store the selected diet type

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diet_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 6);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Set up the diet type selection listeners
        Button vegetarianButton = findViewById(R.id.button_vegetarian);
        Button veganButton = findViewById(R.id.button_vegan);
        Button ketoButton = findViewById(R.id.button_keto);
        Button glutenFreeButton = findViewById(R.id.button_glutenfree);
        Button dairyFreeButton = findViewById(R.id.button_dairyfree);
        Button pescatarianButton = findViewById(R.id.button_pescatarian);

        vegetarianButton.setOnClickListener(v -> {
            selectedDietType = "Vegetarian"; // Set the selected diet type to "Vegetarian"
            //Toast.makeText(this, "Vegetarian selected", Toast.LENGTH_SHORT).show();
        });

        veganButton.setOnClickListener(v -> {
            selectedDietType = "Vegan"; // Set the selected diet type to "Vegan"
            // Toast.makeText(this, "Vegan selected", Toast.LENGTH_SHORT).show();
        });

        ketoButton.setOnClickListener(v -> {
            selectedDietType = "Keto"; // Set the selected diet type to "Keto"
            // Toast.makeText(this, "Keto selected", Toast.LENGTH_SHORT).show();
        });

        glutenFreeButton.setOnClickListener(v -> {
            selectedDietType = "Gluten-Free"; // Set the selected diet type to "Gluten-Free"
            //Toast.makeText(this, "Gluten-Free selected", Toast.LENGTH_SHORT).show();
        });

        dairyFreeButton.setOnClickListener(v -> {
            selectedDietType = "Dairy-Free"; // Set the selected diet type to "Dairy-Free"
            //Toast.makeText(this, "Dairy-Free selected", Toast.LENGTH_SHORT).show();
        });

        pescatarianButton.setOnClickListener(v -> {
            selectedDietType = "Pescatarian"; // Set the selected diet type to "Pescatarian"
            //Toast.makeText(this, "Pescatarian selected", Toast.LENGTH_SHORT).show();
        });

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedDietType.isEmpty()) {
                saveDietType(); // Save the selected diet type
                retrieveAndShowDietType(); // Retrieve and show the diet type for verification
                Intent intent = new Intent(DietType.this, Allergies.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(DietType.this, "Please select your diet type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to save the selected diet type in SharedPreferences
    private void saveDietType() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("dietType", selectedDietType); // Save the selected diet type
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved diet type in a Toast for verification
    private void retrieveAndShowDietType() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedDietType = sharedPreferences.getString("dietType", "No diet type selected");

        // Uncomment the following line if you want to display the saved diet type in a Toast
         Toast.makeText(this, "Diet Type: " + savedDietType, Toast.LENGTH_SHORT).show();
    }
}