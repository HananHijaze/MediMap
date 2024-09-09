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

public class MainActivity extends AppCompatActivity {
    UserDao userDao;




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
        userDao = db.userDao();

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
