package com.example.medimap;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;

import java.text.ParseException;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NetworkUtils {
    //Check internet connection
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Function to check if the server is reachable
    public static boolean isServerReachable() {

//        // Call a lightweight endpoint to check server availability
//        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
//        UserApi userApi = retrofit.create(UserApi.class);
//        Call<Void> call = userApi.pingServer();
//
//        call.enqueue(new Callback<Void>(){
//        @Override
//        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//            if (response.isSuccessful()) {
//
//
//            } else {
//
//            }
//        }
//
//        @Override
//        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//            Toast.makeText(this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
//        }
//    });
//        Thread checkServerThread = new Thread(() -> {
//            // Update UI or handle the result on the main thread
//
//        });
//        checkServerThread.start();
//
//
//
//        try {
//
//            Response<Void> response = call.execute();
//
//            // If the server responds successfully, return true
//            return response.isSuccessful();
//        } catch (Exception e) {
//            Log.e("Server Check", "Failed to reach server: " + e.getMessage(), e);
//            return false;
//        }
        return false;
    }
}