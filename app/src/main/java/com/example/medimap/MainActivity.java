package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.medimap.roomdb.AllergyDao;
import com.example.medimap.roomdb.AllergyRoom;
import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.roomdb.WeekDaysDao;
import com.example.medimap.roomdb.WeekDaysRoom;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private UserDao userDao;
    private AllergyDao allergyDao;
    private WeekDaysDao weekDaysDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set window insets to adjust the layout according to system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AppDatabaseRoom db = AppDatabaseRoom.getInstance(this);
        allergyDao = db.allergyDao();
        weekDaysDao = db.weekDaysRoomDao();
        insertDefaultAllergiesIfNotPresent();
        insertDefaultWeekDaysIfNotPresent();


        // Show loading animation after a delay using a background thread
        ImageView imageView = findViewById(R.id.imageView7);

// Create a new thread to delay the GIF loading

        new Thread(() -> {
            // No delay; GIF will start loading immediately
            runOnUiThread(() -> {
                Glide.with(this)
                        .asGif()
                        .load(R.drawable.loading)
                        .into(imageView);
            });
        }).start();

        // Initialize Room database and UserDao
        this.userDao = db.userDao();

        // Create a shortcut if supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createShortcut();
        }

        // Check the users and navigate accordingly using a background thread
        new Thread(() -> {
            boolean isUserDaoEmpty = userDao.getAllUsers().isEmpty(); // Check if the user DAO is empty
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isUserDaoEmpty) {
                    navigateTo(Signup.class); // Navigate to Signup if no users
                } else if (isUserLoggedIn()) {
                    navigateTo(Home.class); // Navigate to Home if logged in
                } else {
                    navigateTo(LogIn.class); // Navigate to LogIn if not logged in
                }
            }, 5000); // 5-second delay
        }).start(); // Start the thread



        /*********************************** ADDING TESTER USER ***********************************/
        // Remove all existing users in the background
        new Thread(() -> {
            userDao.deleteAllUsers();
            UserRoom newUser = createTestUser();// Create a new test user
            newUser.setId(1L);
            userDao.insertUser(newUser); // Insert the test user
            Log.d("MainActivity", "Test user added: " + newUser.toString());
        }).start();
    }

    // Method to navigate to a different activity
    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        startActivity(intent);
        finish(); // Optionally finish MainActivity
    }

    // Helper method to create a test user
    private UserRoom createTestUser() {
        return new UserRoom(

                "tester@test.com",
                "test test",
                "test123",
                "Male",
                170,
                70,
                "05/07/2004",
                "Normal",
                "Weight loss",
                6000,  // Step count goal
                3000,  // Hydration goal in mL
                "Home",
                "Keto",
                2,  // Meals per day
                2,  // Snacks per day
                200  // Default water intake

        );

    }
    // Check if the user is logged in
    public boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginprefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false); // Default to false if not set
    }

    // Method to create a shortcut if supported (for Android 7.1+)
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void createShortcut() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
            // Check if the shortcut already exists
            boolean shortcutExists = false;
            for (ShortcutInfo pinnedShortcut : shortcutManager.getPinnedShortcuts()) {
                if (pinnedShortcut.getId().equals("shortcut_example")) {
                    shortcutExists = true;
                    break;
                }
            }

            // If the shortcut does not exist, create and pin it
            if (!shortcutExists) {
                ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "shortcut_example")
                        .setShortLabel(getString(R.string.shortcut_short_label))
                        .setLongLabel(getString(R.string.shortcut_long_label))
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut))
                        .setIntent(new Intent(Intent.ACTION_VIEW, null, this, Home.class))
                        .build();

                shortcutManager.requestPinShortcut(shortcut, null);
            }
        }
    }
    //initialize allergie room table
    public void insertDefaultAllergiesIfNotPresent() {
        AsyncTask.execute(() -> {
            // Check if there are already allergies in the table
            if (allergyDao.getAllAllergies().isEmpty()) {
                // Insert default allergies with manually set IDs
                List<AllergyRoom> defaultAllergies = new ArrayList<>();
                defaultAllergies.add(new AllergyRoom(1L, "Dairy"));
                defaultAllergies.add(new AllergyRoom(2L, "Gluten"));
                defaultAllergies.add(new AllergyRoom(3L, "Nuts"));
                defaultAllergies.add(new AllergyRoom(4L, "Seafood"));
                defaultAllergies.add(new AllergyRoom(5L, "Soy"));
                defaultAllergies.add(new AllergyRoom(6L, "Eggs"));
                defaultAllergies.add(new AllergyRoom(7L, "None"));

                // Insert all into the database
               allergyDao.insertAllergies(defaultAllergies);
            }
        });
    }


    //initialize days room table
    public void insertDefaultWeekDaysIfNotPresent() {
        AsyncTask.execute(() -> {
            // Check if there are already weekdays in the table
            if (weekDaysDao.getAllWeekDays().isEmpty()) {
                // Insert default weekdays with manually set IDs
                List<WeekDaysRoom> defaultWeekDays = new ArrayList<>();
                defaultWeekDays.add(new WeekDaysRoom(1L, "Sunday"));
                defaultWeekDays.add(new WeekDaysRoom(2L, "Monday"));
                defaultWeekDays.add(new WeekDaysRoom(3L, "Tuesday"));
                defaultWeekDays.add(new WeekDaysRoom(4L, "Wednesday"));
                defaultWeekDays.add(new WeekDaysRoom(5L, "Thursday"));
                defaultWeekDays.add(new WeekDaysRoom(6L, "Friday"));
                defaultWeekDays.add(new WeekDaysRoom(7L, "Saturday"));

                // Insert all into the database
                weekDaysDao.insertAllWeekDays(defaultWeekDays);
            }
        });
    }

}
