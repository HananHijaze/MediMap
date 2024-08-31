package com.example.medimap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Profile extends AppCompatActivity {
ImageButton settings;
    private SeekBar bmiIndicator;
    private TextView bmiLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MaterialButton leftButton = findViewById(R.id.left);
        leftButton.isCheckable();
        leftButton.setOnClickListener(view -> {
            Intent in = new Intent(this, Profile.class);
            startActivity(in);
        });
        MaterialButton center = findViewById(R.id.center);

        center.isCheckable();
        center.setOnClickListener(view -> {
            Intent in = new Intent(this, Home.class);
            startActivity(in);
        });

        ImageButton logout = findViewById(R.id.logout);
        logout.setOnClickListener(view -> {
            Intent in = new Intent(this, LogIn.class);
            startActivity(in);
        });
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            Intent in = new Intent(this, Settings.class);
            startActivity(in);
        });

        bmiIndicator = findViewById(R.id.bmi_indicator);
        bmiLabel = findViewById(R.id.bmi_label);

        // Example BMI calculation
        double weight = 82; // in kg
        double height = 1.8; // in meters
        double bmi = calculateBMI(weight, height);

        bmiIndicator.setProgress((int)(bmi*2));
        bmiIndicator.setEnabled(false);
        bmiLabel.setText("Your BMI: "+String.format("%.2f", bmi));

        SharedPreferences sharedPreferences = getSharedPreferences("loginprefs", MODE_PRIVATE);
        String useremail = sharedPreferences.getString("userEmail", null);
        TextView name=findViewById(R.id.nameTextView);
        name.setText("hello "+useremail);
    }

        private double calculateBMI(double weight, double height) {
        return weight / (height * height);
    }

}
