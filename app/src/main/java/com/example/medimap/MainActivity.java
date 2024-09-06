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

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

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
    }
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void createShortcut() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
            ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "shortcut_example")
                    .setShortLabel(getString(R.string.shortcut_short_label))
                    .setLongLabel(getString(R.string.shortcut_long_label))
                    .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut))
                    .setIntent(new Intent(Intent.ACTION_VIEW,
                            null, this, Home.class))
                    .build();

            shortcutManager.requestPinShortcut(shortcut, null);
        }
    }

}