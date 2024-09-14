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

public class DietType extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 11;
    private int currentPage;
    private String selectedDietType = "";
    private static final String PREFS_NAME = "UserSignUpData";

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

        currentPage = getIntent().getIntExtra("currentPage", 6);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        updateProgressBar();

        setupDietOption(findViewById(R.id.button_Vegetarian), "Vegetarian");
        setupDietOption(findViewById(R.id.button_Vegan), "Vegan");
        setupDietOption(findViewById(R.id.button_Keto), "Keto");
        setupDietOption(findViewById(R.id.button_Paleo), "Paleo");
        setupDietOption(findViewById(R.id.button_Balanced), "Balanced");
        setupDietOption(findViewById(R.id.button_LowCarb), "Low Carb");

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedDietType.isEmpty()) {
                saveDietType();
                Intent intent = new Intent(DietType.this, Allergies.class);
                intent.putExtra("currentPage", currentPage + 1);  // Pass the updated page number to the next activity
                startActivity(intent);
            } else {
                Toast.makeText(DietType.this, "Please select your diet type", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProgressBar() {
        int progress = (currentPage * 100) / totalPages;
        circularProgressBar.setProgress(progress);
    }

    private void setupDietOption(LinearLayout layout, String dietType) {
        layout.setOnClickListener(v -> {
            selectedDietType = dietType;
            resetSelections();
            layout.setBackgroundResource(R.drawable.border_selected);
        });
    }

    private void resetSelections() {
        findViewById(R.id.button_Vegetarian).setBackgroundResource(R.drawable.border_unselected);
        findViewById(R.id.button_Vegan).setBackgroundResource(R.drawable.border_unselected);
        findViewById(R.id.button_Keto).setBackgroundResource(R.drawable.border_unselected);
        findViewById(R.id.button_Paleo).setBackgroundResource(R.drawable.border_unselected);
        findViewById(R.id.button_Balanced).setBackgroundResource(R.drawable.border_unselected);
        findViewById(R.id.button_LowCarb).setBackgroundResource(R.drawable.border_unselected);
    }

    private void saveDietType() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dietType", selectedDietType);
        editor.apply();
    }
}
