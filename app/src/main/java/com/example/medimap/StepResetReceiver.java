package com.example.medimap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.Log;
import android.widget.Toast;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.StepCountDao;
import com.example.medimap.roomdb.StepCountRoom;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;

import java.util.Date;
import java.util.Locale;

public class StepResetReceiver extends BroadcastReceiver {
    private UserRoom userRoom;
    private UserDao userDao;
    private StepCountDao stepCountRoomDao;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Check if it's midnight (00:00)
        if (currentHour == 23 && currentMinute == 59) {
            // Perform step reset operation only at midnight
            resetSteps(context);
        }
    }
    private void resetSteps(Context context) {
        // Access SharedPreferences and reset steps
        SharedPreferences sharedPreferences = context.getSharedPreferences("stepPrefs", Context.MODE_PRIVATE);

        // Get the current step count and previous total steps before reset
        int totalSteps = sharedPreferences.getInt("totalSteps", 0);
        int stepCount = sharedPreferences.getInt("stepCount", 0);

        // Reset the steps in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("previousTotalSteps", totalSteps); // Update previousTotalSteps
        editor.putInt("stepCount", 0); // Reset current step count to 0
        editor.apply();
        resetStepCountInRoom(context, stepCount);

        // You can log or display a message to confirm the reset happened
        Log.d("StepResetReceiver", "Steps have been reset to 0 at midnight.");
    }

    private void resetStepCountInRoom(Context context, int stepCount) {
        AppDatabaseRoom db = AppDatabaseRoom.getInstance(context);
        StepCountDao stepCountDao = db.stepCountDao();
        userDao = db.userDao();
        stepCountRoomDao = db.stepCountDao();

        // Logic to reset steps in Room database
        new Thread(() -> {
            userRoom  = userDao.getFirstUser(); // Fetch the first user from the database
            if (userRoom != null) { // Ensure the user exists before proceeding
                Long userId = userRoom.getId();
                StepCountRoom stepCountRoom = new StepCountRoom(userId, stepCount, getCurrentDate());
                stepCountRoomDao.insertStepCount(stepCountRoom); // Insert step count into the Room database
            }
        }).start();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}


//        // Optionally reset in Room database
//        resetStepCountInRoom(context, stepCount);
//
//        // Notify user or perform further actions if needed
//        Toast.makeText(context, "Steps have been reset at midnight", Toast.LENGTH_SHORT).show();
//    }
//
//    // Modified method to accept stepCount as a parameter
//    private void resetStepCountInRoom(Context context, int stepCount) {
//        AppDatabaseRoom db = AppDatabaseRoom.getInstance(context);
//        StepCountDao stepCountDao = db.stepCountDao();
//        userDao = db.userDao();
//        stepCountRoomDao = db.stepCountDao();
//
//        // Logic to reset steps in Room database
//        new Thread(() -> {
//            userRoom  = userDao.getFirstUser(); // Fetch the first user from the database
//            if (userRoom != null) { // Ensure the user exists before proceeding
//                Long userId = userRoom.getId();
//                StepCountRoom stepCountRoom = new StepCountRoom(userId, stepCount, getCurrentDate());
//                stepCountRoomDao.insertStepCount(stepCountRoom); // Insert step count into the Room database
//            }
//        }).start();
//    }
//
//    private String getCurrentDate() {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        return sdf.format(new Date());
//    }
//}
