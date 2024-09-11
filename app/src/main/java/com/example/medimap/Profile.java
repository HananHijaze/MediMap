package com.example.medimap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.StepCountDao;
import com.example.medimap.roomdb.StepCountRoom;
import com.example.medimap.roomdb.UserDao;
import com.google.android.material.button.MaterialButton;
import com.example.medimap.roomdb.UserRoom;
import java.io.IOException;
import java.util.List;

public class Profile extends AppCompatActivity {
    private ImageButton settings;
    private ImageView profileImageView;
    private SeekBar bmiIndicator; // Declare as a class field
    private TextView bmiLabel, nameTextView;
    private static final int PICK_IMAGE = 1;
    private AppDatabaseRoom appDatabase; // Room database instance



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

        appDatabase = AppDatabaseRoom.getInstance(this);


        // Initialize UI components
        bmiIndicator = findViewById(R.id.bmi_indicator);
        bmiIndicator.setOnTouchListener((v, event) -> true); // Disable touch

        bmiLabel = findViewById(R.id.bmi_label);
        nameTextView = findViewById(R.id.nameTextView);
        profileImageView = findViewById(R.id.profileImage);

        // Navigation buttons
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
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            saveLoginStatus(false);
            startActivity(in);
            finish();
        });

        // Settings button
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            Intent in = new Intent(this, Settings.class);
            startActivity(in);
        });

        // Fetch and display data for the first user
        new Thread(() -> {
            UserRoom firstUser = appDatabase.userDao().getFirstUser(); // Fetch first user from database

            if (firstUser != null) {
                runOnUiThread(() -> {
                    // Update UI with user data
                    nameTextView.setText(firstUser.getName());

                    // Calculate and display BMI
                    int weight = firstUser.getWeight();
                    int height = firstUser.getHeight();
                    double bmi = calculateBMI(weight, height);

                    bmiIndicator.setProgress((int) (bmi * 2)); // Display BMI on SeekBar
                    bmiLabel.setText("Your BMI: " + String.format("%.2f", bmi));

                    // Fetch and display step count for the last 7 days
                    fetchAndDisplayStepCount(firstUser.getId());

                    fetchAndDisplayWaterGoalAverage(firstUser.getId());

                });
            }
        }).start();

        // Profile image click listener
        profileImageView.setOnClickListener(view -> openGallery());
    }

    private double calculateBMI(double weight, double heightInCm) {
        double heightInMeters = heightInCm / 100.0;  // Convert height to meters
        return weight / (heightInMeters * heightInMeters);
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

    // Save login status when the user logs in
    public void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginprefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply(); // Apply changes
    }

    // Fetch and display step count for the last 7 days
    private void fetchAndDisplayStepCount(Long userId) {
        new Thread(() -> {
            List<StepCountRoom> stepCounts = appDatabase.stepCountDao().getLast7DaysStepCount(userId);
            UserRoom firstUser = appDatabase.userDao().getUserById(userId);
            if (stepCounts != null && !stepCounts.isEmpty()) {
                runOnUiThread(() -> {
                    // Count the number of lines (entries)
                    int numberOfEntries = stepCounts.size();

                    // Calculate the total steps
                    int totalSteps = stepCounts.stream().mapToInt(StepCountRoom::getSteps).sum();

                    // Calculate the average steps
                    double averageSteps = (double) totalSteps / numberOfEntries;
                    // Update the ProgressBar with total or average steps
                    ProgressBar stepProgressBar = findViewById(R.id.stepsbar);

                    // Set the maximum value for the ProgressBar (for example, 10,000 steps goal)
                    stepProgressBar.setMax(firstUser.getStepCountGoal());

                    // Set the progress value (you can use totalSteps or averageSteps)
                    stepProgressBar.setProgress((int) totalSteps); // Alternatively, use (int) averageSteps

                });
            }
        }).start();
    }
    // Fetch and display the average water intake for a user
    private void fetchAndDisplayWaterGoalAverage(Long userId) {
        new Thread(() -> {
            // Fetch all hydration records for the user
            List<HydrationRoom> hydrationRecords = appDatabase.hydrationRoomDao().getAllHydrationsForCustomer(userId);
            UserRoom firstUser = appDatabase.userDao().getUserById(userId);

            if (hydrationRecords != null && !hydrationRecords.isEmpty()) {
                runOnUiThread(() -> {
                    // Calculate the total water intake
                    int totalWaterIntake = hydrationRecords.stream().mapToInt(HydrationRoom::getWaterIntake).sum();

                    // Calculate the average water intake
                    int numberOfEntries = hydrationRecords.size();
                    double averageWaterIntake = (double) totalWaterIntake / numberOfEntries;

                    // Update the ProgressBar with the total water intake
                    ProgressBar waterProgressBar = findViewById(R.id.hydrationbar);
                    waterProgressBar.setMax(firstUser.getHydrationGoal()); // Set max to user's hydration goal
                    waterProgressBar.setProgress((int) totalWaterIntake);  // Set progress to total water intake


                });
            }
        }).start();
    }

}