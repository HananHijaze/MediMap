package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.UserRoom;

import java.util.Set;

public class build_profile extends AppCompatActivity {

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name
    private AppDatabaseRoom appDatabase; // Room database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_build_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the Room database instance
        appDatabase = AppDatabaseRoom.getInstance(this);

        // Retrieve all user data from SharedPreferences and save to the database
        retrieveAndSaveUserDataToDatabase();

        // Simulate loading for 5 seconds before navigating to the home screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent in = new Intent(this, Home.class);
            startActivity(in);
            finish(); // Optionally finish this activity if you don't want to return to it
        }, 5000);
    }

    // Method to retrieve user data from SharedPreferences and save to the Room database
    private void retrieveAndSaveUserDataToDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve data from SharedPreferences with safe fallback values
        String fullName = sharedPreferences.getString("fullName", "N/A");
        String email = sharedPreferences.getString("email", "N/A");
        String phone = sharedPreferences.getString("phone", "N/A");
        String address = sharedPreferences.getString("address", "N/A");
        String password = sharedPreferences.getString("password", "N/A");
        String gender = sharedPreferences.getString("gender", "N/A");

        // Safely parse height and weight with fallback values
        int height = 0;
        int weight = 0;
        try {
            height = Integer.parseInt(sharedPreferences.getString("height", "0"));
            weight = Integer.parseInt(sharedPreferences.getString("weight", "0"));
        } catch (NumberFormatException e) {
            e.printStackTrace();  // Log and handle the exception
        }

        String bodyType = sharedPreferences.getString("bodyType", "N/A");
        String dietType = sharedPreferences.getString("dietType", "N/A");
        Set<String> allergies = sharedPreferences.getStringSet("allergies", null);
        long birthdateTimestamp = sharedPreferences.getLong("birthdate", -1);
        String meals = sharedPreferences.getString("meals", "0");
        String snacks = sharedPreferences.getString("snacks", "0");
        String workoutPlace = sharedPreferences.getString("workoutPlace", "N/A");
        String workoutTime = sharedPreferences.getString("workoutTime", "N/A");
        Set<String> trainingDays = sharedPreferences.getStringSet("trainingDays", null);
        String goal = sharedPreferences.getString("goal", "N/A");

        // Convert birthdate timestamp to a formatted date
        String birthdate = "N/A";
        if (birthdateTimestamp != -1) {
            java.util.Date date = new java.util.Date(birthdateTimestamp);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            birthdate = sdf.format(date);
        }

        // Safely parse meals and snacks with fallback values
        int mealsPerDay = 0;
        int snacksPerDay = 0;
        try {
            mealsPerDay = Integer.parseInt(meals);
            snacksPerDay = Integer.parseInt(snacks);
        } catch (NumberFormatException e) {
            e.printStackTrace();  // Log and handle the exception
        }

        // Create a new UserRoom object with the retrieved data
        UserRoom newUser = new UserRoom(
                email,
                phone,
                fullName,
                password,
                address,
                gender,
                height,
                weight,
                birthdate,
                bodyType,
                goal,
                10000,  // Step count goal (placeholder, modify as needed)
                3000,   // Hydration goal in mL (placeholder, modify as needed)
                workoutPlace,
                dietType,
                mealsPerDay,  // Meals per day
                snacksPerDay, // Snacks per day
                2000          // Default water intake (placeholder, modify as needed)
        );

        // Insert the user data into the Room database asynchronously
        new Thread(() -> {
            appDatabase.userDao().insertUser(newUser);
        }).start();  // Room operations must be done on a background thread
    }
}
