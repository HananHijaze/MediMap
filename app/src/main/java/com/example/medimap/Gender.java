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

import com.example.medimap.roomdb.UserDao;

public class Gender extends AppCompatActivity {

    private String selectedGender = "";

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
        // Reference the layouts
        LinearLayout femaleLayout = findViewById(R.id.linearLayoutFemale);
        LinearLayout maleLayout = findViewById(R.id.linearLayoutMale);

        // Set up the female layout click listener
        femaleLayout.setOnClickListener(v -> {
            selectedGender = "Female";

            // Set the selected border for female
            femaleLayout.setBackgroundResource(R.drawable.border_selected);
            // Reset the border for male
            maleLayout.setBackgroundResource(R.drawable.border_unselected);
        });

        // Set up the male layout click listener
        maleLayout.setOnClickListener(v -> {
            selectedGender = "Male";

            // Set the selected border for male
            maleLayout.setBackgroundResource(R.drawable.border_selected);
            // Reset the border for female
            femaleLayout.setBackgroundResource(R.drawable.border_unselected);
        });

        // Set up the "Next" button
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> {
            if (!selectedGender.isEmpty()) {
                saveGender();
                Intent intent = new Intent(Gender.this, height.class); // Replace with your next activity
                startActivity(intent);
            } else {
                Toast.makeText(Gender.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveGender() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSignUpData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gender", selectedGender);
        editor.apply();
    }
}
