package com.example.medimap;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
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
        // This is a LinearLayout that works like a button.
        // It uses the "onCustomButtonClick" method as its click handler.
        LinearLayout customButton = findViewById(R.id.customButton);
        customButton.setOnClickListener(this::onCustomButtonClick);


        ImageView stepsImage = findViewById(R.id.stepsImage); //1.steps
        LinearLayout steps = findViewById(R.id.steps);

        ImageView imageView = findViewById(R.id.waterImage); //2.water
        LinearLayout water = findViewById(R.id.water);

        ImageButton mealPlanButton = findViewById(R.id.mealPlanButton); //3.meal plan
        LinearLayout mealPlan = findViewById(R.id.mealPlan);

        ImageView trainingImage = findViewById(R.id.trainingImage); //4.training plan
        LinearLayout training = findViewById(R.id.training);

        ImageButton map =findViewById(R.id.imageButton2);


        //1.steps implementation
        steps.isClickable();
        steps.setOnClickListener(view -> {
            Intent in =new Intent(this,steps_tracking.class);
            startActivity(in);
        });


        //2.water implementation
        Glide.with(this)
                .asGif()
                .load(R.drawable.waterbottle2) // Replace with your GIF resource
                .into(imageView);

        water.isClickable();
        water.setOnClickListener(view -> {
            Intent in =new Intent(this,hydration_tracking.class);
            startActivity(in);
        });

        //3.meal implementation
        mealPlanButton.isClickable();
        mealPlanButton.setOnClickListener(view -> {
            Intent in = new Intent(this,meal_plan.class);
            startActivity(in);
        });

        //training implementation
        Glide.with(this)
                .asGif()
                .load(R.drawable.sports) // GIF
                .into(trainingImage);

        training.isClickable();
        training.setOnClickListener(view -> {
            Intent in =new Intent(this,TrainingPlan.class);
            startActivity(in);
        });


        //5.map implementation
        map.isClickable(); //map implementation
        map.setOnClickListener(view -> {
            Intent in = new Intent(this,Map.class);
            startActivity(in);
        });


        //Profile implementation
        MaterialButton leftButton = findViewById(R.id.left);
        leftButton.isCheckable();
        leftButton.setOnClickListener(view -> {
            Intent in =new Intent(this,Profile.class);
            startActivity(in);
        });

        //Home implementation
        MaterialButton center = findViewById(R.id.center);
        center.isCheckable();
        center.setOnClickListener(view -> {
            Intent in =new Intent(this,Home.class);
            startActivity(in);
        });

    }
    public void onCustomButtonClick(View view) {
        // This is a LinearLayout that works like a button.
        // It uses the "onCustomButtonClick" method as its click handler.
    }

}