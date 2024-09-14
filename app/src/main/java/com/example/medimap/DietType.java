package com.example.medimap;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DietType extends AppCompatActivity {
    private ProgressBar circularProgressBar;
    private int totalPages = 11;
    private int currentPage;
    private String selectedDietType = "";
    private ImageView infoIconVegetarian, infoIconVegan, infoIconKeto, infoIconPaleo, infoIconBalanced, infoIconLowCarb;

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
        // Initialize each info icon
        infoIconVegetarian = findViewById(R.id.infoIconVegetarian);
        infoIconVegan = findViewById(R.id.infoIconVegan);
        infoIconKeto = findViewById(R.id.infoIconKeto);
        infoIconPaleo = findViewById(R.id.infoIconPaleo);
        infoIconBalanced = findViewById(R.id.infoIconBalanced);
        infoIconLowCarb = findViewById(R.id.infoIconLowCarb);

        // Set OnClickListener for each diet type info icon
        setDietTypeInfoListeners();
        currentPage = getIntent().getIntExtra("currentPage", 1);
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
    private void setDietTypeInfoListeners() {
        infoIconVegetarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDietInfoDialog("Vegetarian Diet", "A vegetarian diet focuses on plant-based foods and excludes meat, but may include dairy and eggs.");
            }
        });

        infoIconVegan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDietInfoDialog("Vegan Diet", "A vegan diet excludes all animal products, including meat, dairy, eggs, and even honey.");
            }
        });

        infoIconKeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDietInfoDialog("Keto Diet", "The ketogenic diet is a high-fat, low-carb diet that forces the body to burn fats rather than carbohydrates.");
            }
        });

        infoIconPaleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDietInfoDialog("Paleo Diet", "The paleo diet focuses on eating whole foods like meats, fish, fruits, vegetables, nuts, and seeds, avoiding processed foods.");
            }
        });

        infoIconBalanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDietInfoDialog("Balanced Diet", "A balanced diet includes a variety of foods in the right proportions to achieve a healthy intake of nutrients.");
            }
        });

        infoIconLowCarb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDietInfoDialog("Low Carb Diet", "A low-carb diet restricts carbohydrates, such as those found in sugary foods, pasta, and bread, and emphasizes foods high in protein and fat.");
            }
        });
    }
    // Helper method to show a dialog with the diet information
    private void showDietInfoDialog(String dietType, String dietInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(dietType);
        builder.setMessage(dietInfo);

        // Make the dialog cancelable (close when clicked outside)
        builder.setCancelable(true); // Enable canceling the dialog

        AlertDialog dialog = builder.create();

        // Allow the dialog to be canceled when the user touches outside
        dialog.setCanceledOnTouchOutside(true); // Close the dialog if touched outside

        dialog.show();
    }


}
