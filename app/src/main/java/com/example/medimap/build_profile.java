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
import com.example.medimap.roomdb.UsersAllergiesRoom;
import com.example.medimap.roomdb.AllergyRoom;
import com.example.medimap.roomdb.UserWeekdayRoom;
import com.example.medimap.roomdb.WeekDaysRoom;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;

import java.util.Set;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class build_profile extends AppCompatActivity {

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name
    private AppDatabaseRoom appDatabase; // Room database instance
    private User user;

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
        createplan();

        // Simulate loading for 5 seconds before navigating to the home screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent in = new Intent(this, Home.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        String gender = sharedPreferences.getString("gender", "N/A").toLowerCase();

        // Safely parse height and weight with fallback values
        int height = 0;
        int weight = 0;
        try {
            height = Integer.parseInt(sharedPreferences.getString("height", "0"));
            weight = Integer.parseInt(sharedPreferences.getString("weight", "0"));
        } catch (NumberFormatException e) {
            e.printStackTrace();  // Log and handle the exception
        }

        String bodyType = sharedPreferences.getString("bodyType", "N/A").toLowerCase();
        String dietType = sharedPreferences.getString("dietType", "N/A").toLowerCase();
        Set<String> allergies = sharedPreferences.getStringSet("allergies", null);
        Set<String> trainingDays = sharedPreferences.getStringSet("trainingDays", null); // Added for training days
        long birthdateTimestamp = sharedPreferences.getLong("birthdate", -1);
        int mealsPerDay = sharedPreferences.getInt("meals", 0); // Retrieve meals as integer
        int snacksPerDay = sharedPreferences.getInt("snacks", 0); // Retrieve snacks as integer
        String workoutPlace = sharedPreferences.getString("workoutPlace", "N/A").toLowerCase();
        String workoutTime = sharedPreferences.getString("workoutTime", "N/A");
        String goal = sharedPreferences.getString("goal", "N/A").toLowerCase();

        // Convert birthdate timestamp to a formatted date
        String birthdate = "N/A";
        if (birthdateTimestamp != -1) {
            java.util.Date date = new java.util.Date(birthdateTimestamp);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            birthdate = sdf.format(date);
        }
        // Default water intake
        int WaterGoal = (int) calculateHydrationGoal(weight);

        // Create a new UserRoom object with the retrieved data
        UserRoom newUser = new UserRoom(
                email,
                fullName,
                password,
                gender,
                height,
                weight,
                birthdate,
                bodyType,
                goal,
                6000,  // Step count goal (placeholder, modify as needed)
                WaterGoal,   // Hydration goal in mL
                workoutPlace,
                dietType,
                mealsPerDay,  // Meals per day
                snacksPerDay, // Snacks per day
                200          // Default water intake (placeholder, modify as needed)
        );

        // Insert the user data into the Room database asynchronously
        new Thread(() -> {
            // Clear the user and allergy tables before inserting new user data
            appDatabase.userDao().deleteAllUsers();
            appDatabase.usersAllergiesRoomDao().deleteAllUsersAllergies(); // Clear allergies table
            appDatabase.userWeekdayRoomDao().deleteAllUserWeekdays(); // Clear user weekday table

            appDatabase.userDao().insertUser(newUser);

            // Query the inserted user using email to get the userId
            UserRoom insertedUser = appDatabase.userDao().getUserByEmail(email);
            if (insertedUser != null) {
                long userId = insertedUser.getId();

                // Save the user's allergies in the database
                if (allergies != null && !allergies.isEmpty()) {
                    // Retrieve all allergies from the AllergyRoom table
                    List<AllergyRoom> allAllergies = appDatabase.allergyDao().getAllAllergies();

                    // Loop through each allergy in SharedPreferences
                    for (String allergyName : allergies) {
                        for (AllergyRoom allergy : allAllergies) {
                            if (allergy.getName().equalsIgnoreCase(allergyName)) {
                                // Create a new UsersAllergiesRoom entry with the retrieved userId
                                UsersAllergiesRoom userAllergy = new UsersAllergiesRoom(userId, allergy.getId());

                                // Insert the user's allergy into the users_allergies_table
                                appDatabase.usersAllergiesRoomDao().insertUsersAllergies(userAllergy);
                            }
                        }
                    }
                }

                // Save the user's training days in the database
                if (trainingDays != null && !trainingDays.isEmpty()) {
                    // Retrieve all weekdays from the WeekDaysRoom table
                    List<WeekDaysRoom> allWeekDays = appDatabase.weekDaysRoomDao().getAllWeekDays();

                    // Loop through each training day in SharedPreferences
                    for (String trainingDay : trainingDays) {
                        for (WeekDaysRoom weekDay : allWeekDays) {
                            if (weekDay.getDayName().equalsIgnoreCase(trainingDay)) {
                                // Create a new UserWeekdayRoom entry with the retrieved userId
                                UserWeekdayRoom userWeekday = new UserWeekdayRoom(userId, weekDay.getId());

                                // Insert the user's training days into the user_weekday_table
                                appDatabase.userWeekdayRoomDao().insertUserWeekday(userWeekday);
                            }
                        }
                    }
                }
            }

        }).start();  // Room operations must be done on a background thread
    }

    // Calculate hydration goal
    public double calculateHydrationGoal(double weight) {
        double hydrationGoal = weight * 0.033 * 1000;  // Convert to milliliters
        return roundToNearest50(hydrationGoal);  // Round to nearest 50 ml
    }

    public double roundToNearest50(double value) {
        return Math.round(value / 50) * 50;  // Round to the nearest 50 ml
    }

    // Create user plan
    public void createplan() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "N/A");

        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

        // Make the API call to get the user by email
        Call<User> call = userApi.getUserById(1L);

        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    getplan(user);
                } else {
                    // Handle the error response
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle the failure
            }
        });
    }

    // Get user plan
    public void getplan(User user) {
        CreatingPlan creatingPlan = CreatingPlan.getInstance();
        creatingPlan.createPlan(this, user);
    }
}
