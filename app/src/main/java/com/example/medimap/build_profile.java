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
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.roomdb.UsersAllergiesRoom;
import com.example.medimap.roomdb.AllergyRoom;
import com.example.medimap.roomdb.UserWeekdayRoom;
import com.example.medimap.roomdb.WeekDaysRoom;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.example.medimap.server.AllergyApi;
import com.example.medimap.server.UsersAllergies;
import com.example.medimap.server.UsersAllergiesApi;
import com.example.medimap.server.UserWeekday;
import com.example.medimap.server.UserWeekdayApi;

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
    private UserDao userDao;

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

        try {
            insertUser();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
//        // Initialize the Room database instance
      appDatabase = AppDatabaseRoom.getInstance(this);
        // Initialize the UserDao
        userDao = appDatabase.userDao();
//
//        // Check internet connection before proceeding
     if (NetworkUtils.isNetworkAvailable(this)) {
         retrieveAndSaveUserDataToDatabase();
        } else {
            showNoInternetDialog(); // Show dialog if no internet
       }      // Simulate loading for 5 seconds before navigating to the home screen
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent in = new Intent(this, Home.class);
           in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           startActivity(in);
          finish(); // Optionally finish this activity if you don't want to return to it
        }, 5000);
        createplan();
    }

    // Method to retrieve user data from SharedPreferences and save to Room and server
    private void retrieveAndSaveUserDataToDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        String email = sharedPreferences.getString("email", "N/A");
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);
        Call<User> call = userApi.findByEmail(email);

        call.enqueue(new Callback<User>() {
                         @Override
                         public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                             if (response.isSuccessful() && response.body() != null) {
                                 User user = response.body();
                                 UserRoom newUser = null;
                                 SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                 SharedPreferences.Editor editor = sharedPreferences.edit();
                                 editor.putLong("userid", user.getId());
                                 editor.apply();
                                 try {
                                     newUser = new UserRoom(
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
                                 } catch (ParseException e) {
                                     throw new RuntimeException(e);
                                 }
                                 //newUser.setId(user.getId());
                                 // Insert the user data into the Room database asynchronously
                                 //UserRoom finalNewUser = newUser;
                                 UserRoom finalNewUser = newUser;
                                 new Thread(() -> {
                                     // Clear the user, allergies, and training days tables before inserting new user data
                                     appDatabase.userDao().deleteAllUsers();
                                     appDatabase.usersAllergiesRoomDao().deleteAllUsersAllergies();
                                     appDatabase.userWeekdayRoomDao().deleteAllUserWeekdays();

                                     appDatabase.userDao().insertUser(finalNewUser);

                                     // Query the inserted user using email to get the userId
                                     UserRoom insertedUser = appDatabase.userDao().getUserByEmail(email);
                                     if (insertedUser != null) {
                                         long userId = insertedUser.getId();

//                            // Save allergies to the Room and server
//                            saveUserAllergies(userId, allergies);
//
//                            // Save training days to the Room and server
//                            saveUserTrainingDays(userId, trainingDays);
                                     }

                                 }).start();  // Room operations must be done on a background thread
                             }
                         }

                         @Override
                         public void onFailure(Call<User> call, Throwable t) {

                         }
                     });
    }

            // Method to save user allergies
            private void saveUserAllergies(long userId, Set<String> allergies) {
                if (allergies != null && !allergies.isEmpty()) {
                    List<AllergyRoom> allAllergies = appDatabase.allergyDao().getAllAllergies();
                    for (String allergyName : allergies) {
                        for (AllergyRoom allergy : allAllergies) {
                            if (allergy.getName().equalsIgnoreCase(allergyName)) {
                                UsersAllergiesRoom userAllergy = new UsersAllergiesRoom(userId, allergy.getId());
                                appDatabase.usersAllergiesRoomDao().insertUsersAllergies(userAllergy);

                                // Send allergies to the server
                                UsersAllergiesApi usersAllergiesApi = RetrofitClient.getRetrofitInstance().create(UsersAllergiesApi.class);
                                UsersAllergies usersAllergies = new UsersAllergies(userId, allergy.getId());
                                System.out.println(usersAllergies.getAllergyId() + " " + usersAllergies.getUserId());
                                usersAllergiesApi.createUsersAllergies(usersAllergies).enqueue(new Callback<UsersAllergies>() {
                                    @Override
                                    public void onResponse(Call<UsersAllergies> call, Response<UsersAllergies> response) {
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(build_profile.this, "Failed to save allergy to server", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UsersAllergies> call, Throwable t) {
                                        Toast.makeText(build_profile.this, "Error saving allergy to server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                }
            }

            // Method to save user training days
            private void saveUserTrainingDays(long userId, Set<String> trainingDays) {
                if (trainingDays != null && !trainingDays.isEmpty()) {
                    List<WeekDaysRoom> allWeekDays = appDatabase.weekDaysRoomDao().getAllWeekDays();
                    for (String trainingDay : trainingDays) {
                        for (WeekDaysRoom weekDay : allWeekDays) {
                            if (weekDay.getDayName().equalsIgnoreCase(trainingDay)) {
                                UserWeekdayRoom userWeekday = new UserWeekdayRoom(userId, weekDay.getId());
                                appDatabase.userWeekdayRoomDao().insertUserWeekday(userWeekday);

                                // Send training days to the server
                                UserWeekdayApi userWeekdayApi = RetrofitClient.getRetrofitInstance().create(UserWeekdayApi.class);
                                UserWeekday userWeekdayServer = new UserWeekday(userId, weekDay.getId());
                                userWeekdayApi.createUserWeekday(userWeekdayServer).enqueue(new Callback<UserWeekday>() {
                                    @Override
                                    public void onResponse(Call<UserWeekday> call, Response<UserWeekday> response) {
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(build_profile.this, "Failed to save training day to server", Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserWeekday> call, Throwable t) {
                                        Toast.makeText(build_profile.this, "Error saving training day to server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                }
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
                new Thread(() -> {
                    Long id= userDao.getAllUsers().get(0).getId();
                    UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

                    // Make the API call to get the user by email
                    Call<User> call = userApi.getUserById(id);

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
                }).start();
            }


            // Get user plan
            public void getplan(User user) {
                CreatingPlan creatingPlan = CreatingPlan.getInstance();
                creatingPlan.createPlan(this, user);
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

            private void insertUser() throws ParseException {

                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

                // Retrieve data from SharedPreferences with safe fallback values
                String fullName = sharedPreferences.getString("fullName", "N/A");
                String email = sharedPreferences.getString("email", "N/A");
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
                Set<String> trainingDays = sharedPreferences.getStringSet("trainingDays", null);
                long birthdate = sharedPreferences.getLong("birthdate", -1);
                int mealsPerDay = sharedPreferences.getInt("meals", 0);
                int snacksPerDay = sharedPreferences.getInt("snacks", 0);
                String workoutPlace = sharedPreferences.getString("workoutPlace", "N/A").toLowerCase();
                String goal = sharedPreferences.getString("goal", "N/A").toLowerCase();


                int WaterGoal = (int) calculateHydrationGoal(weight);


                // Create a new UserRoom object
                User newUser_Server = new User(
                        email,
                        fullName,
                        password,
                        gender,
                        height,
                        weight,
                        parseDate(getFormattedDate(birthdate)),
                        bodyType,
                        goal,
                        6000,  // Step count goal
                        WaterGoal,   // Hydration goal in mL
                        workoutPlace,
                        dietType,
                        mealsPerDay,  // Meals per day
                        snacksPerDay, // Snacks per day
                        150          // Default water intake
                );
                Service.getInstance().addUser(newUser_Server);

            }

            // Method to parse String to Date
            public Date parseDate(String dateString) throws ParseException {
                return sdf.parse(dateString);
            }

            private String getFormattedDate(Long timeInMillis) {
                // Convert the time in milliseconds to a Date object
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timeInMillis);

                // Format the date using the specified DATE_FORMAT
                return sdf.format(calendar.getTime());
            }

            private String formatDate(Date date) throws ParseException {
                return sdf.format(date);
            }

        }

