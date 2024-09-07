package com.example.medimap;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.HydrationRoomDao;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.HydrationApi;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Retrofit;

public class hydration_tracking extends AppCompatActivity {

    //Page components
    private TextView waterOutput;
    private Button addWaterBtn;
    private GifDrawable waterGif;

    //Daos
    private UserDao userDao;
    private HydrationRoomDao hydRoomDao;

    //Rooms
    private UserRoom userRoom;
    private HydrationRoom hydRoom;

    //Servers
    private HydrationApi hydrationApi;
    private UserApi userApi;

    //variables
    private int currentWaterAmount = 0;
    private int waterGoal = 0;
    private int defaultWaterAmount = 0;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hydration_tracking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addWaterBtn = findViewById(R.id.addWaterBtn);
        addWaterBtn.setOnClickListener(v -> addWater());

        Button editWaterDefault;
        editWaterDefault = findViewById(R.id.editButton);
        editWaterDefault.setOnClickListener(v -> showEditAmountDialog());

        ImageView waterImage;
        waterImage = findViewById(R.id.waterBottle);

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
        loadData();

        //scheduleDailyReset();
    }

    /**************************************** Load The Data ****************************************/
    private void loadData() {
        // Load previously saved water amount
        this.currentWaterAmount = getSavedWaterAmount();
        System.out.println("LOAD DATA: THIS IS THE CURRENT WATER AMOUNT: " + this.currentWaterAmount);

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
        String waterOutputStr = this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
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

    /**************************************** Add Water ****************************************/
    private void addWater(){
        this.currentWaterAmount = this.currentWaterAmount + this.defaultWaterAmount;

        //update water amount in shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("drank",this.currentWaterAmount);
        editor.apply();

        String waterOutputStr = this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
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

    /**************************************** Edit Default Water Amount ****************************************/
    private void showEditAmountDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            builder.setTitle("Default Water Amount");

            // Set up the layout for the dialog
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_amount, null);
            builder.setView(dialogView);

            // Find the EditText and Spinner in the dialog layout
            final EditText input = dialogView.findViewById(R.id.amountInput);

            // Find the buttons in the dialog layout
            Button SaveBtn = dialogView.findViewById(R.id.Save);
            Button cancelBtn = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle button clicks
        SaveBtn.setOnClickListener(v -> {
            String inputStr = input.getText().toString();
            if (!inputStr.isEmpty()) {
                int inputWaterAmount = Integer.parseInt(inputStr);
                if(inputWaterAmount <= 0){
                    Toast.makeText(this, "please enter a positive number", Toast.LENGTH_SHORT).show();
                    input.setText("");
                }
                else if(inputWaterAmount > this.waterGoal){
                    Toast.makeText(this, "number should be smaller than your goal", Toast.LENGTH_SHORT).show();
                    input.setText("");
                }
                else {
                    this.defaultWaterAmount = inputWaterAmount;/********** update in room*************/
//                    userRoom.setWaterDefault(this.defaultWaterAmount);
//                    userDao.updateUser(userRoom);
                    String addWaterTxt = this.defaultWaterAmount + "ml";
                    addWaterBtn.setText(addWaterTxt);
                    Toast.makeText(this, "new default is: "+this.defaultWaterAmount+"ml", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
            else{
                Toast.makeText(this, "please enter amount", Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    /**************************************** Save the Data ****************************************/
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
}