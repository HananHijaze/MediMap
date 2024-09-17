package com.example.medimap;

import static java.lang.Long.getLong;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Typeface;
import android.graphics.drawable.Icon;
import android.os.Build;
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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.Converters;
import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.HydrationRoomDao;
import com.example.medimap.roomdb.TempHydrationRoom;
import com.example.medimap.roomdb.TempHydrationRoomDao;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.Hydration;
import com.example.medimap.server.HydrationApi;
import com.example.medimap.server.RetrofitClient;
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

import java.text.SimpleDateFormat;
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

public class hydration_tracking extends AppCompatActivity {

    //Page components
    private TextView waterOutput;
    private Button addWaterBtn;
    private BarChart barChart;
    private ProgressBar waterProgressBar;

    //Daos
    private UserDao userDao;
    private HydrationRoomDao hydrationRoomDao;
    private TempHydrationRoomDao tempHydrationRoomDao;

    //Room
    private UserRoom userRoom;
    private HydrationRoom hydrationRoom;
    private TempHydrationRoom tempHydrationRoom;

    //Server
    private User user;
    private Hydration hydration;

    //Servers
    private HydrationApi hydrationApi;
    private UserApi userApi;
    private Retrofit retrofit;

    //variables
    private double currentWaterAmount = 0;
    private int waterGoal = 0;
    private int defaultWaterAmount = 0;
    private List<HydrationRoom> allHydrations;
//    private List<TempHydrationRoom> allTempHydrations;
    private LocalDate prevDate;
    private boolean connected = false;

    //preference
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
        createNotificationChannel();

        createHydrationTrackingPage();

        // Create a shortcut if supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            createShortcut();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("ON RESUME HYDRATION TRACKING");
        createNotificationChannel();

        createHydrationTrackingPage();
    }

    private void createHydrationTrackingPage(){

        //water output text
        waterOutput = findViewById(R.id.waterOutput);

        //buttons
        addWaterBtn = findViewById(R.id.addWaterBtn);
        Button editWaterDefault;
        editWaterDefault = findViewById(R.id.editButton);

        //progress bottle
        waterProgressBar = findViewById(R.id.waterProgress);
        waterProgressBar.setMax(100);

        //barChart
        barChart = findViewById(R.id.barChart);

        sharedPreferences = getSharedPreferences("waterPrefs", MODE_PRIVATE);

        //check network and server connection (sets this.connected variable)
        checkServerConnection();

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

        // Fetch hydration data from local
        getNewestHydrationFromRoom();
        if(this.hydrationRoom != null)
            getNewestTempHydrationFromRoom(this.hydrationRoom);
        else{
            System.out.println("HYDRATION ROOM IS NULL (OnCreate)");
            Toast.makeText(this, "NO HYDRATION FOUND IS NULL", Toast.LENGTH_SHORT).show();
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
        if(this.hydrationRoom != null)
            getNewestTempHydrationFromRoom(this.hydrationRoom);
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

        String waterOutputStr = (int) this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
        waterOutput.setText(waterOutputStr);

        //get hydration data
        getAllUserHydrations(this.userRoom.getId());
        if(this.allHydrations == null) {
            //add example data
            //addExampleHydrationsToRoom();
            System.out.println("HYDRATION ROOM IS NULL (OnCreate)");
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            for (int i = 0; i < this.allHydrations.size(); i++) {
                System.out.println("HYDRATION NUM: " + i);
                HydrationRoom hydR = this.allHydrations.get(i);
                System.out.println("HYDRATION DATE: " + hydR.getDate() + " " + hydR.getDrank());
            }
            //load barChart
            loadBarChart(this.allHydrations);
        }

        //get hydration data
        getAllUserHydrations(this.userRoom.getId());

        //check hydration list
        if(this.allHydrations != null) {
        }
        else {
            System.out.println("HYDRATION LIST IS NULL (OnCreate");

        }

        //listeners for buttons
        addWaterBtn.setOnClickListener(v -> addWater());
        editWaterDefault.setOnClickListener(v -> showEditAmountDialog());
    }

    // Method to create a shortcut if supported (for Android 7.1+)
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private void createShortcut() {
        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        if (shortcutManager != null && shortcutManager.isRequestPinShortcutSupported()) {
            // Check if the shortcut already exists
            boolean shortcutExists = false;
            for (ShortcutInfo pinnedShortcut : shortcutManager.getPinnedShortcuts()) {
                if (pinnedShortcut.getId().equals("shortcut_example")) {
                    shortcutExists = true;
                    break;
                }
            }

            // If the shortcut does not exist, create and pin it
            if (!shortcutExists) {
                Intent addWaterIntent = new Intent(this, AddWaterReceiver.class);
                addWaterIntent.setAction("com.example.medimap.ADD_WATER");

                ShortcutInfo shortcut = new ShortcutInfo.Builder(this, "shortcut_example")
                        .setShortLabel(getString(R.string.shortcut_short_label))
                        .setLongLabel(getString(R.string.shortcut_long_label))
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut))
                        .setIntent(new Intent(Intent.ACTION_VIEW, null, this, Home.class))
                        .build();

                shortcutManager.requestPinShortcut(shortcut, null);
            }
        }
    }

    /**************************************** Getters And Setters ****************************************/
    //getters and setters
    public void setThisUser(User user){synchronized (this){this.user = user;}}

    public User getThisUser(){synchronized (this){return this.user;}}

    public UserRoom getThisUserRoom() {synchronized (this){return this.userRoom;}}

    public void setThisUserRoom(UserRoom userRoom) {synchronized (this){this.userRoom = userRoom;}}

    public void setThisHydrationRoom(HydrationRoom hydrationRoom) {synchronized (this){this.hydrationRoom= hydrationRoom;}}

    public HydrationRoom getThisHydrationRoom() {synchronized (this){return this.hydrationRoom;}}

    public void setThisTempHydrationRoom(TempHydrationRoom tempHydrationRoom) {
        synchronized (this){this.tempHydrationRoom= tempHydrationRoom;}}

    public TempHydrationRoom getThisTempHydrationRoom() {
        synchronized (this){return this.tempHydrationRoom;}}

    public void setAllHydrations(List<HydrationRoom> allHydrations) {
        synchronized (this){this.allHydrations = allHydrations;}
    }

    public List<HydrationRoom> getAllHydrations() {
        synchronized (this){return this.allHydrations;}
    }

    public void setConnected(boolean connected){this.connected = connected;}

    public boolean getConnected(){return this.connected;}

    public void updateWaterProgress(float from, float to){
        ProgressBarAnimation anim = new ProgressBarAnimation(this.waterProgressBar,
                from*100/this.waterGoal, to*100/this.waterGoal);
        anim.setDuration(750);
        this.waterProgressBar.startAnimation(anim);
    }

    public void getAddWater(){ this.addWater();}

//    public void setAllTempHydrations(List<TempHydrationRoom> allTempHydrations) {
//        synchronized (this){this.allTempHydrations = allTempHydrations;}
//    }
//
//    public List<TempHydrationRoom> getAllTempHydrations() {
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
                            Toast.makeText(hydration_tracking.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(hydration_tracking.this, "Failed to load user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private boolean CheckConnection() {
//        if (!NetworkUtils.isNetworkAvailable(this))
//            return false;
//        return NetworkUtils.isServerReachable();
//    }

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
                        Toast.makeText(hydration_tracking.this, "No users were found", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(hydration_tracking.this, "Failed to load hydrations: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loadBarChart(List<HydrationRoom> hydrationData) {
        Thread loadBarChartTh = new Thread(() -> {
            // Prepare the entries for the bar chart
            List<BarEntry> entries = new ArrayList<>();
            List<String> formattedDates = new ArrayList<>(); // To store formatted dates for x-axis labels

            // Date formatter to format the date as "dd-MM" (day-month)
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM", Locale.getDefault());

            for (int i = 0; i < hydrationData.size(); i++) {
                HydrationRoom hydration = hydrationData.get(i);

                // Use the index as x-axis and the drank as y-axis
                entries.add(new BarEntry(i, hydration.getDrank().intValue()));

                // Format the date without the year (day-month)
                Date hydrationDate = Converters.localDateToDate(hydration.getDate());
                String formattedDate = sdf.format(hydrationDate);
                formattedDates.add(formattedDate); // Collect formatted dates for x-axis labels
            }

            // Post the data to the main thread to update the chart
            runOnUiThread(() -> {
                // Create the dataset
                BarDataSet dataSet = new BarDataSet(entries, "Hydration");
                dataSet.setColor(getResources().getColor(R.color.blue)); // Set bar color
                dataSet.setValueTextColor(getResources().getColor(R.color.black)); // Value text color

                // Customize bar width
                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.6f); // Set custom bar width

                // Set the data to the chart
                this.barChart.setData(barData);
                this.barChart.setFitBars(true); // Make the bars fit nicely within the chart
                this.barChart.invalidate();  // Refresh the chart with the new data

                // Customize x-axis
                XAxis xAxis = this.barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(formattedDates)); // Display formatted dates (day-month) on x-axis
                xAxis.setGranularity(1f); // Ensure labels are spaced evenly
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position x-axis labels at the bottom

                // Set font to bold
                Typeface boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
                xAxis.setTypeface(boldTypeface);

                // Customize y-axis
                YAxis leftAxis = this.barChart.getAxisLeft();
                leftAxis.setAxisMinimum(0f); // Start y-axis at 0
                float maxDrank = getMaxDrank(hydrationData).floatValue() +500;
                leftAxis.setAxisMaximum(maxDrank); // Set a maximum limit for better visualization (optional)
                leftAxis.setTypeface(boldTypeface);

                // Add goal line (LimitLine) at 10,000 steps
                String goalLineStr = "Hydration Goal: "+this.waterGoal+"ml";
                LimitLine goalLine = new LimitLine(this.waterGoal, goalLineStr);
                goalLine.setLineWidth(1.5f); // Set the thickness of the goal line
                goalLine.setLineColor(getResources().getColor(R.color.darkgreen)); // Set the color of the goal line
                goalLine.setTextSize(8f); // Set the text size for the label
                goalLine.setTextColor(getResources().getColor(R.color.darkgreen)); // Set the text color for the label

                // Add the goal line to the left axis
                leftAxis.addLimitLine(goalLine);

                // Enable chart scaling and dragging
                this.barChart.setDragEnabled(true); // Enable dragging
                this.barChart.setScaleEnabled(true); // Enable scaling
                this.barChart.setScaleXEnabled(true); // Enable horizontal scaling
                this.barChart.setPinchZoom(false); // Disable pinch zooming
                this.barChart.setDoubleTapToZoomEnabled(true); // Enable double-tap zoom

                this.barChart.getAxisRight().setEnabled(false); // Disable the right y-axis
                this.barChart.getLegend().setEnabled(false); // Disable the legend
                this.barChart.getDescription().setText("");
                this.barChart.setDrawGridBackground(false); // Disable grid lines

                // Set visible range (number of bars visible at once)
                this.barChart.setVisibleXRangeMaximum(7); // Show only 7 bars at a time
                this.barChart.moveViewToX(entries.size() - 7); // Move to the last 7 entries

                // Add animation
                this.barChart.animateY(1000); // Animate chart on the y-axis for 1 second
            });
        });
        loadBarChartTh.start();

        try {
            // Wait for the thread to finish
            loadBarChartTh.join();
        } catch (Exception e) {
            System.out.println("EXCEPTION WHEN LOADING BAR CHART");
        }
    }

    private Double getMaxDrank(List<HydrationRoom> hydrationList) {
        if (hydrationList == null || hydrationList.isEmpty()) {
            return null; // Return null or some default value if the list is empty or null
        }

        // Initialize maxDrank with the first object's "drank" value
        Double maxDrank = Double.MIN_VALUE;

        // Iterate through the list to find the maximum drank value
        for (HydrationRoom hydration : hydrationList) {
            if (hydration.getDrank() > maxDrank) {
                maxDrank = hydration.getDrank();
            }
        }

        return maxDrank; // Return the maximum "drank" value
    }

    /**************************************** Add Water ****************************************/
    private void addWater() {
        float prevWaterAmount = (float)this.currentWaterAmount;
        this.currentWaterAmount = this.currentWaterAmount + this.defaultWaterAmount;

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
            //finish activity and go back to home
        }

        String waterOutputStr = (int) this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
        waterOutput.setText(waterOutputStr);

        //update water bottle
        updateWaterProgress(prevWaterAmount,(float) this.currentWaterAmount);

        if(this.currentWaterAmount >= this.waterGoal) {
            boolean ReachedGoalNoti = sharedPreferences.getBoolean("ReachedHydGoalNoti", false);
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

    private void createNotificationChannel() {
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
                if (inputWaterAmount <= 0) {
                    Toast.makeText(this, "Please enter a positive number!", Toast.LENGTH_SHORT).show();
                    input.setText("");
                } else if (inputWaterAmount > this.waterGoal) {
                    Toast.makeText(this, "Number larger than goal!", Toast.LENGTH_SHORT).show();
                    input.setText("");
                }
                else if(inputWaterAmount == this.defaultWaterAmount){
                    Toast.makeText(this, "No change", Toast.LENGTH_SHORT).show();
                    input.setText("");
                }
                else {
                    this.defaultWaterAmount = inputWaterAmount;
                    System.out.println("UPDATED WATER DEFAULT: "+this.defaultWaterAmount);
                    //update default hydration in room
                    Thread updateDefaultWaterTh = new Thread(() -> {
                        this.userRoom.setWaterDefault(this.defaultWaterAmount);
                        this.userDao.updateUser(userRoom);
                    });
                    updateDefaultWaterTh.start();

                    try {
                        //wait for thread to finish
                        updateDefaultWaterTh.join();
                    } catch (Exception e) {
                        System.out.println("EXCEPTION WHEN UPDATING DEFAULT WATER");
                        Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
                        //finish activity and go back to home
                    }

                    //set button text
                    String addWaterTxt = this.defaultWaterAmount + "ml";
                    addWaterBtn.setText(addWaterTxt);

                    Toast.makeText(this, "New default is: " + this.defaultWaterAmount + "ml", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            } else {
                Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    /**************************************** Save the Data ****************************************/
    private void checkResetData(LocalDate prevDate){
        LocalDate currDate = LocalDate.now();
        if(currDate.isAfter(prevDate)) {
            Thread resetThread = new Thread(() -> {

                List<HydrationRoom> allHyd = hydrationRoomDao.getAllHydrationsForCustomer(this.userRoom.getId());
                List<TempHydrationRoom> allTempHyd = tempHydrationRoomDao.getAllTempHydrations();
                if (allHyd == null || allTempHyd == null) {
                    System.out.println("NO HYDRATION WAS IN TABLE");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(hydration_tracking.this, "No hydration data was found", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(hydration_tracking.this, "user not found in server", Toast.LENGTH_SHORT).show();
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