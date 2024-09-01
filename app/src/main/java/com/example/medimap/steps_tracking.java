package com.example.medimap;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class steps_tracking extends AppCompatActivity implements SensorEventListener {

    // Declare variables for SensorManager and the step counter sensor
    private SensorManager mSensorManager = null;
    private Sensor stepSensor;

    // Variables to keep track of the total steps and previous total steps
    private int totalSteps = 0;
    private int previousTotalSteps = 0;

    // UI elements: ProgressBar to show progress and TextView to display the step count
    private ProgressBar progressBar;
    private TextView steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable Edge-to-Edge layout for better UI experience
        EdgeToEdge.enable(this);

        // Set the content view to the activity_steps_tracking layout
        setContentView(R.layout.activity_steps_tracking);

        // Adjust padding based on system bars (status bar, navigation bar) to prevent overlap with UI elements
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the ProgressBar and TextView from the layout
        progressBar = findViewById(R.id.progressBar);
        steps = findViewById(R.id.steps);

        // Initialize SensorManager and get the step counter sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Load the previous step count data from SharedPreferences
        loadData();

        // Set up the functionality to reset steps
        resetSteps();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the sensor listener if the step sensor is available
        if (stepSensor == null) {
            // Show a message if no step sensor is available on the device
            Toast.makeText(this, "No sensor in this device", Toast.LENGTH_SHORT).show();
        } else {
            // Register the listener for step sensor events with normal delay
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener to save battery when the activity is paused
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Check if the event is from the step counter sensor
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            // Get the total steps recorded by the sensor
            totalSteps = (int) event.values[0];

            // Calculate the number of steps taken since the last reset
            int currentSteps = totalSteps - previousTotalSteps;

            // Set the step goal (e.g., 10,000 steps)
            int stepGoal = 10000;

            // Set the maximum value for the progress bar to the step goal
            progressBar.setMax(stepGoal);

            // Update the progress bar to reflect the current step count
            progressBar.setProgress(currentSteps);

            // Update the TextView to display the current step count
            steps.setText(String.valueOf(currentSteps));
        }
    }

    private void resetSteps() {
        // Set a click listener on the steps TextView to inform the user to long press for resetting steps
        steps.setOnClickListener(v ->
                Toast.makeText(steps_tracking.this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        );

        // Set a long-click listener on the steps TextView to reset the step count
        steps.setOnLongClickListener(v -> {
            // Reset the previous total steps to the current total steps
            previousTotalSteps = totalSteps;

            // Reset the TextView and ProgressBar to show 0 steps
            steps.setText("0");
            progressBar.setProgress(0);

            // Save the reset step count to SharedPreferences
            saveData();
            return true;
        });
    }

    private void saveData() {
        // Save the previous total steps to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key1", String.valueOf(previousTotalSteps));
        editor.apply(); // Apply changes to SharedPreferences
    }

    private void loadData() {
        // Load the previously saved step count from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String savedNumber = sharedPreferences.getString("key1", "0");
        previousTotalSteps = Integer.parseInt(savedNumber); // Convert the saved step count to an integer
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed for accuracy changes in step tracking
    }
}
