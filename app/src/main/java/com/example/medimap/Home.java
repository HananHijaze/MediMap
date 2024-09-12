package com.example.medimap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.HydrationRoomDao;
import com.example.medimap.roomdb.StepCountRoom;
import com.example.medimap.roomdb.StepCountDao;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.HydrationApi;
import com.example.medimap.server.StepCountApi;
import com.example.medimap.server.UserApi;
import com.google.android.material.button.MaterialButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity implements SensorEventListener {

    // Page components
    private TextView waterOutput,textView3;
    private Button addWaterBtn;
    private GifDrawable waterGif;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    ProgressBar progressBar;
    TextView textView;
    int stepCount = 0;
    int totalSteps = 0;
    int previousTotalSteps = 0;
    TextView percent;

    // Declare DAOs
    private UserDao userDao;
    private HydrationRoomDao hydRoomDao;
    private StepCountDao stepCountRoomDao;  // Correct declaration

    // Rooms
    private UserRoom userRoom;
    private HydrationRoom hydRoom;
    private StepCountRoom stepCountRoom;

    // Servers
    private HydrationApi hydrationApi;
    private UserApi userApi;
    private StepCountApi stepCountApi;

    // Variables
    private int currentWaterAmount = 0;
    private int waterGoal = 0;
    private int defaultWaterAmount = 0;

    private SharedPreferences sharedPreferences;

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
        // Navigation buttons
        MaterialButton leftButton = findViewById(R.id.left);
        leftButton.setOnClickListener(view -> {
            Intent in = new Intent(this, Profile.class);
            startActivity(in);
        });

        MaterialButton center = findViewById(R.id.center);
        center.setOnClickListener(view -> {
            Intent in = new Intent(this, Home.class);
            startActivity(in);
        });
        // Initialize Room database
        AppDatabaseRoom db = AppDatabaseRoom.getInstance(this);  // Initialize database

        // Initialize DAOs
        userDao = db.userDao();
        stepCountRoomDao = db.stepCountDao();  // Initialize stepCountRoomDao here

        // Initialize UI components
        initViews();
        setupSensors();
        resetSteps();
        loadData();

        // Button listeners
        addWaterBtn = findViewById(R.id.addWaterBtn);
        addWaterBtn.setOnClickListener(v -> addWater());

        // ImageView and click listeners
        ImageView stepsImage = findViewById(R.id.stepsImage); // 1. Steps
        LinearLayout steps = findViewById(R.id.steps);
        ImageView imageView = findViewById(R.id.waterbottle); // 2. Water
        LinearLayout water = findViewById(R.id.water);
        ImageButton mealPlanButton = findViewById(R.id.mealPlanButton); // 3. Meal plan
        LinearLayout mealPlan = findViewById(R.id.mealPlan);
        ImageView trainingImage = findViewById(R.id.trainingImage); // 4. Training plan
        LinearLayout training = findViewById(R.id.training);
        ImageButton map = findViewById(R.id.imageButton2); // 5. Map
        textView3= findViewById(R.id.textView3);

        // 1. Steps implementation
        steps.isClickable();
        steps.setOnClickListener(view -> {
            Intent in = new Intent(this, steps_tracking.class);
            startActivity(in);
        });

        // 2. Water implementation
        Glide.with(this)
                .asGif()
                .load(R.drawable.waterbottle2) // Replace with your GIF resource
                .into(imageView);
        water.isClickable();
        water.setOnClickListener(view -> {
            Intent in = new Intent(this, hydration_tracking.class);
            startActivity(in);
        });

        // 3. Meal plan implementation
        mealPlanButton.isClickable();
        mealPlanButton.setOnClickListener(view -> {
            Intent in = new Intent(this, meal_plan.class);
            startActivity(in);
        });

        // 4. Training plan implementation
        Glide.with(this)
                .asGif()
                .load(R.drawable.sports) // GIF
                .into(trainingImage);
        trainingImage.isClickable();
        trainingImage.setOnClickListener(view -> {
            Intent in = new Intent(this, TrainingPlan.class);
            startActivity(in);
        });

        training.isClickable();
        training.setOnClickListener(view -> {
            Intent in = new Intent(this, TrainingPlan.class);
            startActivity(in);
        });

        // 5. Map implementation
        map.isClickable();
        map.setOnClickListener(view -> {
            Intent in = new Intent(this, Map.class);
            startActivity(in);
        });
    }

    /*****************************************Steps*****************************************/
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            totalSteps = (int) event.values[0];
            stepCount = totalSteps - previousTotalSteps;
            updateProgressBar(stepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void updateProgressBar(int stepCount) {
        int maxSteps = 10000;  // Maximum number of steps
        int progress = (stepCount * 100) / maxSteps;
        progressBar.setProgress(progress);
        textView.setText(String.valueOf(stepCount));
        percent.setText(progress + "%");

        // Save step count in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData(); // Save steps data when the activity is paused
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (stepCounterSensor == null) {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        } else {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void resetSteps() {
        textView.setOnClickListener(v -> Toast.makeText(Home.this, "Long tap to reset steps", Toast.LENGTH_SHORT).show());

        textView.setOnLongClickListener(v -> {
            previousTotalSteps = totalSteps;
            totalSteps = 0;
            progressBar.setProgress(0);
            textView.setText("0");
            percent.setText("0%");
            saveData();
            return true;
        });
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key1", String.valueOf(previousTotalSteps));
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        String savedNumber = sharedPreferences.getString("key1", "0");
        previousTotalSteps = Integer.parseInt(savedNumber);
    }

    /**************************************** Water ****************************************/
    private void loadWaterData() {
        // Load previously saved water amount
        this.currentWaterAmount = getSavedWaterAmount();

        this.defaultWaterAmount = 150;
        this.waterGoal = 3000;

        String addWaterTxt = this.defaultWaterAmount + "ml";
        addWaterBtn.setText(addWaterTxt);
        String waterOutputStr = this.currentWaterAmount + " ml";
        waterOutput.setText(waterOutputStr);
    }

    private int getSavedWaterAmount() {
        this.sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        return sharedPreferences.getInt("drank", 0);
    }

    private void addWater() {
        this.currentWaterAmount = this.currentWaterAmount + this.defaultWaterAmount;

        //update water amount in shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("drank",this.currentWaterAmount);
        editor.apply();

        String waterOutputStr = this.currentWaterAmount + " ml";
        waterOutput.setText(waterOutputStr);

        ////////////////////// update animation //////////////////////
    }

    private void fillWaterBottle(){
        if (waterGif != null) {
            int totalFrames = waterGif.getFrameCount();

            // Set frame based on current water amount
            int currentFrame = (this.currentWaterAmount * totalFrames) / waterGoal;
        }
    }

//    //save water data
//    private void scheduleDailyReset() {
//        // Get an instance of AlarmManager
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//        // Set the alarm to start at midnight
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        // Create an Intent to broadcast
//        Intent intent = new Intent(this, StepResetReceiver.class);
//
//        // Create a PendingIntent that will perform a broadcast
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//        // Set the alarm to repeat daily at midnight
//        if (alarmManager != null) {
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                    calendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY,
//                    pendingIntent);
//        }
//    }
}