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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Locale;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            // Manually created step data and dates for testing purposes
            List<StepCountRoom> stepData = new ArrayList<>();
            // Retrieve data from the database
            Long userId = userDao.getAllUsers().get(0).getId();

            // Example step data (replace these with your desired manual data)
            stepData.add(new StepCountRoom(userId,5003,"2024-09-10")); // Date format: yyyy-MM-dd
            stepData.add(new StepCountRoom(userId,2668,"2024-09-11"));
            stepData.add(new StepCountRoom(userId,4286,"2024-09-12"));
            stepData.add(new StepCountRoom(userId,6853,"2024-09-13"));
            stepData.add(new StepCountRoom(userId,7002,"2024-09-14"));
            stepData.add(new StepCountRoom(userId,4750,"2024-09-15"));
            stepData.add(new StepCountRoom(userId,4017,"2024-09-16"));


            // Prepare the entries for the bar chart
            List<BarEntry> entries = new ArrayList<>();
            List<String> formattedDates = new ArrayList<>(); // To store formatted dates for x-axis labels

            // Date formatter to format the date as "dd-MM" (day-month)
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM", Locale.getDefault());

            for (int i = 0; i < stepData.size(); i++) {
                StepCountRoom step = stepData.get(i);
                // Use the index as x-axis and the step count as y-axis
                entries.add(new BarEntry(i, step.getSteps()));
                // Format the date without the year (day-month)
                String formattedDate = sdf.format(java.sql.Date.valueOf(step.getDate()));
                formattedDates.add(formattedDate); // Collect formatted dates for x-axis labels
            }

            // Post the data to the main thread to update the chart
            runOnUiThread(() -> {
                BarChart barChart = findViewById(R.id.stepChart);

                // Create the dataset
                BarDataSet dataSet = new BarDataSet(entries, "Steps");
                dataSet.setColor(getResources().getColor(R.color.blue)); // Set bar color
                dataSet.setValueTextColor(getResources().getColor(R.color.black)); // Value text color

                // Customize bar width
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.9f); // Set custom bar width

                // Set the data to the chart
                barChart.setData(barData);
                barChart.setFitBars(true); // Make the bars fit nicely within the chart
                barChart.invalidate();  // Refresh the chart with the new data

                // Customize x-axis
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(formattedDates)); // Display formatted dates (day-month) on x-axis
                xAxis.setGranularity(1f); // Ensure labels are spaced evenly
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position x-axis labels at the bottom

                // Customize y-axis
                YAxis leftAxis = barChart.getAxisLeft();
                leftAxis.setAxisMinimum(0f); // Start y-axis at 0
                leftAxis.setAxisMaximum(12000f); // Set a maximum limit for better visualization (optional)

                // Add goal line (LimitLine) at 10,000 steps
                LimitLine goalLine = new LimitLine(6000f);
                goalLine.setLineWidth(2f); // Set the thickness of the goal line
                goalLine.setLineColor(getResources().getColor(R.color.black)); // Set the color of the goal line
                goalLine.setTextSize(12f); // Set the text size for the label
                goalLine.setTextColor(getResources().getColor(R.color.black)); // Set the text color for the label

                // Add the goal line to the left axis
                leftAxis.addLimitLine(goalLine);

                barChart.getAxisRight().setEnabled(false); // Disable the right y-axis

                // Enable chart scaling and dragging
                barChart.setDragEnabled(true); // Enable dragging
                barChart.setScaleEnabled(true); // Enable scaling
                barChart.setScaleXEnabled(true); // Enable horizontal scaling
                barChart.setPinchZoom(false); // Disable pinch zooming
                barChart.setDoubleTapToZoomEnabled(true); // Enable double-tap zoom

                // Set visible range (number of bars visible at once)
                barChart.setVisibleXRangeMaximum(7); // Show only 7 bars at a time
                barChart.moveViewToX(entries.size() - 7); // Move to the last 7 entries

                // Add animation
                barChart.animateY(1000); // Animate chart on the y-axis for 1 second

                // Customize legend
                Legend legend = barChart.getLegend();
                legend.setTextSize(14f); // Set text size
                legend.setForm(Legend.LegendForm.CIRCLE); // Customize legend form
            });
        }).start();
    }








    private void updateProgressBar(int stepCount) {
        int maxSteps = 6000; // Maximum number of steps for the day
        progressBar.setMax(maxSteps);
        progressBar.setProgress(stepCount);
        steps.setText(String.valueOf(stepCount)); // Show the actual step count
    }
}