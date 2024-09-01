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

public class height extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private EditText heightInput;

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_height);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 3);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Initialize the height input field
        heightInput = findViewById(R.id.heightInput);

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (validateHeight()) {  // Validate the height before proceeding
                saveHeight(); // Save the height if valid
                retrieveAndShowHeight(); // Retrieve and show the height for verification (for checking, you can delete it later)
                Intent intent = new Intent(height.this, Weight.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(height.this, "Please enter a valid height", Toast.LENGTH_SHORT).show(); // Show error if validation fails
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to validate the height input
    private boolean validateHeight() {
        String heightText = heightInput.getText().toString(); // Get the text from the height input field
        if (heightText.isEmpty()) {  // Check if the height field is empty
            heightInput.setError("Height is required"); // Set an error message if empty
            return false;
        }

        int heightValue = Integer.parseInt(heightText); // Convert the height text to an integer
        if (heightValue < 50 || heightValue > 250) {  // Assuming height should be between 50 cm and 250 cm
            heightInput.setError("Please enter a height between 50 and 250 cm"); // Set an error message if out of range
            return false; // Return false if the height is invalid
        }

        return true; // Height is valid
    }

    // Function to save the height in SharedPreferences
    private void saveHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Access SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("height", heightInput.getText().toString()); // Save the height as a string
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved height in a Toast for verification
    private void retrieveAndShowHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedHeight = sharedPreferences.getString("height", "No height entered");

        // Uncomment the following line if you want to display the saved height in a Toast
        // Toast.makeText(this, "Height: " + savedHeight + " cm", Toast.LENGTH_SHORT).show();
    }
}
