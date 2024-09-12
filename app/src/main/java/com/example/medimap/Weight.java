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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Weight extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 11;
    private int currentPage;
    private EditText weightInput;

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weight);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 4);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Initialize the weight input field
        weightInput = findViewById(R.id.weightInput);

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (validateWeight()) {  // Validate the weight before proceeding
                saveWeight(); // Save the weight if valid
                retrieveAndShowWeight(); // Retrieve and show the weight for verification (for checking, you can delete it later)
                Intent intent = new Intent(Weight.this, BodyType.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(Weight.this, "Please enter a valid weight", Toast.LENGTH_SHORT).show(); // Show error if validation fails
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to validate the weight input
    private boolean validateWeight() {
        String weightText = weightInput.getText().toString(); // Get the text from the weight input field
        if (weightText.isEmpty()) {  // Check if the weight field is empty
            weightInput.setError("Weight is required"); // Set an error message if empty
            return false;
        }

        try {
            float weightValue = Float.parseFloat(weightText); // Convert the weight text to a float
            if (weightValue <= 0) {  // Ensure the weight is a positive number
                weightInput.setError("Please enter a positive weight value");
                return false;
            }
        } catch (NumberFormatException e) {
            weightInput.setError("Please enter a valid weight");
            return false;
        }

        return true; // Weight is valid
    }

    // Function to save the weight in SharedPreferences
    private void saveWeight() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Access SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("weight", weightInput.getText().toString()); // Save the weight as a string
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved weight in a Toast for verification
    private void retrieveAndShowWeight() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedWeight = sharedPreferences.getString("weight", "No weight entered");

        // Uncomment the following line if you want to display the saved weight in a Toast
        // Toast.makeText(this, "Weight: " + savedWeight + " kg", Toast.LENGTH_SHORT).show();
    }
}
