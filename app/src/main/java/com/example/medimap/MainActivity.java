package com.example.medimap;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class MainActivity extends AppCompatActivity {

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createShortcut();
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent in = new Intent(MainActivity.this, Home.class);
            startActivity(in);
            finish(); // Optionally finish the MainActivity if you don't want to return to it
        }, 5000); ;
        ImageView imageView = findViewById(R.id.imageView7);

        Glide.with(this)
                .asGif()
                .load(R.drawable.loading)
                .into(imageView);


        /*********************************** TESTER USER ***********************************/
        // Create a new UserRoom object with the retrieved data
        UserRoom newUser = new UserRoom(
                "tester@test.com",
                "test test",
                "test123",
                "Male",
                170,
                70,
                "05/07/2004",
                "Skinny",
                "Gain Muscle",
                6000,  // Step count goal (placeholder, modify as needed)
                3000,   // Hydration goal in mL (placeholder, modify as needed)
                "Home",
                "Keto",
                2,  // Meals per day
                2, // Snacks per day
                200          // Default water intake (placeholder, modify as needed)
        );

        //create user Dao
        UserDao userDao = AppDatabaseRoom.getInstance(this).userDao();

        //add hydrationRoom in room
        new Thread(() -> {
            userDao.insertUser(newUser);
        }).start();

        System.out.println("TESTING USER ADDED: " + newUser.toString());
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
}