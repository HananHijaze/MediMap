package com.example.medimap;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BodyType extends AppCompatActivity {
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

        // Set up the body type selection listeners
        LinearLayout skinnyLayout = findViewById(R.id.skinny);
        LinearLayout normalLayout = findViewById(R.id.normal);
        LinearLayout heavyLayout = findViewById(R.id.heavy);

        skinnyLayout.setOnClickListener(v -> {
            selectedBodyType = "Skinny"; // Set the selected body type to "Skinny"
            skinnyLayout.setBackgroundResource(R.drawable.border_selected);
            normalLayout.setBackgroundResource(R.drawable.border_unselected);
            heavyLayout.setBackgroundResource(R.drawable.border_unselected);
        });

        normalLayout.setOnClickListener(v -> {
            selectedBodyType = "Normal"; // Set the selected body type to "Normal"
            normalLayout.setBackgroundResource(R.drawable.border_selected);
            skinnyLayout.setBackgroundResource(R.drawable.border_unselected);
            heavyLayout.setBackgroundResource(R.drawable.border_unselected);
        });

        heavyLayout.setOnClickListener(v -> {
            selectedBodyType = "Heavier"; // Set the selected body type to "Heavier"
            heavyLayout.setBackgroundResource(R.drawable.border_selected);
            skinnyLayout.setBackgroundResource(R.drawable.border_unselected);
            normalLayout.setBackgroundResource(R.drawable.border_unselected);
        });

        // Set up the "Next" button to navigate to the next activity
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedBodyType.isEmpty()) {
                saveBodyType(); // Save the selected body type
                Intent intent = new Intent(BodyType.this, DietType.class);
                startActivity(intent);
            } else {
                Toast.makeText(BodyType.this, "Please select your body type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to save the selected body type in SharedPreferences
    private void saveBodyType() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("bodyType", selectedBodyType); // Save the selected body type
        editor.apply(); // Apply the changes asynchronously
    }
}
