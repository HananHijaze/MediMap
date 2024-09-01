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

public class BodyType extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private String selectedBodyType = ""; // Variable to store the selected body type

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_body_type);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 5);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Set up the body type selection listeners
        LinearLayout skinnyLayout = findViewById(R.id.skinny);
        LinearLayout normalLayout = findViewById(R.id.normal);
        LinearLayout heavyLayout = findViewById(R.id.heavy);

        skinnyLayout.setOnClickListener(v -> {
            selectedBodyType = "Skinny"; // Set the selected body type to "Skinny"
            //Toast.makeText(this, "Skinny selected", Toast.LENGTH_SHORT).show();
        });

        normalLayout.setOnClickListener(v -> {
            selectedBodyType = "Normal"; // Set the selected body type to "Normal"
            //Toast.makeText(this, "Normal selected", Toast.LENGTH_SHORT).show();
        });

        heavyLayout.setOnClickListener(v -> {
            selectedBodyType = "Heavier"; // Set the selected body type to "Heavier"
           // Toast.makeText(this, "Heavier selected", Toast.LENGTH_SHORT).show();
        });

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedBodyType.isEmpty()) {
                saveBodyType(); // Save the selected body type
                retrieveAndShowBodyType(); // Retrieve and show the body type for verification
                Intent intent = new Intent(BodyType.this, DietType.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(BodyType.this, "Please select your body type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Calculate the percentage of progress
        circularProgressBar.setProgress(progress);
    }

    // Function to save the selected body type in SharedPreferences
    private void saveBodyType() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("bodyType", selectedBodyType); // Save the selected body type
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved body type in a Toast for verification
    private void retrieveAndShowBodyType() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedBodyType = sharedPreferences.getString("bodyType", "No body type selected");

        // Uncomment the following line if you want to display the saved body type in a Toast
        // Toast.makeText(this, "Body Type: " + savedBodyType, Toast.LENGTH_SHORT).show();
    }
}
