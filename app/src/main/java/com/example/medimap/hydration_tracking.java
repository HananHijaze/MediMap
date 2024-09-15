package com.example.medimap;

import static java.lang.Long.getLong;

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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private Date prevDate;
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

        // Fetch hydration data from local
        getNewestHydrationFromRoom();
        getNewestTempHydrationFromRoom(this.hydrationRoom);

        // set date
        this.prevDate = Converters.localDateToDate(this.hydrationRoom.getDate());

        // Check Reset
//        checkResetData(this.prevDate);

        //load water def, goal and amount
        this.defaultWaterAmount = this.userRoom.getWaterDefault();
        this.waterGoal = this.userRoom.getHydrationGoal();

        //load newest hydration
        getNewestHydrationFromRoom();
        this.currentWaterAmount = this.hydrationRoom.getDrank();

        allHydrations = getAllHydrations();

        //update visuals
        String defaultWaterTxt = this.defaultWaterAmount + "ml";
        addWaterBtn.setText(defaultWaterTxt);

        updateWaterProgress(0,(float) this.currentWaterAmount);

        String waterOutputStr = (int) this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
        waterOutput.setText(waterOutputStr);

        //listeners for buttons
        addWaterBtn.setOnClickListener(v -> addWater());
        editWaterDefault.setOnClickListener(v -> showEditAmountDialog());
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        System.out.println("ON RESUME");
//        loadData();
//    }

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
//
//    private void setAllTempHydrations(List<TempHydrationRoom> allTempHydrations) {
//        synchronized (this){this.allTempHydrations = allTempHydrations;}
//    }
//
//    private List<TempHydrationRoom> getAllTempHydrations() {
//        synchronized (this){return this.allTempHydrations;}
//    }

    /**************************************** Load The Data ****************************************/
//    private void loadData() {
//        // Load previously saved water amount
//        this.currentWaterAmount = getSavedWaterAmount();
//        System.out.println("LOAD DATA: THIS IS THE CURRENT WATER AMOUNT: " + this.currentWaterAmount);
//
//        // Load user dao
//        userDao = AppDatabaseRoom.getInstance(this).userDao();
//        System.out.println("LOAD DATA: LOADED USER DAO");
//
//        //check user room
//        if (this.userRoom == null) {
//            System.out.println("USER ROOM NOT FOUND");
//            Toast.makeText(this, "user not found", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        this.user = new User(this.userRoom);
//        System.out.println("USER ID and NAME ARE: " + this.user.getId() + " " + this.user.getEmail());
//        System.out.println("USER BIRTHDATE: " + this.user.getBirthDate().toString());
//        //check internet and server connection
//        boolean connected = CheckConnection();
//
//        //get default water amount and goal from user room
//        this.defaultWaterAmount = userRoom.getWaterDefault();
//        this.waterGoal = userRoom.getHydrationGoal();
//        System.out.println("WATER GOAL IS: " + this.waterGoal);
//        System.out.println("WATER DEFAULT IS: " + this.defaultWaterAmount);
//
//        String addWaterTxt = this.defaultWaterAmount + "ml";
//        this.addWaterBtn.setText(addWaterTxt);
//        String waterOutputStr = (int) this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
//        this.waterOutput.setText(waterOutputStr);
//
//        /******* TEST *******/
//        //save a new hydration to room
//        this.hydrationRoomDao = AppDatabaseRoom.getInstance(this).hydrationRoomDao();
//        new Thread(() -> {
//            saveHydration();
//        }).start();
//
//        //delete oldest hydration
//        new Thread(() -> {
//            hydrationRoomDao.deleteOldestHydration();
//            System.out.println("DELETE OLDEST NO ERROR!!!!!");
//        }).start();
//
//        //getting all hydrations for user room
//        new Thread(() -> {
//            List<HydrationRoom> hydList = hydrationRoomDao.getAllHydrationsForCustomer(this.userRoom.getId());
//            if (hydList == null) {
//                System.out.println("HYDRATION LIST IS NULL!!!!");
//                return;
//            }
//            HydrationRoom hydrationRoom = hydList.get(0);
//            if (hydrationRoom != null) {
//                System.out.println("DELETED HYDRATION ROOM!!!!");
//            }
//        }).start();
//
//        fillWaterBottle();
//
//        load barChart
//        loadBarChart();
//    }

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
            Log.e("HYDRATION_TRACKING", "SOMETHING WENT WRONG", e);
            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
            //finish activity and go back to home
            finish();
        }
    }

    private void getAllUserHydrations(Long userId){
        List<Hydration> allHydrations=null;
        if(this.connected){
            getUserHydrationsFromServer(userId);
        }
        else{
            new Thread(() -> {
                List<HydrationRoom> allHydRoom = this.hydrationRoomDao.getAllHydrationsForCustomer(userId);
                setAllHydrations(allHydRoom);
            });
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
                System.out.println("GOING TO CREATE A NEW HYDRATION");
                createNewHydration(this.userRoom.getId());
                System.out.println("CREATED NEW HYDRATION DATE IS: "+this.hydrationRoom.getDate());
                return;
            }
            else {
                //get user from list
                setThisHydrationRoom(newestHydration);
                System.out.println("LOAD DATA: LOADED HYDRATION ROOM");
                System.out.println("HYDRATION ROOM IS: " + newestHydration.getDate() + " " + newestHydration.getDrank());
                getNewestTempHydrationFromRoom(newestHydration);
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
                System.out.println("GOING TO CREATE A NEW TEMP HYDRATION");
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

    private void loadBarChart() {
//        private void loadStepDataIntoChart() {
//            new Thread(() -> {
//                // Retrieve data from the database
//                Long userId = this.userRoom.getId();
//                List<HydrationRoom> stepData = this.hydrationRoomDao.getAllHydrationsForCustomer(userId);
//
//                // Prepare the entries for the bar chart
//                List<BarEntry> entries = new ArrayList<>();
//                List<String> formattedDates = new ArrayList<>(); // To store formatted dates for x-axis labels
//
//                for (int i = 0; i < stepData.size(); i++) {
//                    StepCountRoom step = stepData.get(i);
//                    // Use the index as x-axis and the step count as y-axis
//                    entries.add(new BarEntry(i, step.getSteps()));
//                    // Format the date without the year (day-month)
//                    String formattedDate = sdf.format(java.sql.Date.valueOf(step.getDate()));
//                    formattedDates.add(formattedDate); // Collect formatted dates for x-axis labels
//                }
//
//                // Post the data to the main thread to update the chart
//                runOnUiThread(() -> {
//                    BarChart barChart = findViewById(R.id.stepChart);
//
//                    // Create the dataset
//                    BarDataSet dataSet = new BarDataSet(entries, "Steps");
//                    dataSet.setColor(getResources().getColor(R.color.white)); // Set bar color
//                    dataSet.setValueTextColor(getResources().getColor(R.color.black)); // Value text color
//
//                    // Customize bar width
//                    BarData barData = new BarData(dataSet);
//                    barData.setBarWidth(0.9f); // Set custom bar width
//
//                    // Set the data to the chart
//                    barChart.setData(barData);
//                    barChart.setFitBars(true); // Make the bars fit nicely within the chart
//                    barChart.invalidate();  // Refresh the chart with the new data
//
//                    // Customize x-axis
//                    XAxis xAxis = barChart.getXAxis();
//                    xAxis.setValueFormatter(new IndexAxisValueFormatter(formattedDates)); // Display formatted dates (day-month) on x-axis
//                    xAxis.setGranularity(1f); // Ensure labels are spaced evenly
//                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position x-axis labels at the bottom
//
//                    // Customize y-axis
//                    YAxis leftAxis = barChart.getAxisLeft();
//                    leftAxis.setAxisMinimum(0f); // Start y-axis at 0
//                    leftAxis.setAxisMaximum(12000f); // Set a maximum limit for better visualization (optional)
//
//                    // Add goal line (LimitLine) at 10,000 steps
//                    LimitLine goalLine = new LimitLine(10000f, "Step Goal (10,000)");
//                    goalLine.setLineWidth(2f); // Set the thickness of the goal line
//                    goalLine.setLineColor(getResources().getColor(R.color.red)); // Set the color of the goal line
//                    goalLine.setTextSize(12f); // Set the text size for the label
//                    goalLine.setTextColor(getResources().getColor(R.color.red)); // Set the text color for the label
//
//                    // Add the goal line to the left axis
//                    leftAxis.addLimitLine(goalLine);
//
//                    barChart.getAxisRight().setEnabled(false); // Disable the right y-axis
//
//                    // Enable chart scaling and dragging
//                    barChart.setDragEnabled(true); // Enable dragging
//                    barChart.setScaleEnabled(true); // Enable scaling
//                    barChart.setScaleXEnabled(true); // Enable horizontal scaling
//                    barChart.setPinchZoom(false); // Disable pinch zooming
//                    barChart.setDoubleTapToZoomEnabled(true); // Enable double-tap zoom
//
//                    // Set visible range (number of bars visible at once)
//                    barChart.setVisibleXRangeMaximum(7); // Show only 7 bars at a time
//                    barChart.moveViewToX(entries.size() - 7); // Move to the last 7 entries
//
//                    // Add animation
//                    barChart.animateY(1000); // Animate chart on the y-axis for 1 second
//
//                    // Customize legend
//                    Legend legend = barChart.getLegend();
//                    legend.setTextSize(14f); // Set text size
//                    legend.setForm(Legend.LegendForm.CIRCLE); // Customize legend form
//                });
//            }).start();
//        }
    }

    private int getSavedWaterAmount() {
        int waterAmount = 0;
//        if (this.sharedPreferences == null) {
//            System.out.println("DATA NOT FOUND");
//            Toast.makeText(this, "data not found", Toast.LENGTH_SHORT).show();
//        }
//        waterAmount = sharedPreferences.getInt("drank", 0);
        return waterAmount;
    }

    /**************************************** Add Water ****************************************/
    private void addWater() {
        float prevWaterAmount = (float)this.currentWaterAmount;
        this.currentWaterAmount = this.currentWaterAmount + this.defaultWaterAmount;

        this.hydrationRoom.setDrank(this.currentWaterAmount);
        this.tempHydrationRoom.setDrank(this.currentWaterAmount);

        //update hydration in room
        new Thread(() -> {
            hydrationRoomDao.updateHydration(this.hydrationRoom);
            tempHydrationRoomDao.updateTempHydration(this.tempHydrationRoom);
        }).start();

        String waterOutputStr = (int) this.currentWaterAmount + " ml / " + this.waterGoal + " ml";
        waterOutput.setText(waterOutputStr);

        //update water bottle
        updateWaterProgress(prevWaterAmount,(float) this.currentWaterAmount);
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
                    Toast.makeText(this, "No changes were made", Toast.LENGTH_SHORT).show();
                    input.setText("");
                }
                else {
                    this.defaultWaterAmount = inputWaterAmount;
                    System.out.println("UPDATED WATER DEFAULT: "+this.defaultWaterAmount);
                    //update default hydration in room
                    new Thread(() -> {
                        this.userRoom.setWaterDefault(this.defaultWaterAmount);
                        this.userDao.updateUser(userRoom);
                    }).start();

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
    public void scheduleDailyReset() {
//        Calendar currentDate = Calendar.getInstance();
//        Calendar dueDate = Calendar.getInstance();
//
//        // Set the time to 00:00
//        dueDate.set(Calendar.HOUR_OF_DAY, 0);
//        dueDate.set(Calendar.MINUTE, 0);
//        dueDate.set(Calendar.SECOND, 0);
//
//        // Check if the dueDate has already passed today, schedule for the next day
//        if (dueDate.before(currentDate)) {
//            dueDate.add(Calendar.HOUR_OF_DAY, 24);
//        }
//
//        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        // Schedule the work
//        WorkRequest dailyWorkRequest =
//                new PeriodicWorkRequest.Builder(ResetWorker.class, 24, TimeUnit.HOURS)
//                        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
//                        .build();
//
//        WorkManager.getInstance(getApplicationContext()).enqueue(dailyWorkRequest);

        //update user in server
//        Service.getInstance().updateUser(userRoom.getId(), this.user);
//
//        //save new hydration in room and server
//        saveHydration();

        //Current date
        Date currDate = new Date();

        //previous date
        setPrevDate(this.prevDate);

        //if day changed
        if(currDate.after(this.prevDate)){
            //update user in server
            getUserFromServer(this.userRoom.getEmail());
            this.user.setWaterDefault(this.userRoom.getWaterDefault());
            /******** add user to server ********/

            saveHydration();
        }


    }

    private void setPrevDate(Date date){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        this.sharedPreferences.edit().putLong("prevDate", date.getTime()).apply();
        editor.apply();
    }

    private Date getPrevDate(){
        long  prevLong = this.sharedPreferences.getLong("prevDate",0 );
        return new Date(prevLong);
    }

    private void saveHydration() {
//        //add hydrationRoom in room
//        new Thread(() -> {
//            hydrationRoom.setDrank(this.currentWaterAmount);
//            hydrationRoomDao.insertHydration(hydrationRoom);
//            System.out.println("HYDRATION SAVED IN ROOM: drank= "+hydrationRoom.getDrank()+
//                    " ID= "+hydrationRoom.getId()+" DATE= "+hydrationRoom.getDate().toString());
//        }).start();

        //add hydration in server
        //Service.getInstance().addHydration(hydration);
    }

    private void checkResetData(Date prevDate){
        Date currDate = new Date();
        if(currDate.after(prevDate)) {
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

    private void uploadUserToServer(User user) {
        this.userApi.updateUser(user.getId(),user);
    }

    private void uploadHydrationToServer(Hydration hydration){
        this.hydrationApi.createHydration(hydration);
    }

    private void uploadAllTempHydration(List<TempHydrationRoom> allTempHydrations, User user){
        for(TempHydrationRoom tempH : allTempHydrations){
            Hydration hydration = new Hydration(tempH);
            hydration.setCustomerId(user.getId());/*******************************************/
            uploadHydrationToServer(hydration);
        }
    }
}