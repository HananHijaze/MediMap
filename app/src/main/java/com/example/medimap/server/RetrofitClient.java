package com.example.medimap.server;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Base URL for the API
    private static final String BASE_URL = "http://192.168.62.78:8081";

    // Singleton instance of Retrofit
    private static Retrofit retrofit;

    // Method to get the Retrofit instance
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Create a logging interceptor to see request and response details
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Create an OkHttpClient with a custom timeout configuration
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(7, TimeUnit.SECONDS) // 3 seconds connection timeout
                    .readTimeout(7, TimeUnit.SECONDS)    // 3 seconds read timeout
                    .writeTimeout(7, TimeUnit.SECONDS)   // 3 seconds write timeout
                    .retryOnConnectionFailure(true)       // Retry on connection failure
                    .build();

            // Build Retrofit instance with Gson converter and the custom OkHttpClient
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
