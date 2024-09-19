package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.StepCountRoom;
import com.example.medimap.roomdb.UserRoom;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class Profile extends AppCompatActivity {
    private ImageButton settings;
    private ImageView profileImageView;
    private SeekBar bmiIndicator;
    private TextView bmiLabel, nameTextView;
    private static final int PICK_IMAGE = 1;
    private AppDatabaseRoom appDatabase;
    private String gender;
    private ProgressBar stepProgressBar, waterProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the database instance before any database operations
        appDatabase = AppDatabaseRoom.getInstance(this);

        // Ensure the database instance is not null
        if (appDatabase == null) {
            Log.e("Profile", "Database instance is null");
            return; // Exit if the database instance failed to initialize
        }

        // Initialize UI components
        profileImageView = findViewById(R.id.profileImage);
        bmiIndicator = findViewById(R.id.bmi_indicator);
        bmiIndicator.setOnTouchListener((v, event) -> true); // Disable touch

        bmiLabel = findViewById(R.id.bmi_label);
        nameTextView = findViewById(R.id.nameTextView);

        // Initialize UI components
        stepProgressBar = findViewById(R.id.stepsbar);
        waterProgressBar = findViewById(R.id.hydrationbar);
        setProgressManually();

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
                    gender = firstUser.getGender();

                    // Set profile picture AFTER gender is fetched
                    setProfilePicture();

                    // Calculate and display BMI
                    int weight = firstUser.getWeight();
                    int height = firstUser.getHeight();
                    double bmi = calculateBMI(weight, height);

                    bmiIndicator.setProgress((int) (bmi * 2)); // Display BMI on SeekBar
                    bmiLabel.setText("Your BMI: " + String.format("%.2f", bmi));

                    // Fetch and display step count for the last 7 days
                  //  fetchAndDisplayStepCount(firstUser.getId());

                  //  fetchAndDisplayWaterGoalAverage(firstUser.getId());
                });
            }
        }).start();
    }

    private double calculateBMI(double weight, double heightInCm) {
        double heightInMeters = heightInCm / 100.0; // Convert height to meters
        return weight / (heightInMeters * heightInMeters);
    }

    private void openGallery() {
        // Intent to open the gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
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

    // Fetch and display average water goal
    private void fetchAndDisplayWaterGoalAverage(Long userId) {
        new Thread(() -> {
            // Fetch all hydration records for the user
            List<HydrationRoom> hydrationRecords = appDatabase.hydrationRoomDao().getAllHydrationsForCustomer(userId);
            UserRoom firstUser = appDatabase.userDao().getUserById(userId);

            if (hydrationRecords != null && !hydrationRecords.isEmpty()) {
                runOnUiThread(() -> {
                    // Calculate the total water intake
                    double totalWaterIntake = hydrationRecords.stream().mapToDouble(HydrationRoom::getDrank).sum();

                    // Calculate the average water intake
                    int numberOfEntries = hydrationRecords.size();
                    double averageWaterIntake = totalWaterIntake / numberOfEntries;

                    // Update the ProgressBar with the total water intake
                    ProgressBar waterProgressBar = findViewById(R.id.hydrationbar);
                    waterProgressBar.setMax(firstUser.getHydrationGoal()); // Set max to user's hydration goal
                    waterProgressBar.setProgress((int) totalWaterIntake); // Set progress to total water intake
                });
            }
        }).start();
    }

    // Set the profile picture based on stored gender
    private void setProfilePicture() {
        if (gender != null) {
            if (gender.equals("male")) {
                profileImageView.setImageResource(R.drawable.mmale);
            } else if (gender.equals("female")) {
                profileImageView.setImageResource(R.drawable.ffemale);
            }
        } else {
            profileImageView.setImageResource(R.drawable.profile_tsofen);
        }
    }
    private void setProgressManually() {
        // Assuming the max value of both progress bars is 100, adjust if needed.
        stepProgressBar.setMax(100);   // Max steps progress (change if different)
        waterProgressBar.setMax(100);  // Max water progress (change if different)

        // Set progress for steps to 55%
        stepProgressBar.setProgress(55);

        // Set progress for hydration to 70%
        waterProgressBar.setProgress(70);
    }
}
