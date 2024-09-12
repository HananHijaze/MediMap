package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.google.android.material.textfield.TextInputEditText;

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
            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String email1 = email.getText().toString();
            editor.putString("email_key", email1);
            editor.apply(); // Apply changes asynchronously

            // Run the server reachability check and local login in a background thread
            Executors.newSingleThreadExecutor().execute(() -> {
                boolean serverReachable = isServerReachable();
                runOnUiThread(() -> {
                    if (NetworkUtils.isNetworkAvailable(LogIn.this)) {
                        if (serverReachable) {
                            performLogin(); // Call the server login function
                        } else {
                            performLocalLogin(); // Fallback to local login function
                        }
                    } else {
                        performLocalLogin(); // No network available, call local login
                    }
                });
            });
        });

        // Sign up button listener
        signUp.setOnClickListener(view -> {
            Intent in = new Intent(LogIn.this, Signup.class);
            startActivity(in);
        });
    }

    // Function to check login through server (With internet)
    private void performLogin() {
        String emailIn = email.getText().toString().trim();
        String passwordIn = password.getText().toString().trim();

        // Check that the fields aren't empty
        if (emailIn.isEmpty() || passwordIn.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the API to get the user by email
        Call<User> call = userApi.findByEmail(emailIn);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Check if the password matches
                    if (user.getPassword().equals(passwordIn)) {
                        Toast.makeText(LogIn.this, "Login successful (server)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LogIn.this, Home.class);
                        saveLoginStatus(true);
                        startActivity(intent);

                        // Convert server User to UserRoom
                        UserRoom userRoom = convertToUserRoom(user);

                        // Save the user to the Room database after deleting all existing users
                        Executors.newSingleThreadExecutor().execute(() -> {
                            userDao.deleteAllUsers();  // Delete existing users
                            userDao.insertUser(userRoom);  // Insert new user
                        });

                    } else {
                        Toast.makeText(LogIn.this, "Incorrect email or password (server)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LogIn.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Toast.makeText(LogIn.this, "Failed to connect to server, using local login", Toast.LENGTH_SHORT).show();
                performLocalLogin();  // Fallback to local login if server fails
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
    private UserRoom convertToUserRoom(User user) {
        return new UserRoom(
                user.getEmail(),
                user.getName(),
                user.getPassword(),
                user.getGender(),
                (int) user.getHeight(),  // Assuming height is a double in User and int in UserRoom
                (int) user.getWeight(),  // Assuming weight is a double in User and int in UserRoom
                user.getBirthDate().toString(),  // Assuming you want to save birthDate as a String in Room
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
        System.out.println("LOGIN EMAIL INPUT: " + emailIn);
        System.out.println("LOGIN PASSWORD INPUT: " + passwordIn);

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

    // Function to check if the server is reachable
    private boolean isServerReachable() {
        try {
            // Call a lightweight endpoint to check server availability
            Call<Void> call = userApi.pingServer();
            Response<Void> response = call.execute();

            // If the server responds successfully, return true
            return response.isSuccessful();
        } catch (Exception e) {
            Log.e("Server Check", "Failed to reach server: " + e.getMessage(), e);
            return false;
        }
    }
}
