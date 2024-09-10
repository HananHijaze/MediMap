package com.example.medimap;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.UserDao;

import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppDatabaseRoom db = AppDatabaseRoom.getInstance(this);
        this.userDao = AppDatabaseRoom.getInstance(this).userDao();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createShortcut();
        }
        // Start the AsyncTask to check users in the background
        new CheckUsersTask().execute();
        ImageView imageView = findViewById(R.id.imageView7);

        Glide.with(this)
                .asGif()
                .load(R.drawable.loading)
                .into(imageView);


        /*********************************** ADDING TESTER USER ***********************************/
        // Create a new UserRoom object with the retrieved data
//        System.out.println("GETTING ALL USERS");
//        List<UserRoom> usersList = this.userDao.getAllUsers();
//        UserRoom userRoom = userDao.getUserByEmail("tester@test.com");
//        System.out.println("CHECKING IF TESTING USER EXISTS");
//        //check if user already exists
//        if(userRoom != null){
//            System.out.println("USER ALREADY EXISTS");
//        }
//        else {
//            UserRoom newUser = new UserRoom(
//                    "tester@test.com",
//                    "test test",
//                    "test123",
//                    "Male",
//                    170,
//                    70,
//                    "05/07/2004",
//                    "Skinny",
//                    "Gain Muscle",
//                    6000,  // Step count goal (placeholder, modify as needed)
//                    3000,   // Hydration goal in mL (placeholder, modify as needed)
//                    "Home",
//                    "Keto",
//                    2,  // Meals per day
//                    2, // Snacks per day
//                    200          // Default water intake (placeholder, modify as needed)
//            );
//
//            //add testing user in room
//            new Thread(() -> {
//                userDao.insertUser(newUser);
//            }).start();
//
//            System.out.println("TESTING USER ADDED: " + newUser.toString());
//        }
    }

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
            } else {
                // Shortcut already exists, do nothing or show a message
               // Toast.makeText(this, "Shortcut already exists", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    // AsyncTask to query the users in the background
    private class CheckUsersTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return userDao.getAllUsers().isEmpty();
        }

        @Override
        protected void onPostExecute(Boolean isEmpty) {
            // Delay the action by 5 seconds using a Handler
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isEmpty) {
                    Intent in = new Intent(MainActivity.this, LogIn.class);
                    startActivity(in);
                    finish(); // Optionally finish the MainActivity
                } else {
                    Intent in = new Intent(MainActivity.this, Home.class);
                    startActivity(in);
                    finish(); // Optionally finish the MainActivity
                }
            }, 5000); // 5-second delay
        }
    }
}
