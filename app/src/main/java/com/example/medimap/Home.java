package com.example.medimap;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    ProgressBar progressBar;
    TextView textView;
    int stepCount = 0;
    int totalSteps = 0;
    int previousTotalSteps = 0;
    TextView percent;


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

        //
        initViews();
        setupSensors();
        scheduleMidnightReset();



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
        trainingImage.isClickable();
        trainingImage.setOnClickListener(view -> {
            Intent in =new Intent(this,TrainingPlan.class);
            startActivity(in);
        });


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
            SharedPreferences sharedPreferences = getSharedPreferences("loginprefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userEmail", "Majd");
            editor.apply();
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
    private void initViews() {
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);
        percent = findViewById(R.id.percent);
    }
    private void setupSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    private void scheduleMidnightReset() {
        Intent intent = new Intent(this, StepResetReceiver.class);

        // Create a PendingIntent with FLAG_IMMUTABLE
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the alarm for midnight
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // If it's already past midnight today, schedule for the next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            // Log when the alarm is scheduled for debugging
            Log.d("MidnightReset", "Alarm scheduled for midnight reset");
        }

    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
            long lastResetTime = sharedPreferences.getLong("lastResetTime", -1);

            // Check if the date has changed
            long currentTime = System.currentTimeMillis();
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeInMillis(currentTime);

            Calendar resetCalendar = Calendar.getInstance();
            resetCalendar.setTimeInMillis(lastResetTime);

            if (currentCalendar.get(Calendar.DAY_OF_YEAR) != resetCalendar.get(Calendar.DAY_OF_YEAR)) {
                // Reset the step count for a new day
                previousTotalSteps = totalSteps;
                stepCount = 0;
                Log.d("MidnightReset", "Step count reset to zero");

                // Save the reset time
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong("lastResetTime", currentTime);
                editor.apply();
            }

            // Update the step count
            totalSteps = (int) event.values[0];
            stepCount = totalSteps - previousTotalSteps;
            updateProgressBar(stepCount);
        }
    }


    private void updateProgressBar(int stepCount) {
        int maxSteps = 10000; // Maximum number of steps
        int progress = (stepCount * 100) / maxSteps;
        progressBar.setProgress(progress);
        textView.setText(String.valueOf(stepCount)); // Show actual steps
        percent.setText(progress + "%"); // Show percentage progress

        // Save stepCount to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);
        editor.apply(); // Apply changes
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
