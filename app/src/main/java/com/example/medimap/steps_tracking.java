package com.example.medimap;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.medimap.server.RetrofitClient;

import com.example.medimap.server.StepCountApi;

import retrofit2.Retrofit;

public class steps_tracking extends AppCompatActivity implements SensorEventListener {
    private StepCountApi stepCountApi;

    // Declare variables for SensorManager and the step counter sensor
    private SensorManager mSensorManager = null;
    private Sensor stepSensor;

    // Variables to keep track of the total steps and previous total steps
    private int totalSteps = 0;
    private int previousTotalSteps = 0;

    // UI elements: ProgressBar to show progress and TextView to display the step count
    private ProgressBar progressBar;
    private TextView steps;
    private TextView goal;
    private Button editGoal;

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
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        stepCountApi = retrofit.create(StepCountApi.class);


        // Initialize the ProgressBar and TextView from the layout
        progressBar = findViewById(R.id.progressBar);
        steps = findViewById(R.id.steps);
        goal = findViewById(R.id.goal);
        editGoal = findViewById(R.id.editGoal);
        editGoal.setOnClickListener(v -> showEditAmountDialog());



        // Initialize SensorManager and get the step counter sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        // Load the previous step count data from SharedPreferences
        loadData();
        scheduleDailyReset();
        // Load and display the saved goal
        loadGoal();

    }

    private void showEditAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        builder.setTitle("New Steps Goal");

        // Set up the layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_goal, null);
        builder.setView(dialogView);

        // Find the EditText
        final EditText input = dialogView.findViewById(R.id.amountInput);

        // Find the buttons in the dialog layout
        Button addButton = dialogView.findViewById(R.id.Save);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle button clicks
        addButton.setOnClickListener(v -> {
            String amount = input.getText().toString();
            if (!amount.isEmpty()) {
                // Update the TextView
                goal.setText("Goal: " + amount);

                // Save the new goal to SharedPreferences
                saveGoal(amount);

                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter goal", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    // Method to save the goal in SharedPreferences
    private void saveGoal(String goal) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("goal", goal);
        editor.apply();
    }
    // Method to load the goal from SharedPreferences
    private void loadGoal() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String savedGoal = sharedPreferences.getString("goal", "6000"); // Default goal is 6000
        goal.setText("Goal: " + savedGoal);
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

            // Update the progress bar to reflect the current step count
            progressBar.setProgress(currentSteps);

            // Update the TextView to display the current step count
            steps.setText(String.valueOf(currentSteps));
        }
    }

    private void scheduleDailyReset() {
        // Get an instance of AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm to start at midnight
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Create an Intent to broadcast
        Intent intent = new Intent(this, StepResetReceiver.class);

        // Create a PendingIntent that will perform a broadcast
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm to repeat daily at midnight
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
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
