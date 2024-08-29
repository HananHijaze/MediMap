package com.example.medimap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class Profile extends AppCompatActivity {
ImageButton settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MaterialButton leftButton = findViewById(R.id.left);
        leftButton.isCheckable();
        leftButton.setOnClickListener(view -> {
            Intent in =new Intent(this,Profile.class);
            startActivity(in);
        });
        MaterialButton center = findViewById(R.id.center);

        center.isCheckable();
        center.setOnClickListener(view -> {
            Intent in =new Intent(this,Home.class);
            startActivity(in);
        });

        ImageButton logout = findViewById(R.id.logout);
        logout.setOnClickListener(view -> {
            Intent in =new Intent(this,LogIn.class);
            startActivity(in);
                });
        settings=findViewById(R.id.settings);
        settings.setOnClickListener(view -> {
            Intent in =new Intent(this,Settings.class);
            startActivity(in);
        });
    }
}