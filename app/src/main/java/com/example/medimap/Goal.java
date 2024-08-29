package com.example.medimap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Button;

public class Goal extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 12;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_goal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Get the current page number passed from the previous activity
        currentPage = getIntent().getIntExtra("currentPage", totalPages); // Default to totalPages (12)

        // Initialize the circular progress bar and update it to 100%
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();

        // Set up the "Finish" button to end the process and navigate to the Home page
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            // Navigate to the Home page
            Intent intent = new Intent(Goal.this, Home.class);  // Replace HomeActivity with the actual class for your home page
            startActivity(intent);
            finish();  // Optionally finish this activity so the user cannot go back to it
        });
    }

    // Function to update the progress bar based on the current page
    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;  // Final page should show 100%
        circularProgressBar.setProgress(progress);
    }
}