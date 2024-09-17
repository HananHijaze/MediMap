package com.example.medimap;
import android.Manifest;  // For ACTIVITY_RECOGNITION permission
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;  // For permission handling

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;  // For requesting permissions
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;  // For checking permissions



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
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
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
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
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
    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1001;

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
        });/**********************************************************/
        // Initialize Room database
        AppDatabaseRoom db = AppDatabaseRoom.getInstance(this);  // Initialize database

        // Initialize DAOs
        userDao = db.userDao();
        stepCountRoomDao = db.stepCountDao();  // Initialize stepCountRoomDao here

        scheduleMidnightReset();
        // Initialize UI components
        initViews();
        setupSensors();
        loadData();
        resetSteps();
        checkStepSensorPermission();


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
            loadData();  // Load previousTotalSteps from SharedPreferences
            // Log the previous total steps for debugging
            Log.d("StepsTracking", "Previous Total Steps: " + previousTotalSteps);
            Log.d("StepsTracking", "Current Total Steps: " + totalSteps);
            Log.d("StepsTracking", "Step Count: " + stepCount);


            totalSteps = (int) event.values[0];

            // Calculate stepCount based on totalSteps and previousTotalSteps
            stepCount = totalSteps - previousTotalSteps;

            updateProgressBar(stepCount);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    private void updateProgressBar(int stepCount) {
        int maxSteps = 6000;  // Maximum number of steps
        int progress = (stepCount * 100) / maxSteps;
        progressBar.setProgress(progress);
        textView.setText(String.valueOf(stepCount));
        percent.setText(progress + "%");

        // Save step count in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);
        editor.putInt("totalSteps", totalSteps);
        editor.putInt("previousTotalSteps", previousTotalSteps); // Save the previous total steps
        editor.apply();
    }
    private void scheduleMidnightReset() {
        // Create an instance of AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //create calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());


        // Set the alarm to start at midnight every day
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);

        // Intent to trigger the BroadcastReceiver
        Intent intent = new Intent(this, StepResetReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        // Set the alarm to trigger at midnight and repeat daily
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }


    private void resetSteps() {
        textView.setOnClickListener(v ->
                Toast.makeText(Home.this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        );

        textView.setOnLongClickListener(v -> {
            //saveStepCountToRoom();
            previousTotalSteps = totalSteps;  // Reset previous steps to current total// Reset current step count to 0
            progressBar.setProgress(0);
            textView.setText("0");
            percent.setText("0%");
            saveData();  // Save the reset state
            return true;
        });
    }


    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("previousTotalSteps", previousTotalSteps); // Save previous total steps
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        previousTotalSteps = sharedPreferences.getInt("previousTotalSteps", 0); // Load previous total steps
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();  // Save steps data when the activity is paused

        // Save totalSteps in SharedPreferences to be accessed by StepResetReceiver
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("totalSteps", totalSteps);  // Save the current total steps
        editor.apply();

        // Unregister sensor listeners
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        // Release any additional resources
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
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    /***************************************** sensor permission *****************************************/
// Check for permission
    private void checkStepSensorPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Activity Recognition is required on Android 10 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, show a dialog to ask for permission
                showStepSensorPermissionDialog();
            } else {
                // Permission already granted, proceed with step tracking
                setupSensors();
            }
        } else {
            // If Android version is below 10 (Q), no need for this permission
            setupSensors();
        }
    }
    // Show a dialog to explain the need for permission
    private void showStepSensorPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Step Tracking")
                .setMessage("To track your steps, the app needs permission to access the step sensor. Would you like to enable it?")
                .setPositiveButton("Yes", (dialog, which) -> requestStepSensorPermission())
                .setNegativeButton("No", (dialog, which) -> {
                    // User denied permission, handle accordingly
                    Toast.makeText(this, "Permission required for step tracking.", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    // Request the Activity Recognition permission
    private void requestStepSensorPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Handle activity recognition permission (Step sensor)
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted for activity recognition, proceed with step tracking
                setupSensors();
            } else {
                // Permission denied for activity recognition
                Toast.makeText(this, "Step tracking permission denied.", Toast.LENGTH_SHORT).show();
            }
        }

        // Handle notification permission
        if (requestCode == 1002) {  // Request code for notification permission
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the notification
                sendStepGoalNotification();
            } else {
                // Permission denied for notifications
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /***************************************** Notification ********************************************/
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "StepGoalChannel";
            String description = "Channel for Step Goal Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("STEP_GOAL_CHANNEL", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1002);
            } else {
                // Permission already granted, send notification
                sendStepGoalNotification();
            }
        } else {
            // For Android versions below 13, send notification without permission check
            sendStepGoalNotification();
        }
    }

    private void sendStepGoalNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "STEP_GOAL_CHANNEL")
                .setSmallIcon(R.drawable.ic_notification)  // Replace with your notification icon
                .setContentTitle("Step Goal Reached!")
                .setContentText("Congratulations! You've reached your step goal.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, builder.build());

        // Mark the notification as sent for today
        markNotificationAsSent();
    }
    private boolean isNotificationSentToday() {
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        String lastNotificationDate = sharedPreferences.getString("lastNotificationDate", "");  // Retrieve last notification date
        String currentDate = getCurrentDate();  // Get the current date

        return lastNotificationDate.equals(currentDate);  // Return true if it's the same day
    }


    private void markNotificationAsSent() {
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastNotificationDate", getCurrentDate());  // Save today's date
        editor.apply();
    }




    /*************************************************************************************/
//    private void saveStepCountToRoom() {
//        // Run the database operation in a background thread
//        new Thread(() -> {
//            userRoom  = userDao.getFirstUser(); // Fetch the first user from the database
//            if (userRoom != null) { // Ensure the user exists before proceeding
//                Long userId = userRoom.getId();
//                StepCountRoom stepCountRoom = new StepCountRoom(userId, stepCount, getCurrentDate());
//                stepCountRoomDao.insertStepCount(stepCountRoom); // Insert step count into the Room database
//            }
//        }).start();
//    }



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