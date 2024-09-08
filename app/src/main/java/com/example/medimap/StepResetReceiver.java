
package com.example.medimap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class StepResetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MidnightReset", "Midnight reset triggered");

        // Reset the step count at midnight
        SharedPreferences sharedPreferences = context.getSharedPreferences("stepPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("stepCount", 0);
        editor.putLong("lastResetTime", System.currentTimeMillis());
        editor.apply();
    }
}
