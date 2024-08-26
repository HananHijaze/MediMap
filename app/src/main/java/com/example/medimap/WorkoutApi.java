package com.example.medimap;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WorkoutApi {

    // Get a list of all workouts
    @GET("api/workouts")
    Call<List<Workout>> getAllWorkouts();

    // Get a workout by ID
    @GET("api/workouts/{id}")
    Call<Workout> getWorkoutById(@Path("id") Long id);

    // Create a new workout
    @POST("api/workouts")
    Call<Workout> createWorkout(@Body Workout workout);

    // Update an existing workout
    @PUT("api/workouts/{id}")
    Call<Workout> updateWorkout(@Path("id") Long id, @Body Workout workout);

    // Delete a workout by ID
    @DELETE("api/workouts/{id}")
    Call<Void> deleteWorkout(@Path("id") Long id);
}

