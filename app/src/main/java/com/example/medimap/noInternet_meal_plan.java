package com.example.medimap;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.MealDao;
import com.example.medimap.roomdb.WeeklyMealPlanRoom;

import java.util.ArrayList;
import java.util.List;

public class noInternet_meal_plan extends AppCompatActivity {


    private RecyclerView recyclerView;
    private MealPlanAdapter mealPlanAdapter;
    private AppDatabaseRoom db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_no_internet_meal_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database
        db = AppDatabaseRoom.getInstance(this);

        // Setup buttons
        setupDayButtons();
    }

    private void setupDayButtons() {
        Button sunday = findViewById(R.id.sunday);
        Button monday = findViewById(R.id.monday);
        Button tuesday = findViewById(R.id.tuesday);
        // Add the rest of the day buttons...

        sunday.setOnClickListener(v -> displayMealsForDay(1)); // Sunday = 1
        monday.setOnClickListener(v -> displayMealsForDay(2)); // Monday = 2
        tuesday.setOnClickListener(v -> displayMealsForDay(3)); // etc.
    }

    private void displayMealsForDay(int dayOfWeek) {

        // Fetch all meal plans from the Room database
        new Thread(() -> {
            List<WeeklyMealPlanRoom> allMealPlans = db.weeklyMealPlanRoomDao().getAllMealPlans();

            // Filter meal plans by day of the week
            List<WeeklyMealPlanRoom> filteredMealPlans = new ArrayList<>();
            for (WeeklyMealPlanRoom mealPlan : allMealPlans) {
                if (mealPlan.getMealDay() == dayOfWeek) {
                    filteredMealPlans.add(mealPlan);
                }
            }

            // Update RecyclerView on the main thread
            runOnUiThread(() -> {
                MealDao mealDao = db.mealDao();
                mealPlanAdapter = new MealPlanAdapter(filteredMealPlans,mealDao);
                recyclerView.setAdapter(mealPlanAdapter);
            });
        }).start();
    }
}