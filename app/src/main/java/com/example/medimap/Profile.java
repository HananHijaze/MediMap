package com.example.medimap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;

public class Profile extends AppCompatActivity {
    private ImageButton settings;
    private ImageView profileImageView;
    private SeekBar bmiIndicator;
    private TextView bmiLabel;
    private static final int PICK_IMAGE = 1;

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
        leftButton.setOnClickListener(view -> {
            Intent in = new Intent(this, Profile.class);
            startActivity(in);
        });

        MaterialButton center = findViewById(R.id.center);
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

        bmiIndicator.setProgress((int) (bmi * 2));
        bmiIndicator.setEnabled(false);
        bmiLabel.setText("Your BMI: " + String.format("%.2f", bmi));

        SharedPreferences sharedPreferences = getSharedPreferences("loginprefs", MODE_PRIVATE);
        String useremail = sharedPreferences.getString("userEmail", null);
        TextView name = findViewById(R.id.nameTextView);
        name.setText(useremail);

        // Initialize the profileImageView and set click listener to change profile picture
        profileImageView = findViewById(R.id.profileImage);
        profileImageView.setOnClickListener(view -> openGallery());
    }

    private double calculateBMI(double weight, double height) {
        return weight / (height * height);
    }

    private void openGallery() {
        // Intent to open the gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            try {
                // Convert the selected image URI to a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                // Set the Bitmap to the ImageView
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
