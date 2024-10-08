package com.example.medimap;
import android.Manifest;  // For ACTIVITY_RECOGNITION permission
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;  // For permission handling

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;  // For requesting permissions
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;  // For checking permissions



import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.Converters;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.HydrationRoomDao;
import com.example.medimap.roomdb.StepCountRoom;
import com.example.medimap.roomdb.StepCountDao;
import com.example.medimap.roomdb.TempHydrationRoom;
import com.example.medimap.roomdb.TempHydrationRoomDao;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.Hydration;
import com.example.medimap.server.HydrationApi;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.StepCountApi;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;


import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Home extends AppCompatActivity implements SensorEventListener {

    // Page components
    private TextView nameText;
    //private LinearLayout waterLayout;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    ProgressBar progressBar;
    ProgressBar waterBottleProgress;
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
    private UserApi userApi;
    private StepCountApi stepCountApi;

    // Variables

    private SharedPreferences sharedPreferences;

    /********* Hydration variables *********/
    //Page components
    private TextView waterOutput;
    private Button addWaterBtn;
    private BarChart barChart;
    private ProgressBar waterProgressBar;

    //Daos
    private HydrationRoomDao hydrationRoomDao;
    private TempHydrationRoomDao tempHydrationRoomDao;

    //Room
    private HydrationRoom hydrationRoom;
    private TempHydrationRoom tempHydrationRoom;

    //Server
    private User user;
    private Hydration hydration;

    //Servers
    private HydrationApi hydrationApi;
    private Retrofit retrofit;

    //variables
    private double currentWaterAmount = 0;
    private int waterGoal = 0;
    private int defaultWaterAmount = 0;
    private List<HydrationRoom> allHydrations;
    //    private List<TempHydrationRoom> allTempHydrations;
    private LocalDate prevDate;
    private boolean connected = false;

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

        System.out.println("CREATED HOME");

        System.out.println("DATE TODAY IS: "+LocalDate.now());

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


        // ImageView and click listeners
        ImageView stepsImage = findViewById(R.id.stepsImage); // 1. Steps
        LinearLayout steps = findViewById(R.id.steps);
        waterProgressBar = findViewById(R.id.waterProgress); // 2. Water
        LinearLayout water = findViewById(R.id.water);
        ImageButton mealPlanButton = findViewById(R.id.mealPlanButton); // 3. Meal plan
        LinearLayout mealPlan = findViewById(R.id.mealPlan);
        ImageView trainingImage = findViewById(R.id.trainingImage); // 4. Training plan
        LinearLayout training = findViewById(R.id.training);
        ImageButton map = findViewById(R.id.imageButton2); // 5. Map
        nameText= findViewById(R.id.nameText);

        // 1. Steps implementation
        steps.isClickable();
        steps.setOnClickListener(view -> {
            Intent in = new Intent(this, steps_tracking.class);
            startActivity(in);
        });

        // 2. Water implementation
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


        System.out.println("GOING TO CREATE HYDRATION SECTION");
        //create hydration section
        createHydrationTrackingPage();
    }

    /***************************************** Steps *****************************************/
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

        //createHydrationTrackingPage();
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
                });

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Show the dialog
        dialog.show();

        // Change the button text colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(getResources().getColor(R.color.blue));
        }
        if (negativeButton != null) {
            negativeButton.setTextColor(getResources().getColor(R.color.blue));
        }
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



    /********************************************************* Hydration *********************************************************/
    private void createHydrationTrackingPage(){

        //water output text
        waterOutput = findViewById(R.id.waterOutput);

        //buttons
        addWaterBtn = findViewById(R.id.addWaterBtn);

        //progress bottle
        waterProgressBar = findViewById(R.id.waterProgress);
        waterProgressBar.setMax(100);

        //barChart
        barChart = findViewById(R.id.barChart);

        //check network and server connection
        this.connected = CheckConnection();

        if(connected){
            //get server components
            retrofit = RetrofitClient.getRetrofitInstance();
            hydrationApi = retrofit.create(HydrationApi.class);
            userApi = retrofit.create(UserApi.class);
        }

        //set Daos
        this.userDao = AppDatabaseRoom.getInstance(this).userDao();
        this.hydrationRoomDao = AppDatabaseRoom.getInstance(this).hydrationRoomDao();
        this.tempHydrationRoomDao = AppDatabaseRoom.getInstance(this).tempHydrationRoomDao();

        /***************** DELETING HYDRATION *********************/
//        new Thread(() -> {
//            this.hydrationRoomDao.deleteAllHydrations();
//            this.tempHydrationRoomDao.deleteAllTempHydration();
//        }).start();

        // Fetch the single user from local
        getUserRoomTh();

        if(this.userRoom == null) {
            System.out.println("USER ROOM IS NULL (OnCreate)");
            Toast.makeText(this, "NO USER WAS FOUND", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            System.out.println("USER ROOM IS NOT NULL (OnCreate)");
            String greeting = "Hello, "+this.userRoom.getName();
            nameText.setText(greeting);
        }

        // Fetch hydration data from local
        getNewestHydrationFromRoom();
        System.out.println("GOT NEWEST HYDRATION: "+this.hydrationRoom.getDate());
        if(this.hydrationRoom != null) {
            getNewestTempHydrationFromRoom(this.hydrationRoom);
            System.out.println("GOT NEWEST TEMP HYDRATION: "+this.tempHydrationRoom.getDate());
        }
        else{
            System.out.println("HYDRATION ROOM IS NULL (OnCreate)");
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set date for daily reset
        this.prevDate = this.hydrationRoom.getDate();
        System.out.println("PREV DATE IS: "+this.prevDate);

        // Check Reset
        checkResetData(this.prevDate);

        //load water def, goal and amount
        this.defaultWaterAmount = this.userRoom.getWaterDefault();
        this.waterGoal = this.userRoom.getHydrationGoal();
        System.out.println("WATER GOAL IS: "+this.waterGoal);
        System.out.println("WATER DEFAULT IS: "+this.defaultWaterAmount);

        //load newest hydration
        getNewestHydrationFromRoom();
        if(this.hydrationRoom != null) {
            getNewestTempHydrationFromRoom(this.hydrationRoom);
            System.out.println("GOT NEWEST TEMP HYDRATION: " + this.tempHydrationRoom.getDate());
        }
        else {
            System.out.println("HYDRATION ROOM IS NULL (OnCreate)");
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            finish();
        }

        this.currentWaterAmount = this.hydrationRoom.getDrank();
        System.out.println("CURRENT WATER AMOUNT IS: " + this.currentWaterAmount);

        //update visuals
        String defaultWaterTxt = this.defaultWaterAmount + "ml";
        addWaterBtn.setText(defaultWaterTxt);

        updateWaterProgress(0,(float) this.currentWaterAmount);

        String waterOutputStr = (int) this.currentWaterAmount+"ml";
        waterOutput.setText(waterOutputStr);


        //add example data
        //addExampleHydrationsToRoom();


        //listeners for buttons
        addWaterBtn.setOnClickListener(v -> addWater());
    }

    /**************************************** Getters And Setters ****************************************/
    //getters and setters
    private void setThisUser(User user){synchronized (this){this.user = user;}}

    private User getThisUser(){synchronized (this){return this.user;}}

    private UserRoom getThisUserRoom() {synchronized (this){return this.userRoom;}}

    private void setThisUserRoom(UserRoom userRoom) {synchronized (this){this.userRoom = userRoom;}}

    private void setThisHydrationRoom(HydrationRoom hydrationRoom) {synchronized (this){this.hydrationRoom= hydrationRoom;}}

    private HydrationRoom getThisHydrationRoom() {synchronized (this){return this.hydrationRoom;}}

    private void setThisTempHydrationRoom(TempHydrationRoom tempHydrationRoom) {
        synchronized (this){this.tempHydrationRoom= tempHydrationRoom;}}

    private TempHydrationRoom getThisTempHydrationRoom() {
        synchronized (this){return this.tempHydrationRoom;}}

    private void setAllHydrations(List<HydrationRoom> allHydrations) {
        synchronized (this){this.allHydrations = allHydrations;}
    }

    private List<HydrationRoom> getAllHydrations() {
        synchronized (this){return this.allHydrations;}
    }

    private void updateWaterProgress(float from, float to){
        ProgressBarAnimation anim = new ProgressBarAnimation(this.waterProgressBar,
                from*100/this.waterGoal, to*100/this.waterGoal);
        anim.setDuration(750);
        this.waterProgressBar.startAnimation(anim);
    }

    public void setConnected(boolean connected){this.connected = connected;}

    public boolean getConnected(){return this.connected;}

//    private void setAllTempHydrations(List<TempHydrationRoom> allTempHydrations) {
//        synchronized (this){this.allTempHydrations = allTempHydrations;}
//    }
//
//    private List<TempHydrationRoom> getAllTempHydrations() {
//        synchronized (this){return this.allTempHydrations;}
//    }

    /**************************************** Load The Data ****************************************/
    //check server connection
    private void checkServerConnection() {
        if (NetworkUtils.isNetworkAvailable(this)){

            Thread checkServerThread = new Thread(() -> {

                // Call a lightweight endpoint to check server availability
                Retrofit retrofit = RetrofitClient.getRetrofitInstance();
                UserApi userApi = retrofit.create(UserApi.class);
                Call<Void> call = userApi.pingServer();

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse
                            (@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            setConnected(true);
                            System.out.println("SERVER IS CONNECTED: "+getConnected());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        setConnected(false);
                        System.out.println("CONNECTED TO SERVER: "+getConnected());
                        Toast.makeText(Home.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();

                    }
                });
            });
            checkServerThread.start();

            try {
                //wait for thread to finish
                checkServerThread.join();
            } catch (Exception e) {
                Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
                System.out.println("SOMETHING WENT WRONG WITH THE SERVER CHECK");
                //finish activity and go back to home
                finish();
            }
        }else{
            setConnected(false);
        }
    }

    //gets the local user from server
    private void getUserFromServer(String email){
        // Call the API to get the user by email from the server
        Call<User> call = userApi.findByEmail(email);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    //get user saved in room
                    UserRoom usRoom = getThisUserRoom();

                    //update the user saved in room with the user saved in server
                    Executors.newSingleThreadExecutor().execute(() -> {
                        userDao.updateUser(usRoom);
                        setThisUserRoom(usRoom);
                        setThisUser(user);
                        System.out.println("UPDATED USER IN ROOM FROM SERVER: "+usRoom.getId()+" "+usRoom.getEmail());
                    });
                } else {
                    System.out.println("USER NOT FOUND IN SERVER");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                // Handle failure (e.g., network issues, server not responding)
                Toast.makeText(Home.this, "Failed to load user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean CheckConnection() {
        if (!NetworkUtils.isNetworkAvailable(this))
            return false;
        return NetworkUtils.isServerReachable();
    }

    //get user from room
    private void getUserRoomTh() {
        Thread fetchUserRoomTh = new Thread(() -> {
            //get all users
            UserRoom userRoom = userDao.getFirstUser();
            //check if there is no users
            if (userRoom == null) {
                System.out.println("NO USERS WERE FOUND");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Home.this, "No users were found", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            else {
                //get user from list
                setThisUserRoom(userRoom);
                System.out.println("LOAD DATA: LOADED USER ROOM");
                System.out.println("USER ROOM IS: " + userRoom.getId() + " " + userRoom.getEmail());
            }
        });
        fetchUserRoomTh.start();

        try {
            // Wait for the thread to finish
            fetchUserRoomTh.join();
        } catch (Exception e) {
            System.out.println("EXCEPTION WHILE GETTING USER ROOM");
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            //finish activity and go back to home
            finish();
        }
    }

    private void getAllUserHydrations(Long userId){
        if(this.connected){
            getUserHydrationsFromServer(userId);
        }
        else{
            Thread getHydrationListTh = new Thread(() -> {
                List<HydrationRoom> allHydRoom = this.hydrationRoomDao.getAllHydrationsForCustomer(userId);
                if(allHydRoom ==null)
                    System.out.println("HYD LIST IS NULL (getHydrationListTh)");
                setAllHydrations(allHydRoom);
            });
            getHydrationListTh.start();

            try {
                //wait for thread to finish
                getHydrationListTh.join();
            } catch (Exception e) {
                Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
                System.out.println("EXCEPTION WHILE GETTING USER "+userId+" HYD LIST");
                //finish activity and go back to home
                finish();
            }
        }
        return;
    }

    private List<HydrationRoom> convertHydToHydRoom(List<Hydration> allHyd){
        List<HydrationRoom> allHydRoom = new ArrayList<HydrationRoom>();
        for(Hydration hyd : allHyd){
            allHydRoom.add(new HydrationRoom(hyd));
        }
        return allHydRoom;
    }

    private void getUserHydrationsFromServer(Long userId){
        // Call the API to get the user by email from the server
        Call<List<Hydration>> call = hydrationApi.getHydrationsByCustomerId(userId);

        call.enqueue(new Callback<List<Hydration>>() {
            @Override
            public void onResponse(@NonNull Call<List<Hydration>> call, @NonNull Response<List<Hydration>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Hydration> hydrList = response.body();
                    System.out.println("HYDRATION LIST SIZE IN SERVER: " + hydrList.size()+" USERID: "+userId);
                    setAllHydrations(convertHydToHydRoom(hydrList));
                } else {
                    System.out.println("NO HYDRATION WAS FOUND IN SERVER OR SOMETHING WENT WRONG");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Hydration>> call, @NonNull Throwable t) {
                // Handle failure (e.g., network issues, server not responding)
                System.out.println("Failed to load user hydrations: " + t.getMessage());
                Toast.makeText(Home.this, "Failed to load hydrations: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //get latest hydration from room
    private void getNewestHydrationFromRoom(){
        Thread getNewestHydrationTh = new Thread(() -> {
            System.out.println("GET NEWEST HYDRATION THREAD");

            //get newest hydration
            HydrationRoom newestHydration = this.hydrationRoomDao.getNewestHydration();

            //check if there is no users
            if (newestHydration == null) {
                System.out.println("NO HYDRATION WAS FOUND");
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(hydration_tracking.this, "No hydration data found", Toast.LENGTH_SHORT).show();
//                    }
//                });
                System.out.println("GOING TO CREATE A NEW HYDRATION (NULL)");
                createNewHydration(this.userRoom.getId());
                System.out.println("CREATED NEW HYDRATION DATE IS: "+this.hydrationRoom.getDate());
                return;
            }
            else {
                //get user from list
                setThisHydrationRoom(newestHydration);
                System.out.println("LOAD DATA: LOADED HYDRATION ROOM");
                System.out.println("HYDRATION ROOM IS: " + newestHydration.getDate() + " " + newestHydration.getDrank());
            }
        });
        getNewestHydrationTh.start();

        try {
            // Wait for the thread to finish
            getNewestHydrationTh.join();
        } catch (Exception e) {
            Log.e("HYDRATION_TRACKING", "SOMETHING WENT WRONG", e);
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            //finish activity and go back to home
            finish();
        }
    }

    private void getNewestTempHydrationFromRoom(HydrationRoom hydrationRoom){
        Thread getNewestTemptHydrationTh = new Thread(() -> {
            System.out.println("GET NEWEST TEMP HYDRATION THREAD");

            //get newest hydration
            TempHydrationRoom newestTempHydrationRoom = null;
            newestTempHydrationRoom = tempHydrationRoomDao.getTempHydByDate(hydrationRoom.getDate());
            //check if there is no users
            if (newestTempHydrationRoom == null) {
                System.out.println("NO TEMP HYDRATION WAS FOUND");
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(hydration_tracking.this, "No temp hydration data found", Toast.LENGTH_SHORT).show();
//                    }
//                });
                System.out.println("GOING TO CREATE A NEW TEMP HYDRATION (NULL)");
                createNewTempHydration(this.userRoom.getId());
                System.out.println("CREATED NEW TEMP HYDRATION DATE IS: "+this.tempHydrationRoom.getDate());
                return;
            }
            else {
                //get user from list
                setThisTempHydrationRoom(newestTempHydrationRoom);
                System.out.println("LOADED TEMP HYDRATION ROOM");
                System.out.println("NEWEST TEMP HYDRATION ROOM IS: " + newestTempHydrationRoom.getDate() + " " + newestTempHydrationRoom.getDrank());
            }
        });
        getNewestTemptHydrationTh.start();

        try {
            // Wait for the thread to finish
            getNewestTemptHydrationTh.join();
        } catch (Exception e) {
            Log.e("HYDRATION_TRACKING", "SOMETHING WENT WRONG", e);
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            //finish activity and go back to home
            finish();
        }
    }

    private void createNewHydration(Long userId){
        HydrationRoom newHydration = new HydrationRoom(this.userRoom.getId(), 0.0, LocalDate.now());
        Thread createHydrationTh = new Thread(() -> {
            this.hydrationRoomDao.insertHydration(newHydration);
            this.hydrationRoom = newHydration;
        });
        createHydrationTh.start();

        try {
            //wait for thread to finish
            createHydrationTh.join();
        } catch (Exception e) {
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            //finish activity and go back to home
            finish();
        }
    }

    private void createNewTempHydration(Long userId){
        TempHydrationRoom newTempHydration = new TempHydrationRoom(this.userRoom.getId(), 0.0, LocalDate.now());
        System.out.println("CREATED NEW TEMP HYDRATION: "+newTempHydration.getDate()+" "+newTempHydration.getCustomerId());
        Thread createTempHydrationTh = new Thread(() -> {
            this.tempHydrationRoomDao.insertTempHydration(newTempHydration);
            this.tempHydrationRoom = newTempHydration;
        });
        createTempHydrationTh.start();

        try {
            //wait for thread to finish
            createTempHydrationTh.join();
        } catch (Exception e) {
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            //finish activity and go back to home
            finish();
        }
    }

    //creates example hydration for barChart
    private void addExampleHydrationsToRoom() {
        List<HydrationRoom> hydrationList = new ArrayList<>();

        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 1500.0, LocalDate.of(2024, 9, 1)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 2000.0, LocalDate.of(2024, 9, 2)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 1200.0, LocalDate.of(2024, 9, 3)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 2500.0, LocalDate.of(2024, 9, 4)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 1000.0, LocalDate.of(2024, 9, 5)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 2300.0, LocalDate.of(2024, 9, 6)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 1700.0, LocalDate.of(2024, 9, 7)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 2100.0, LocalDate.of(2024, 9, 8)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 4000.0, LocalDate.of(2024, 9, 9)));
        hydrationList.add(new HydrationRoom(this.userRoom.getId(), 3000.0, LocalDate.of(2024, 9, 10)));

        // Iterate through the hydrationList and insert each HydrationRoom
        Long Hid = 1L;
        for (HydrationRoom hydrationRoom : hydrationList) {
            hydrationRoom.setId(Hid++);
            addHydrationToRoom(hydrationRoom);
        }
    }

    //adds a hydration to the room
    private void addHydrationToRoom(HydrationRoom hydrationRoom){
        Thread addHydrationTh = new Thread(() -> {
            this.hydrationRoomDao.insertHydration(hydrationRoom);
        });
        addHydrationTh.start();

        try {
            //wait for thread to finish
            addHydrationTh.join();
        } catch (Exception e) {
            System.out.println("EXCEPTION WHEN ADDING EXAMPLE HYDRATION");
            //finish activity and go back to home
            finish();
        }
    }

    /**************************************** Add Water ****************************************/
    private void addWater() {

        System.out.println("PRESSED ADD WATER BUTTON");
        float prevWaterAmount = (float)this.currentWaterAmount;
        this.currentWaterAmount = this.currentWaterAmount + this.defaultWaterAmount;
        System.out.println("CURRENT WATER AMOUNT IS: " + this.currentWaterAmount);


        this.hydrationRoom.setDrank(this.currentWaterAmount);
        this.tempHydrationRoom.setDrank(this.currentWaterAmount);

        //update hydration in room
        Thread addHydrationTh = new Thread(() -> {
            hydrationRoomDao.updateHydration(this.hydrationRoom);
            tempHydrationRoomDao.updateTempHydration(this.tempHydrationRoom);
        });
        addHydrationTh.start();

        try {
            //wait for thread to finish
            addHydrationTh.join();
        } catch (Exception e) {
            System.out.println("EXCEPTION WHEN ADDING WATER");
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
        }

        String waterOutputStr = (int) this.currentWaterAmount +" ml";
        waterOutput.setText(waterOutputStr);

        //update water bottle
        updateWaterProgress(prevWaterAmount,(float) this.currentWaterAmount);

        if(this.currentWaterAmount >= this.waterGoal) {
            SharedPreferences sharedPreferences = getSharedPreferences("waterPrefs", MODE_PRIVATE);
            boolean ReachedGoalNoti = sharedPreferences.getBoolean("ReachedHydGoalNoti", false);
            System.out.println("REACHED HYDRATION GOAL");
            if (ReachedGoalNoti) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ReachedHydGoalNoti", true);
                editor.apply(); // Apply changes

                Toast.makeText(this, "Hydration goal reached!", Toast.LENGTH_SHORT).show();

                //send notification
                sendNotification();
            }
        }
    }

    private void sendNotification() {
        String channelId = "hydration_goal_channel"; // The same ID used when creating the channel
        String title = "Hydration Goal Reached!";
        String message = "Congratulations! You've reached your daily hydration goal of " + this.waterGoal + " ml.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.congrats) // Add a small icon here (replace with your app's icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this, R.color.blue)) // Optional: Set a color for the notification
                .setAutoCancel(true); // Dismiss notification when clicked

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build()); // You can use a different ID for each notification if needed
        }
    }

    private void createNotificationChannelHyd() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            String channelId = "hydration_goal_channel";
            CharSequence name = "Hydration Goal";
            String description = "Notifications for reaching hydration goal";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**************************************** Save the Data ****************************************/
    private void checkResetData(LocalDate prevDate){
        LocalDate currDate = LocalDate.now();
        if(currDate.isAfter(prevDate)) {
            System.out.println("RESETTING DATA");

            Thread resetThread = new Thread(() -> {

                List<HydrationRoom> allHyd = hydrationRoomDao.getAllHydrationsForCustomer(this.userRoom.getId());
                List<TempHydrationRoom> allTempHyd = tempHydrationRoomDao.getAllTempHydrations();
                if (allHyd == null || allTempHyd == null) {
                    System.out.println("NO HYDRATION WAS IN TABLE");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Home.this, "No hydration data was found", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                } else if (allHyd.size() > 7) {
                    hydrationRoomDao.deleteOldestHydration();
                }
                //getting user from server
                if(this.connected) {

                    getUserFromServer(this.userRoom.getEmail());
                    if (this.user == null) {
                        System.out.println("NO USER FOUND IN SERVER");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Home.this, "user not found in server", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                    else {
                        this.user.setWaterDefault(this.userRoom.getWaterDefault());
                        uploadUserToServer(this.user);
                        if(allTempHyd != null)
                            uploadAllTempHydration(allTempHyd, this.user);
                        this.tempHydrationRoomDao.deleteAllTempHydration();
                    }

                }
                if(this.tempHydrationRoom !=null) {
                    LocalDate today = LocalDate.now();
                    if( !(this.tempHydrationRoom.getDate().equals(today)) ) {
                        createNewTempHydration(this.userRoom.getId());
                    }
                }
                createNewHydration(this.userRoom.getId());
                createNewTempHydration(this.userRoom.getId());
            });
            resetThread.start();

            try {
                // Wait for the thread to finish
                resetThread.join();
            } catch (Exception e) {
                Log.e("HYDRATION_TRACKING", "SOMETHING WENT WRONG", e);
                Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
                //finish activity and go back to home
                finish();
            }
        }
        else{
            return;
        }
    }

    //uploads a user to server
    private void uploadUserToServer(User user) {
        this.userApi.updateUser(user.getId(),user);
    }

    //upload a temp hydration to server
    private void uploadHydrationToServer(Hydration hydration){
        this.hydrationApi.createHydration(hydration);
    }

    //uploads all temp hydration to server
    private void uploadAllTempHydration(List<TempHydrationRoom> allTempHydrations, User user){
        for(TempHydrationRoom tempH : allTempHydrations){
            Hydration hydration = new Hydration(tempH);
            hydration.setCustomerId(user.getId());
            uploadHydrationToServer(hydration);
        }
    }
}