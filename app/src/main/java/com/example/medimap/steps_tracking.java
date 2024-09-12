package com.example.medimap;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.StepCountDao;
import com.example.medimap.roomdb.StepCountRoom;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class steps_tracking extends AppCompatActivity {
    ProgressBar progressBar;
    TextView steps;
    com.github.mikephil.charting.charts.BarChart stepChart;
    AppDatabaseRoom db = AppDatabaseRoom.getInstance(this);
    private StepCountDao stepCountRoomDao = db.stepCountDao();
    UserDao userDao = db.userDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_steps_tracking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        steps = findViewById(R.id.steps);
        progressBar = findViewById(R.id.progressBar);
        this.stepChart = findViewById(R.id.stepChart);

        // Retrieve the stored step count from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        int stepCount = sharedPreferences.getInt("stepCount", 0); // Default to 0 if no value found

// Log the step count to check if it's retrieved correctly
        Log.d("StepsTracking", "Step Count Retrieved: " + stepCount);

// Update UI with step count
        updateProgressBar(stepCount);


        //
        loadStepDataIntoChart();
    }

    private void loadStepDataIntoChart() {
        new Thread(() -> {
            // Retrieve data from the database
            Long userId = userDao.getAllUsers().get(0).getId();
            List<StepCountRoom> stepData = stepCountRoomDao.getAllStepCounts(userId);

            // Prepare the entries for the bar chart
            List<BarEntry> entries = new ArrayList<>();

            for (int i = 0; i < stepData.size(); i++) {
                StepCountRoom step = stepData.get(i);
                // Assuming each entry corresponds to a day, for example:
                entries.add(new BarEntry(i + 1, step.getSteps())); // Index as x-axis, step count as y-axis
            }

            // Post the data to the main thread to update the chart
            runOnUiThread(() -> {
                BarChart barChart = findViewById(R.id.stepChart);
                BarDataSet dataSet = new BarDataSet(entries, "Steps");
                BarData barData = new BarData(dataSet);
                barChart.setData(barData);
                barChart.invalidate();  // Refresh the chart with the new data
            });
        }).start();
    }



    private void updateProgressBar(int stepCount) {
        int maxSteps = 10000; // Maximum number of steps for the day
        progressBar.setMax(maxSteps);
        progressBar.setProgress(stepCount);
        steps.setText(String.valueOf(stepCount)); // Show the actual step count
    }
}