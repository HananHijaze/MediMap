package com.example.medimap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.HydrationRoomDao;
import com.example.medimap.roomdb.StepCountDao;
import com.example.medimap.roomdb.StepCountRoom;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.roomdb.UserWeekdayDao;
import com.example.medimap.roomdb.UserWeekdayRoom;
import com.example.medimap.roomdb.UsersAllergiesDao;
import com.example.medimap.roomdb.UsersAllergiesRoom;
import com.example.medimap.server.Hydration;
import com.example.medimap.server.HydrationApi;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.StepCount;
import com.example.medimap.server.StepCountApi;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.example.medimap.server.UserWeekday;
import com.example.medimap.server.UserWeekdayApi;
import com.example.medimap.server.UsersAllergies;
import com.example.medimap.server.UsersAllergiesApi;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LogIn extends AppCompatActivity {
    TextView signUp, forgetPass;
    TextInputEditText email, password;
    Button login;
    UserDao userDao;
    private UserApi userApi;
    boolean serverReachable;
    private static final String DATE_FORMAT = "MMM d, yyyy hh:mm:ss a";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        email = findViewById(R.id.emailin);
        password = findViewById(R.id.passwordin);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);

        // Initialize Retrofit instance and create an implementation of the UserApi interface
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        userApi = retrofit.create(UserApi.class);

        // Initialize Room database and UserDao
        AppDatabaseRoom db = AppDatabaseRoom.getInstance(this);
        userDao = db.userDao();

        // Setup login button click listener
        login.setOnClickListener(view -> {
            // Run the login check in a background thread
            Executors.newSingleThreadExecutor().execute(() -> {
                String enteredEmail = email.getText().toString().trim();
                String enteredPassword = password.getText().toString().trim();

                // Check that email and password fields aren't empty
                if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show());
                    return;
                }

                // First, try local login using Room database
                UserRoom localUser = userDao.getUserByEmail(enteredEmail);
                if (localUser != null && localUser.getPassword().equals(enteredPassword)) {
                    // If local login is successful
                    runOnUiThread(() -> {
                        Toast.makeText(LogIn.this, "Login successful (local)", Toast.LENGTH_SHORT).show();
                        saveLoginStatus(true); // Save login status
                        startActivity(new Intent(LogIn.this, Home.class)); // Start Home activity
                    });
                } else {
                    // If local login fails, try the server login
                    performServerLogin(enteredEmail, enteredPassword);
                }

                // Save entered email to SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email_key", enteredEmail);
                editor.apply(); // Apply changes asynchronously
            });
        });

        // Sign up button listener
        signUp.setOnClickListener(view -> {
            Intent in = new Intent(LogIn.this, Signup.class);
            startActivity(in);
        });
    }

    // Function to perform login through the server (With internet)
    private void performServerLogin(String emailIn, String passwordIn) {
        // Check network availability
        if (!NetworkUtils.isNetworkAvailable(LogIn.this)) {
            performLocalLogin(); // If no network, fallback to local login
            return;
        }

        // Call the API to get the user by email
        Call<User> call = userApi.findByEmail(emailIn);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Toast.makeText(LogIn.this, "we are here", Toast.LENGTH_SHORT).show();

                    // Check if the password matches
                    if (user.getPassword().equals(passwordIn)) {
                        Toast.makeText(LogIn.this, "Login successful (server)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LogIn.this, Home.class);
                        saveLoginStatus(true);
                        startActivity(intent);

                        // Convert server User to UserRoom
                        UserRoom userRoom = null;
                        try {
                            userRoom = convertToUserRoom(user);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                        // Save the user to the Room database after deleting all existing users
                        UserRoom finalUserRoom = userRoom;
                        Executors.newSingleThreadExecutor().execute(() -> {
                            userDao.deleteAllUsers();  // Delete existing users
                            userDao.insertUser(finalUserRoom);  // Insert new user
                        });
                        syncHydrationData(LogIn.this, userRoom.getId()); // Sync hydration data
                        syncStepCountData(LogIn.this, userRoom.getId()); // Sync step count data
                        syncAllergiesData(LogIn.this, userRoom.getId()); // Sync allergies data
                        syncWeekdaysData(LogIn.this, userRoom.getId()); // Sync weekdays data

                    } else {
                        Toast.makeText(LogIn.this, "Incorrect email or password (server)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LogIn.this, "User not found (server)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e("Login Error", "Failed to connect to server", t);  // Log the full exception
                Toast.makeText(LogIn.this, "Failed to hhhhhhhhhhhhhhhhhh connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Save login status when the user logs in
    public void saveLoginStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("loginprefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply(); // Apply changes
    }

    private UserRoom convertToUserRoom(User user) throws ParseException {
        return new UserRoom(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                user.getGender(),
                (int) user.getHeight(),  // Assuming height is a double in User and int in UserRoom
                (int) user.getWeight(),  // Assuming weight is a double in User and int in UserRoom
                getFormattedDate(user.getBirthDate().getTime()),
                user.getBodyType(),
                user.getGoal(),
                user.getStepcountgoal(),
                user.getHydrationgoal(),
                user.getWheretoworkout(),
                user.getDietType(),
                user.getMealsperday(),
                user.getSnackesperday(),
                user.getWaterDefault()
        );
    }

    // Function to check login locally using Room database (Without internet)
    private void performLocalLogin() {
        String emailIn = email.getText().toString().trim();
        String passwordIn = password.getText().toString().trim();

        if (emailIn.isEmpty() || passwordIn.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Run database operation in background to avoid locking the UI
        Executors.newSingleThreadExecutor().execute(() -> {
            UserRoom user = userDao.getUserByEmail(emailIn);
            runOnUiThread(() -> {
                if (user != null && user.getPassword().equals(passwordIn)) {
                    Toast.makeText(LogIn.this, "Login successful (local)", Toast.LENGTH_SHORT).show();
                    saveLoginStatus(true);
                    Intent in = new Intent(LogIn.this, Home.class);
                    startActivity(in);
                } else {
                    Toast.makeText(LogIn.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    //filling hydration data from the server to the room
    public void syncHydrationData(Context context, Long customerId) {
        HydrationApi hydrationApi = RetrofitClient.getRetrofitInstance().create(HydrationApi.class);
        Call<List<Hydration>> call = hydrationApi.getLast7DaysHydration(customerId);

        call.enqueue(new Callback<List<Hydration>>() {
            @Override
            public void onResponse(Call<List<Hydration>> call, Response<List<Hydration>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Hydration> hydrationList = response.body();

                    // Insert the data into Room Database
                    AsyncTask.execute(() -> {
                        AppDatabaseRoom db = AppDatabaseRoom.getInstance(context);
                        HydrationRoomDao hydrationRoomDao = db.hydrationRoomDao();

                        // First, delete all the existing hydration records
                        hydrationRoomDao.deleteAllHydrations();

                        // Insert new hydration data
                        for (Hydration hydration : hydrationList) {
                            HydrationRoom hydrationRoom = new HydrationRoom(hydration);
                            hydrationRoomDao.insertHydration(hydrationRoom);
                        }

                        Log.d("Hydration Sync", "Hydration data synced successfully.");
                    });
                } else {
                    Log.e("Hydration Sync", "Failed to sync hydration data from server.");
                }
            }

            @Override
            public void onFailure(Call<List<Hydration>> call, Throwable t) {
                Log.e("Hydration Sync", "API call failed: " + t.getMessage());
            }
        });
    }

    //filling steps data from the server to the room
    public void syncStepCountData(Context context, Long userId) {
        StepCountApi stepCountApi = RetrofitClient.getRetrofitInstance().create(StepCountApi.class);
        Call<List<StepCount>> call = stepCountApi.getLast7DaysSteps(userId);

        call.enqueue(new Callback<List<StepCount>>() {
            @Override
            public void onResponse(Call<List<StepCount>> call, Response<List<StepCount>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<StepCount> stepCountList = response.body();

                    // Insert the data into Room Database
                    AsyncTask.execute(() -> {
                        AppDatabaseRoom db = AppDatabaseRoom.getInstance(context);
                        StepCountDao stepCountDao = db.stepCountDao();

                        // First, delete all the existing step count records for the user
                        stepCountDao.deleteAllStepsForUser(userId);

                        // Insert new step count data
                        for (StepCount stepCount : stepCountList) {
                            StepCountRoom stepCountRoom = new StepCountRoom(
                                    stepCount.getCustomerId(),
                                    stepCount.getSteps(),
                                    stepCount.getDate().toString()
                            );
                            stepCountDao.insertStepCount(stepCountRoom);
                        }

                        Log.d("StepCount Sync", "Step count data synced successfully.");
                    });
                } else {
                    Log.e("StepCount Sync", "Failed to sync step count data from server.");
                }
            }

            @Override
            public void onFailure(Call<List<StepCount>> call, Throwable t) {
                Log.e("StepCount Sync", "API call failed: " + t.getMessage());
            }
        });
    }

    private List<UsersAllergiesRoom> convertServerToRoomAllergies(List<UsersAllergies> serverAllergies) {
        List<UsersAllergiesRoom> roomAllergiesList = new ArrayList<>();
        for (UsersAllergies serverAllergy : serverAllergies) {
            UsersAllergiesRoom roomAllergy = new UsersAllergiesRoom(serverAllergy.getUserId(), serverAllergy.getAllergyId());
            roomAllergiesList.add(roomAllergy);
        }
        return roomAllergiesList;
    }

    public void syncAllergiesData(Context context, Long userId) {
        // Create an instance of UsersAllergiesApi (Retrofit setup assumed to be done)
        UsersAllergiesApi usersAllergiesApi = RetrofitClient.getRetrofitInstance().create(UsersAllergiesApi.class);

        // Fetch allergies data from the server for the given user ID
        Call<List<UsersAllergies>> call = usersAllergiesApi.getAllUsersAllergiesByUserId(userId);

        call.enqueue(new Callback<List<UsersAllergies>>() {
            @Override
            public void onResponse(Call<List<UsersAllergies>> call, Response<List<UsersAllergies>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UsersAllergies> serverAllergies = response.body();

                    // Convert server-side allergies to Room entities
                    List<UsersAllergiesRoom> roomAllergiesList = convertServerToRoomAllergies(serverAllergies);

                    // Insert into Room database
                    AsyncTask.execute(() -> {
                        // Get Room database instance and DAO
                        AppDatabaseRoom db = AppDatabaseRoom.getInstance(context);
                        UsersAllergiesDao usersAllergiesDao = db.usersAllergiesRoomDao();

                        // Delete existing data and insert new data
                        usersAllergiesDao.deleteAllUsersAllergies(); // Clear previous data
                        for (UsersAllergiesRoom usersAllergiesRoom : roomAllergiesList) {
                            usersAllergiesDao.insertUsersAllergies(usersAllergiesRoom);
                        }

                        Log.d("Allergy Sync", "Allergy data synced successfully.");
                    });
                } else {
                    Log.e("Allergy Sync", "Failed to fetch allergies from server.");
                }
            }

            @Override
            public void onFailure(Call<List<UsersAllergies>> call, Throwable t) {
                Log.e("Allergy Sync", "API call failed: " + t.getMessage());
            }
        });
    }

    public void syncWeekdaysData(Context context, Long userId) {
        // Create an instance of UserWeekdayApi (Retrofit setup assumed to be done)
        UserWeekdayApi userWeekdayApi = RetrofitClient.getRetrofitInstance().create(UserWeekdayApi.class);

        // Fetch weekdays data from the server for the given user ID
        Call<List<UserWeekday>> call = userWeekdayApi.getUserWeekdaysByUserId(userId);

        call.enqueue(new Callback<List<UserWeekday>>() {
            @Override
            public void onResponse(Call<List<UserWeekday>> call, Response<List<UserWeekday>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserWeekday> serverWeekdays = response.body();

                    // Convert server-side weekdays to Room entities
                    List<UserWeekdayRoom> roomWeekdaysList = convertServerToRoomWeekdays(serverWeekdays);

                    // Insert into Room database
                    AsyncTask.execute(() -> {
                        // Get Room database instance and DAO
                        AppDatabaseRoom db = AppDatabaseRoom.getInstance(context);
                        UserWeekdayDao userWeekdayDao = db.userWeekdayRoomDao();

                        // Delete existing data and insert new data
                        userWeekdayDao.deleteAllUserWeekdays(); // Clear previous data
                        for (UserWeekdayRoom userWeekdayRoom : roomWeekdaysList) {
                            userWeekdayDao.insertUserWeekday(userWeekdayRoom);
                        }

                        Log.d("Weekday Sync", "Weekday data synced successfully.");
                    });
                } else {
                    Log.e("Weekday Sync", "Failed to fetch weekdays from server.");
                }
            }

            @Override
            public void onFailure(Call<List<UserWeekday>> call, Throwable t) {
                Log.e("Weekday Sync", "API call failed: " + t.getMessage());
            }
        });
    }

    private List<UserWeekdayRoom> convertServerToRoomWeekdays(List<UserWeekday> serverWeekdays) {
        List<UserWeekdayRoom> roomWeekdaysList = new ArrayList<>();
        for (UserWeekday serverWeekday : serverWeekdays) {
            UserWeekdayRoom roomWeekday = new UserWeekdayRoom(serverWeekday.getUserId(), serverWeekday.getWeekdayId());
            roomWeekdaysList.add(roomWeekday);
        }
        return roomWeekdaysList;
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