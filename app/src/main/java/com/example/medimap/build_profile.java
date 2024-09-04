package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Set;

public class build_profile extends AppCompatActivity {

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

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

        // Retrieve all user data from SharedPreferences
        retrieveAndDisplayUserData();

        // Simulate loading for 5 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent in = new Intent(this, Home.class);
            startActivity(in);
            finish(); // Optionally finish the MainActivity if you don't want to return to it
        }, 5000);
    }

    private void retrieveAndDisplayUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve data
        String fullName = sharedPreferences.getString("fullName", "N/A");
        String email = sharedPreferences.getString("email", "N/A");
        String phone = sharedPreferences.getString("phone", "N/A");
        String address = sharedPreferences.getString("address", "N/A");
        String password = sharedPreferences.getString("password", "N/A");
        String gender = sharedPreferences.getString("gender", "N/A");
        String height = sharedPreferences.getString("height", "N/A");
        String weight = sharedPreferences.getString("weight", "N/A");
        String bodyType = sharedPreferences.getString("bodyType", "N/A");
        String dietType = sharedPreferences.getString("dietType", "N/A");
        Set<String> allergies = sharedPreferences.getStringSet("allergies", null);
        long birthdateTimestamp = sharedPreferences.getLong("birthdate", -1);
        String meals = sharedPreferences.getString("meals", "N/A");
        String snacks = sharedPreferences.getString("snacks", "N/A");
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

        // Display the data in TextViews or other UI components
     /*   TextView profileSummary = findViewById(R.id.profile_summary);
        profileSummary.setText(
                "Full Name: " + fullName + "\n" +
                        "Email: " + email + "\n" +
                        "Phone: " + phone + "\n" +
                        "Address: " + address + "\n" +
                        "Gender: " + gender + "\n" +
                        "Height: " + height + " cm\n" +
                        "Weight: " + weight + " kg\n" +
                        "Body Type: " + bodyType + "\n" +
                        "Diet Type: " + dietType + "\n" +
                        "Allergies: " + (allergies != null ? allergies.toString() : "None") + "\n" +
                        "Birthdate: " + birthdate + "\n" +
                        "Meals: " + meals + "\n" +
                        "Snacks: " + snacks + "\n" +
                        "Workout Place: " + workoutPlace + "\n" +
                        "Workout Time: " + workoutTime + "\n" +
                        "Training Days: " + (trainingDays != null ? trainingDays.toString() : "None") + "\n" +
                        "Goal: " + goal
        );*/
    }
}
