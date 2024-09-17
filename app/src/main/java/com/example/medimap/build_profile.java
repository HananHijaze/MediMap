package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.example.medimap.server.UsersAllergies;
import com.example.medimap.server.UserWeekday;
import com.example.medimap.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class build_profile extends AppCompatActivity {

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name
    private AppDatabaseRoom appDatabase; // Room database instance
    private User user;
    private static final String DATE_FORMAT = "MMM d, yyyy hh:mm:ss a";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_build_profile);

        // Handling window insets (system bars like status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Room database
        appDatabase = AppDatabaseRoom.getInstance(this);

        try {
            insertUser(); // Insert user into Room and server
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Simulate loading for 5 seconds before navigating to the home screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent in = new Intent(this, Home.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(in);
            finish(); // Optionally finish this activity if you don't want to return to it
        }, 5000);

//        // Check internet connection before proceeding (preserved as a comment for now)
        if (NetworkUtils.isNetworkAvailable(this)) {
            retrieveAndSaveUserDataToDatabase();
            createplan();
        } else {
            showNoInternetDialog(); // Show dialog if no internet
        }
    }

    // Method to retrieve user data from SharedPreferences and save to Room and server
    private void retrieveAndSaveUserDataToDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "N/A");
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);
        Call<User> call = userApi.findByEmail(email);

        // Asynchronous API call to get user data from the server
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    try {
                        // Create UserRoom object to store user data locally in Room
                        UserRoom newUser = new UserRoom(
                                user.getId(),
                                email,
                                user.getName(),
                                user.getPassword(),
                                user.getGender(),
                                (int) user.getHeight(),
                                (int) user.getWeight(),
                                formatDate(user.getBirthDate()),
                                user.getBodyType(),
                                user.getGoal(),
                                6000,  // Step count goal
                                user.getHydrationgoal(),   // Hydration goal in mL
                                user.getWheretoworkout(),
                                user.getDietType(),
                                user.getMealsperday(),   // Meals per day
                                user.getSnackesperday(),  // Snacks per day
                                150          // Default water intake
                        );

                        // Insert user data into Room database asynchronously
                        Executors.newSingleThreadExecutor().execute(() -> {
                            // Clear and reinsert the user, allergies, and training days in Room
                            appDatabase.userDao().deleteAllUsers();
                            appDatabase.usersAllergiesRoomDao().deleteAllUsersAllergies();
                            appDatabase.userWeekdayRoomDao().deleteAllUserWeekdays();
                            appDatabase.userDao().insertUser(newUser);

                            // Save allergies and training days
                            saveUserAllergies(user.getId(), sharedPreferences.getStringSet("allergies", null));
                            saveUserTrainingDays(user.getId(), sharedPreferences.getStringSet("trainingDays", null));
                        });

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Handle failure to retrieve user data
            }
        });
    }

    // Method to save user allergies in Room and server
    private void saveUserAllergies(long userId, Set<String> allergies) {
        if (allergies != null && !allergies.isEmpty()) {
            List<AllergyRoom> allAllergies = appDatabase.allergyDao().getAllAllergies();
            for (String allergyName : allergies) {
                for (AllergyRoom allergy : allAllergies) {
                    if (allergy.getName().equalsIgnoreCase(allergyName)) {
                        // Save allergy to Room
                        UsersAllergiesRoom userAllergy = new UsersAllergiesRoom(userId, allergy.getId());
                        appDatabase.usersAllergiesRoomDao().insertUsersAllergies(userAllergy);

                        // Send allergy to server
                        UsersAllergies usersAllergies = new UsersAllergies(userId, allergy.getId());
                        Service.getInstance().addUsersAllergies(usersAllergies);
                    }
                }
            }
        }
    }

    // Method to save user training days in Room and server
    private void saveUserTrainingDays(long userId, Set<String> trainingDays) {
        if (trainingDays != null && !trainingDays.isEmpty()) {
            List<WeekDaysRoom> allWeekDays = appDatabase.weekDaysRoomDao().getAllWeekDays();
            for (String trainingDay : trainingDays) {
                for (WeekDaysRoom weekDay : allWeekDays) {
                    if (weekDay.getDayName().equalsIgnoreCase(trainingDay)) {
                        // Save training day to Room
                        UserWeekdayRoom userWeekday = new UserWeekdayRoom(userId, weekDay.getId());
                        appDatabase.userWeekdayRoomDao().insertUserWeekday(userWeekday);

                        // Send training day to server
                        UserWeekday userWeekdayServer = new UserWeekday(userId, weekDay.getId());
                        Service.getInstance().addUserWeekday(userWeekdayServer);
                    }
                }
            }
        }
    }

    // Calculate hydration goal based on weight
    public double calculateHydrationGoal(double weight) {
        double hydrationGoal = weight * 0.033 * 1000;  // Convert to milliliters
        return roundToNearest50(hydrationGoal);  // Round to nearest 50 ml
    }

    public double roundToNearest50(double value) {
        return Math.round(value / 50) * 50;  // Round to the nearest 50 ml
    }

    // Show a no internet connection dialog
    private void showNoInternetDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button btnOk = dialogView.findViewById(R.id.Save);
        btnOk.setOnClickListener(v -> dialog.dismiss());
    }

    // Insert user into Room and send to server
    private void insertUser() throws ParseException {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "N/A");
        String email = sharedPreferences.getString("email", "N/A");
        String password = sharedPreferences.getString("password", "N/A");
        String gender = sharedPreferences.getString("gender", "N/A").toLowerCase();
        int height = Integer.parseInt(sharedPreferences.getString("height", "0"));
        int weight = Integer.parseInt(sharedPreferences.getString("weight", "0"));
        String bodyType = sharedPreferences.getString("bodyType", "N/A").toLowerCase();
        String dietType = sharedPreferences.getString("dietType", "N/A").toLowerCase();
        Set<String> allergies = sharedPreferences.getStringSet("allergies", null);
        Set<String> trainingDays = sharedPreferences.getStringSet("trainingDays", null);
        long birthdate = sharedPreferences.getLong("birthdate", -1);
        int mealsPerDay = sharedPreferences.getInt("meals", 0);
        int snacksPerDay = sharedPreferences.getInt("snacks", 0);
        String workoutPlace = sharedPreferences.getString("workoutPlace", "N/A").toLowerCase();
        String goal = sharedPreferences.getString("goal", "N/A").toLowerCase();
        int waterGoal = (int) calculateHydrationGoal(weight);

        // Create a new User object for the server
        User newUser_Server = new User(
                email, fullName, password, gender, height, weight, parseDate(getFormattedDate(birthdate)),
                bodyType, goal, 6000, waterGoal, workoutPlace, dietType, mealsPerDay, snacksPerDay, 150
        );

        // Send user to server
        Service.getInstance().addUser(newUser_Server);
    }

    // Method to parse String to Date
    public Date parseDate(String dateString) throws ParseException {
        return sdf.parse(dateString);
    }

    private String getFormattedDate(Long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return sdf.format(calendar.getTime());
    }

    private String formatDate(Date date) throws ParseException {
        return sdf.format(date);
    }

    // Create user plan and get plan from server
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
                    Toast.makeText(build_profile.this, "Failed to retrieve user plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(build_profile.this, "Error retrieving user plan: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Get user plan
    public void getplan(User user) {
        CreatingPlan creatingPlan = CreatingPlan.getInstance();
        creatingPlan.createPlan(this,user);
    }
}