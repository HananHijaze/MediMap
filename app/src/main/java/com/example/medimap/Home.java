package com.example.medimap;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        ImageButton map =findViewById(R.id.imageButton2);

        ImageView imageView = findViewById(R.id.waterImage);
        LinearLayout water = findViewById(R.id.water);
        ImageButton mealplan = findViewById(R.id.mealplan);


        Glide.with(this)
                .asGif()
                .load(R.drawable.waterbottle2) // Replace with your GIF resource
                .into(imageView);
        ImageView sports= findViewById(R.id.imageView5);
        Glide.with(this)
                .asGif()
                .load(R.drawable.sports) // Replace with your GIF resource
                .into(sports);
        map.isClickable();
        map.setOnClickListener(view -> {
            Intent in = new Intent(this,Map.class);
            startActivity(in);
        });
        water.isClickable();
        water.setOnClickListener(view -> {
            Intent in =new Intent(this,hydration_tracking.class);
            startActivity(in);
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
        /*mealplan.isClickable();
        mealplan.setOnClickListener(view -> {
            Intent in =new Intent(this,MealPlan.class);
            startActivity(in);
        });*/
    }
}