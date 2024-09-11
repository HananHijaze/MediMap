package com.example.medimap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.HydrationRoomDao;
import com.example.medimap.roomdb.StepCountRoom;
import com.example.medimap.roomdb.StepCountRoomDao;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.HydrationApi;
import com.example.medimap.server.StepCount;
import com.example.medimap.server.StepCountApi;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Home extends AppCompatActivity implements SensorEventListener {
    //Page components
    private TextView waterOutput;
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
    AppDatabaseRoom db = AppDatabaseRoom.getInstance(this);



    //Daos
    private UserDao userDao;
    private HydrationRoomDao hydRoomDao;
    private StepCountRoomDao stepCountRoomDao;


    //Rooms
    private UserRoom userRoom;
    private HydrationRoom hydRoom;
    private StepCountRoom stepCountRoom;

    //Servers
    private HydrationApi hydrationApi;
    private UserApi userApi;
    private StepCountApi stepCountApi;

    //variables
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
        //****************************************************
        // Initialize Room database and UserDao
        userDao = db.userDao();
       stepCountRoomDao= db.stepsCountRoomDao();
       stepCountRoomDao = db.stepsCountRoomDao();

        initViews();
        setupSensors();
        resetSteps();
        loadData();


        //***************************************************

        addWaterBtn = findViewById(R.id.addWaterBtn);
        addWaterBtn.setOnClickListener(v -> addWater());


        ImageView stepsImage = findViewById(R.id.stepsImage); //1.steps
        LinearLayout steps = findViewById(R.id.steps);

        ImageView imageView = findViewById(R.id.waterbottle); //2.water
        LinearLayout water = findViewById(R.id.water);

        ImageButton mealPlanButton = findViewById(R.id.mealPlanButton); //3.meal plan
        LinearLayout mealPlan = findViewById(R.id.mealPlan);

        ImageView trainingImage = findViewById(R.id.trainingImage); //4.training plan
        LinearLayout training = findViewById(R.id.training);

        ImageButton map = findViewById(R.id.imageButton2);


        //1.steps implementation
        steps.isClickable();
        steps.setOnClickListener(view -> {
            Intent in = new Intent(this, steps_tracking.class);
            startActivity(in);
        });


        //2.water implementation
        Glide.with(this)
                .asGif()
                .load(R.drawable.waterbottle2) // Replace with your GIF resource
                .into(imageView);

        water.isClickable();
        water.setOnClickListener(view -> {
            Intent in = new Intent(this, hydration_tracking.class);
            startActivity(in);
        });

        //3.meal implementation
        mealPlanButton.isClickable();
        mealPlanButton.setOnClickListener(view -> {
            Intent in = new Intent(this, meal_plan.class);
            startActivity(in);
        });

        //training implementation
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


        //5.map implementation
        map.isClickable(); //map implementation
        map.setOnClickListener(view -> {
            Intent in = new Intent(this, Map.class);
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
            Intent in = new Intent(this, Profile.class);
            startActivity(in);

        });

        //Home implementation
        MaterialButton center = findViewById(R.id.center);
        center.isCheckable();
        center.setOnClickListener(view -> {
            Intent in = new Intent(this, Home.class);
            startActivity(in);
        });

        addWaterBtn = findViewById(R.id.addWaterBtn);
        addWaterBtn.setOnClickListener(v -> addWater());

        ImageView waterImage;
        waterImage = findViewById(R.id.waterbottle);

//        try {
//            waterGif = new GifDrawable(getResources(), R.drawable.waterbottle2);
//            waterImage.setImageDrawable(waterGif);
//
//            // Start the animation
//            waterGif.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        waterOutput = findViewById(R.id.waterOutput);

        //get all Data
        loadWaterData();


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
            // Update the total steps
            totalSteps = (int) event.values[0];

            // Calculate the steps for today
            stepCount = totalSteps - previousTotalSteps;

            // Update the UI to reflect the new step count (progress bar or step count display)
            updateProgressBar(stepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateProgressBar(int stepCount) {
        int maxSteps = 10000; // Maximum number of steps
        int progress = (stepCount * 100) / maxSteps;

        // Update the progress bar and text views
        progressBar.setProgress(progress);
        textView.setText(String.valueOf(stepCount)); // Show actual steps
        percent.setText(progress + "%"); // Show percentage progress

        // Store the stepCount in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("stepPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", stepCount);
        editor.apply(); // Commit the changes asynchronously
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
        if(stepCounterSensor==null){
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }else{
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void resetSteps() {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "Long tap to reset steps", Toast.LENGTH_SHORT).show();
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                // Save the current step count to Room before resetting
//                saveStepCountToRoom();

                previousTotalSteps = totalSteps;  // Reset previous total steps to the current total steps
                totalSteps = 0;  // Reset total steps to 0
                progressBar.setProgress(0);
                textView.setText(String.valueOf(0));  // Set UI text to 0
                percent.setText("0%");
                saveData();  // Save the reset state
                return true;
            }
        });
    }



    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
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


    /**************************************** Water ****************************************/
    //load water data
    private void loadWaterData() {
        // Load previously saved water amount
        this.currentWaterAmount = getSavedWaterAmount();
        System.out.println("WATER DATA: THIS IS THE CURRENT WATER AMOUNT: " + this.currentWaterAmount);

        // Load user room from local database
        //userDao = AppDatabaseRoom.getInstance(this).userDao();

        // Fetch the single user from local
//      userRoom = userDao.getAllUsers().get(0);
//      if(userRoom == null){
//          System.out.println("user not found");
//          Toast.makeText(this, "user not found", Toast.LENGTH_SHORT).show();
//      }
//        get default water amount and goal from user room
//        this.defaultWaterAmount = userRoom.getWaterDefault();
//        this.waterGoal = userRoom.getHydrationGoal();

        this.defaultWaterAmount = 150;/**********************/
        this.waterGoal = 3000;

        //fillWaterBottle();
        String addWaterTxt = this.defaultWaterAmount + "ml";
        addWaterBtn.setText(addWaterTxt);
        String waterOutputStr = this.currentWaterAmount + " ml";
        waterOutput.setText(waterOutputStr);
    }

    private int getSavedWaterAmount(){
        int waterAmount = 0;
        this.sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if(this.sharedPreferences == null){
            System.out.println("DATA NOT FOUND");
            Toast.makeText(this, "data not found", Toast.LENGTH_SHORT).show();
        }
        waterAmount = sharedPreferences.getInt("drank", 0);
        return waterAmount;
    }

    //add water
    private void addWater(){
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