

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

        // Retrieve the stepCount from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        int stepCount = sharedPreferences.getInt("stepCount", 0); // Default to 0 if no value found

// Log the step count to check if it's retrieved correctly
        Log.d("StepsTracking", "Step Count Retrieved: " + stepCount);

// Update UI with step count
        updateProgressBar(stepCount);
        BarChart barChart = findViewById(R.id.stepChart);
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1f, 240f)); // Sample data
        entries.add(new BarEntry(2f, 448f)); // Sample data
        entries.add(new BarEntry(3f, 150f)); // Sample data

        BarDataSet dataSet = new BarDataSet(entries, "Steps");
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate(); //
    }


    private void updateProgressBar(int stepCount) {
        int maxSteps = 10000; // Maximum number of steps for the day
        progressBar.setMax(maxSteps);
        progressBar.setProgress(stepCount);
        steps.setText(String.valueOf(stepCount)); // Show the actual step count
    }
}
