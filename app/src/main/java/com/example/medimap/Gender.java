package com.example.medimap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Button;
import android.widget.Toast;

public class Gender extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;
    private String selectedGender = ""; // Variable to store the selected gender

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gender);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", 2);

        // Initialize the circular progress bar
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();  // Update progress bar based on the current page

        // Set up the gender selection listeners
        LinearLayout femaleLayout = findViewById(R.id.linearLayout2);
        LinearLayout maleLayout = findViewById(R.id.linearLayout3);

        femaleLayout.setOnClickListener(v -> {
            selectedGender = "Female"; // Set the selected gender to "Female"
            // Toast.makeText(this, "Female selected", Toast.LENGTH_SHORT).show(); // Show a Toast when Female is selected
        });

        maleLayout.setOnClickListener(v -> {
            selectedGender = "Male"; // Set the selected gender to "Male"
            // Toast.makeText(this, "Male selected", Toast.LENGTH_SHORT).show(); // Show a Toast when Male is selected
        });

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedGender.isEmpty()) {
                saveGender(); // Save the selected gender
                retrieveAndShowGender(); // Retrieve and show the gender for verification
                Intent intent = new Intent(Gender.this, height.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(Gender.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;
        circularProgressBar.setProgress(progress);
    }

    // Function to save the selected gender in SharedPreferences
    private void saveGender() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("gender", selectedGender); // Save the selected gender
        editor.apply(); // Apply the changes asynchronously
    }

    // Function to retrieve and show the saved gender in a Toast for verification
   private void retrieveAndShowGender() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedGender = sharedPreferences.getString("gender", "No gender selected");

        //Toast.makeText(this, "Gender: " + savedGender, Toast.LENGTH_SHORT).show();
    }
}
