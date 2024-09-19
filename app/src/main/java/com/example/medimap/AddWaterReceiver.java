package com.example.medimap;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.medimap.roomdb.AppDatabaseRoom;
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

import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddWaterReceiver extends BroadcastReceiver {
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
    public void onReceive(Context context, Intent intent) {
        if ("com.example.medimap.ADD_WATER".equals(intent.getAction())) {
            // Initialize Room DAOs
            AppDatabaseRoom db = AppDatabaseRoom.getInstance(context);
            userDao = db.userDao();
            hydrationRoomDao = db.hydrationRoomDao();
            tempHydrationRoomDao = db.tempHydrationRoomDao();

            // Get the user from the database
            UserRoom userRoom = userDao.getFirstUser();
            if (userRoom != null) {
                // Fetch the latest hydration record
                HydrationRoom hydrationRoom = hydrationRoomDao.getNewestHydration();
                if (hydrationRoom != null) {
                    // Update water intake
                    double defaultWaterAmount = userRoom.getWaterDefault();
                    double newWaterAmount = hydrationRoom.getDrank() + defaultWaterAmount;
                    hydrationRoom.setDrank(newWaterAmount);

                    // Update the hydration record in the database
                    new Thread(() -> {
                        hydrationRoomDao.updateHydration(hydrationRoom);
                        // Notify the user
                        showToast(context, defaultWaterAmount);
                    }).start();
                } else {
                    showToast(context, "No hydration record found.");
                }
            } else {
                showToast(context, "No user found.");
            }
        }
    }

    private void showToast(Context context, double amount) {
        String message = amount + " ml added to your hydration today!";
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

    private void showToast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

//    private void createHydrationTrackingPage(){
//
//        //check network and server connection (sets this.connected variable)
//        checkServerConnection();
//
//        if(connected){
//            //get server components
//            retrofit = RetrofitClient.getRetrofitInstance();
//            hydrationApi = retrofit.create(HydrationApi.class);
//            userApi = retrofit.create(UserApi.class);
//        }
//
//        //set Daos
//        this.userDao = AppDatabaseRoom.getInstance(this).userDao();
//        this.hydrationRoomDao = AppDatabaseRoom.getInstance(this).hydrationRoomDao();
//        this.tempHydrationRoomDao = AppDatabaseRoom.getInstance(this).tempHydrationRoomDao();
//
//        /***************** DELETING HYDRATION *********************/
////        new Thread(() -> {
////            this.hydrationRoomDao.deleteAllHydrations();
////            this.tempHydrationRoomDao.deleteAllTempHydration();
////        }).start();
//
//        // Fetch the single user from local
//        getUserRoomTh();
//
//        if(this.userRoom == null) {
//            System.out.println("USER ROOM IS NULL (OnCreate)");
//            Toast.makeText(this, "NO USER WAS FOUND", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Fetch hydration data from local
//        getNewestHydrationFromRoom();
//        if(this.hydrationRoom != null)
//            getNewestTempHydrationFromRoom(this.hydrationRoom);
//        else{
//            System.out.println("HYDRATION ROOM IS NULL (OnCreate)");
//            Toast.makeText(this, "NO HYDRATION FOUND IS NULL", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        // Set date for daily reset
//        this.prevDate = this.hydrationRoom.getDate();
//        System.out.println("PREV DATE IS: "+this.prevDate);
//
//        // Check Reset
//        checkResetData(this.prevDate);
//
//        //load water def, goal and amount
//        this.defaultWaterAmount = this.userRoom.getWaterDefault();
//        this.waterGoal = this.userRoom.getHydrationGoal();
//        System.out.println("WATER GOAL IS: "+this.waterGoal);
//        System.out.println("WATER DEFAULT IS: "+this.defaultWaterAmount);
//
//        //load newest hydration
//        getNewestHydrationFromRoom();
//        if(this.hydrationRoom != null)
//            getNewestTempHydrationFromRoom(this.hydrationRoom);
//        else {
//            System.out.println("HYDRATION ROOM IS NULL (OnCreate)");
//            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
//        this.currentWaterAmount = this.hydrationRoom.getDrank();
//        System.out.println("CURRENT WATER AMOUNT IS: " + this.currentWaterAmount);
//
//        //check hydration list
//        if(this.allHydrations != null) {
//        }
//        else {
//            System.out.println("HYDRATION LIST IS NULL (OnCreate");
//
//        }
//    }

//    //check server connection
//    private void checkServerConnection() {
//        if (NetworkUtils.isNetworkAvailable(this)){
//
//            Thread checkServerThread = new Thread(() -> {
//
//                // Call a lightweight endpoint to check server availability
//                Retrofit retrofit = RetrofitClient.getRetrofitInstance();
//                UserApi userApi = retrofit.create(UserApi.class);
//                Call<Void> call = userApi.pingServer();
//
//                call.enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse
//                            (@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                        if (response.isSuccessful()) {
//                            setConnected(true);
//                            System.out.println("SERVER IS CONNECTED: "+getConnected());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                        setConnected(false);
//                        System.out.println("CONNECTED TO SERVER: "+getConnected());
//                        Toast.makeText(hydration_tracking.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//            });
//            checkServerThread.start();
//
//            try {
//                //wait for thread to finish
//                checkServerThread.join();
//            } catch (Exception e) {
//                Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
//                System.out.println("SOMETHING WENT WRONG WITH THE SERVER CHECK");
//            }
//        }else{
//            setConnected(false);
//        }
//    }
//
//    //get user from room
//    private void getUserRoomTh() {
//        Thread fetchUserRoomTh = new Thread(() -> {
//            //get all users
//            UserRoom userRoom = userDao.getFirstUser();
//            //check if there is no users
//            if (userRoom == null) {
//                System.out.println("NO USERS WERE FOUND");
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(, "No users were found", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return;
//            }
//            else {
//                //get user from list
//                setThisUserRoom(userRoom);
//                System.out.println("LOAD DATA: LOADED USER ROOM");
//                System.out.println("USER ROOM IS: " + userRoom.getId() + " " + userRoom.getEmail());
//            }
//        });
//        fetchUserRoomTh.start();
//
//        try {
//            // Wait for the thread to finish
//            fetchUserRoomTh.join();
//        } catch (Exception e) {
//            System.out.println("EXCEPTION WHILE GETTING USER ROOM");
//            Toast.makeText(this, "SOMETHING WENT WRONG", Toast.LENGTH_SHORT).show();
//            //finish activity and go back to home
//            finish();
//        }
//    }
//
//    public void setConnected(boolean connected){this.connected = connected;}
//
//    public boolean getConnected(){return this.connected;}
}
