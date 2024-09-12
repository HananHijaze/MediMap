package com.example.medimap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.UserApi;

import retrofit2.Call;
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
        try {
            // Call a lightweight endpoint to check server availability
            Retrofit retrofit = RetrofitClient.getRetrofitInstance();
            UserApi userApi = retrofit.create(UserApi.class);
            Call<Void> call = userApi.pingServer();
            Response<Void> response = call.execute();

            // If the server responds successfully, return true
            return response.isSuccessful();
        } catch (Exception e) {
            Log.e("Server Check", "Failed to reach server: " + e.getMessage(), e);
            return false;
        }
    }
}