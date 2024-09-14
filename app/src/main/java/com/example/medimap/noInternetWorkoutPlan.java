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
import com.example.medimap.roomdb.WeeklyTrainingPlanRoom;
import com.example.medimap.roomdb.WeeklyTrainingPlanRoomDao;
import com.example.medimap.roomdb.WorkoutRoom;
import com.example.medimap.roomdb.WorkoutDao;

import java.util.List;
import java.util.ArrayList;


public class noInternetWorkoutPlan extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WorkoutPlanAdapter workoutPlanAdapter;
    private AppDatabaseRoom db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_no_internet_workout_plan);
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

        sunday.setOnClickListener(v -> displayWorkoutsForDay(1)); // Sunday = 1
        monday.setOnClickListener(v -> displayWorkoutsForDay(2)); // Monday = 2
        tuesday.setOnClickListener(v -> displayWorkoutsForDay(3)); // etc.
    }

    private void displayWorkoutsForDay(int dayOfWeek) {
        new Thread(() -> {
            WeeklyTrainingPlanRoomDao workoutPlanDao = db.weeklyTrainingPlanRoomDao();
            List<WeeklyTrainingPlanRoom> allWorkoutPlans = workoutPlanDao.getAllWorkoutPlans();

            // Filter workout plans by day of the week
            List<WeeklyTrainingPlanRoom> filteredWorkoutPlans = new ArrayList<>();
            for (WeeklyTrainingPlanRoom plan : allWorkoutPlans) {
                if (plan.getWorkoutDay() == dayOfWeek) {
                    filteredWorkoutPlans.add(plan);
                }
            }

            // Get workout details for the filtered plans
            List<WorkoutRoom> workoutDetails = new ArrayList<>();
            WorkoutDao workoutDao = db.workoutDao();
            for (WeeklyTrainingPlanRoom plan : filteredWorkoutPlans) {
                WorkoutRoom workout = workoutDao.getWorkoutById(plan.getWorkoutID());
                workoutDetails.add(workout);
            }

            // Update RecyclerView on the main thread
            runOnUiThread(() -> {
                workoutPlanAdapter = new WorkoutPlanAdapter(workoutDetails);
                recyclerView.setAdapter(workoutPlanAdapter);
            });
        }).start();
    }
}