package com.example.medimap;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.HydrationRoomDao;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.Hydration;
import com.example.medimap.server.HydrationApi;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Retrofit;

public class hydration_tracking extends AppCompatActivity {

    //Page components
    private TextView waterOutput;
    private Button addWaterBtn;
    private BarChart barChart;
    private ProgressBar hydrationProgressBar;

    //Daos
    private UserDao userDao;

    //Rooms
    private UserRoom userRoom;

    //objects
    private User user;

    //Servers
    private HydrationApi hydrationApi;
    private UserApi userApi;
    private Retrofit retrofit;

    //variables
    private double currentWaterAmount = 0;
    private int waterGoal = 0;
    private int defaultWaterAmount = 0;
    private ArrayList<BarEntry> barEntries;

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

        System.out.println("CREATED HYDRATION TRACKING");

        //server
        retrofit = RetrofitClient.getRetrofitInstance();
        hydrationApi = retrofit.create(HydrationApi.class);
        userApi = retrofit.create(UserApi.class);

        //buttons
        addWaterBtn = findViewById(R.id.addWaterBtn);
        addWaterBtn.setOnClickListener(v -> addWater());
        Button editWaterDefault;
        editWaterDefault = findViewById(R.id.editButton);
        editWaterDefault.setOnClickListener(v -> showEditAmountDialog());

        //barChart
        barChart = findViewById(R.id.barChart);
        this.barEntries = new ArrayList<>();

        waterOutput = findViewById(R.id.waterOutput);

        //get all Data
        loadData();


        //scheduleDailyReset();
    }

    public void onResume() {
        super.onResume();
        System.out.println("ON RESUME");
        loadData();
    }

    /**************************************** Load The Data ****************************************/
    private void loadData() {
        // Load previously saved water amount
        this.currentWaterAmount = getSavedWaterAmount();
        System.out.println("LOAD DATA: THIS IS THE CURRENT WATER AMOUNT: " + this.currentWaterAmount);

        // Load user dao
        userDao = AppDatabaseRoom.getInstance(this).userDao();
        System.out.println("LOAD DATA: LOADED USER DAO");

        // Fetch the single user from local
        fetchUserRoomTh();

        //check user room
        if (userRoom == null) {
            System.out.println("USER NOT FOUND");
            Toast.makeText(this, "user not found", Toast.LENGTH_SHORT).show();
            return;
        }

        user = new User(userRoom);
        System.out.println("USER ID and NAME ARE: "+ this.user.getId() +" " + this.user.getName());
        System.out.println(this.user.toString());

        //get default water amount and goal from user room
        this.defaultWaterAmount = userRoom.getWaterDefault();
        this.waterGoal = userRoom.getHydrationGoal();
        System.out.println("WATER GOAL IS: " + this.waterGoal);
        System.out.println("WATER GOAL IS: " + this.defaultWaterAmount);

        String addWaterTxt = this.defaultWaterAmount + "ml";
        this.addWaterBtn.setText(addWaterTxt);
        String waterOutputStr = (int) this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
        this.waterOutput.setText(waterOutputStr);




        /******* TEST *******/
        new Thread(()-> {
            saveHydration();
        }).start();
        System.out.println("HYDRATION SAVED!!!!!");
        HydrationRoomDao hydrationRoomDao = AppDatabaseRoom.getInstance(this).hydrationRoomDao();
        hydrationRoomDao.deleteOldestHydration();
        System.out.println("DELETE OLDEST NO ERROR!!!!!");
        new Thread(()-> {
            List<HydrationRoom> hydList = hydrationRoomDao.getAllHydrationsForCustomer(1L);
            if(hydList == null){
                System.out.println("HYDRATION LIST IS NULL!!!!");
                return;
            }
            HydrationRoom hydrationRoom = hydList.get(0);
            if(hydrationRoom != null){
                System.out.println("DELETED HYDRATION ROOM!!!!");
            }
        }).start();

        //fillWaterBottle();

        //load barChart
        //loadBarChart();
    }

    private void fetchUserRoomTh() {
        this.userRoom = null;
        Thread fetchUserRoomTh = new Thread(() -> {
            //get all users
            List<UserRoom> usersRoom = userDao.getAllUsers();
            //check if there is no users
            if (usersRoom.isEmpty() || usersRoom == null) {
                System.out.println("NO USERS WERE FOUND");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(hydration_tracking.this, "No users were found", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            //get user from list
            userRoom = usersRoom.get(0);
            System.out.println("LOAD DATA: LOADED USER ROOM");
            System.out.println("USER ROOM NAME IS: " + userRoom.getName());
        });
        fetchUserRoomTh.start();

        try {
            // Wait for the thread to finish
            fetchUserRoomTh.join();
        } catch (InterruptedException e) {
            Log.e("HYDRATION_TRACKING","SOMETHING WENT WRONG",e);
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBarChart(){
        // Sample data for hydration levels (in liters)
        barEntries.add(new BarEntry(0, 1.5f)); // Monday: 1.5 liters
        barEntries.add(new BarEntry(1, 2.0f)); // Tuesday: 2.0 liters
        barEntries.add(new BarEntry(2, 1.2f)); // Wednesday: 1.2 liters
        barEntries.add(new BarEntry(3, 2.5f)); // Thursday: 2.5 liters
        barEntries.add(new BarEntry(4, 1.8f)); // Friday: 1.8 liters
        barEntries.add(new BarEntry(5, 3.0f)); // Saturday: 3.0 liters
        barEntries.add(new BarEntry(6, 2.2f)); // Sunday: 2.2 liters

        BarDataSet barDataSet = new BarDataSet(barEntries, "Hydration Level (Liters)");
        barDataSet.setColor(getResources().getColor(R.color.blue));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f); // Set the bar width

//        // Set up X-axis labels for days of the week
//        String[] daysOfWeek = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setGranularity(1f); // Ensures labels are not skipped
//        xAxis.setGranularityEnabled(true);

        barChart.setData(barData);
        barChart.invalidate(); // Refresh the chart
        barChart.setFitBars(true); // Makes the bars fit the screen
        barChart.getDescription().setEnabled(false); // Disable description text
        barChart.animateY(1000); // Animation
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
        editor.putInt("drank",(int) this.currentWaterAmount);
        editor.apply();

        String waterOutputStr = (int)this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
        waterOutput.setText(waterOutputStr);

        ////////////////////// update animation //////////////////////
    }

    private void fillWaterBottle(){
//        if (waterGif != null) {
//            int totalFrames = waterGif.getFrameCount();
//
//            // Set frame based on current water amount
//            int currentFrame = (this.currentWaterAmount * totalFrames) / waterGoal;
//        }
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
                    /*************************************************/
                    this.defaultWaterAmount = inputWaterAmount;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("defaultWater", this.defaultWaterAmount);
                    editor.apply();

                    //update default hydration in room
                    new Thread(()-> {
                        userRoom.setWaterDefault(this.defaultWaterAmount);
                        userDao.updateUser(userRoom);
                    }).start();

                    //set button text
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
    public void scheduleDailyReset() {
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        // Set the time to 00:00
        dueDate.set(Calendar.HOUR_OF_DAY, 0);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);

        // Check if the dueDate has already passed today, schedule for the next day
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        // Schedule the work
//        WorkRequest dailyWorkRequest =
//                new PeriodicWorkRequest.Builder(ResetWorker.class, 24, TimeUnit.HOURS)
//                        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
//                        .build();
//
//        WorkManager.getInstance(getApplicationContext()).enqueue(dailyWorkRequest);

        //update user in server
        Service.getInstance().updateUser(userRoom.getId(), this.user);

        //save new hydration in room and server
        saveHydration();
    }

    private void saveHydration(){
        //create new hydration
        Hydration hydration = new Hydration(this.user.getId() ,this.currentWaterAmount , LocalDate.now());

        //create new hydrationRoom
        HydrationRoom hydrationRoom = new HydrationRoom(hydration);

        //create hydration Dao
        HydrationRoomDao hydrationRoomDao = AppDatabaseRoom.getInstance(this).hydrationRoomDao();

        //add hydrationRoom in room
        new Thread(()-> {
            hydrationRoom.setDrank(this.currentWaterAmount);
            hydrationRoomDao.insertHydration(hydrationRoom);
        }).start();

        //add hydration in server
        //Service.getInstance().addHydration(hydration);
    }
}