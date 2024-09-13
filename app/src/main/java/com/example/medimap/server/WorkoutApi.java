package com.example.medimap.server;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WorkoutApi {

    // Get a list of all workouts
    @GET("workouts")
    Call<List<Workout>> getAllWorkouts();

    // Get a workout by ID
    @GET("workouts/{id}")
    Call<Workout> getWorkoutById(@Path("id") Long id);

    // Create a new workout
    @POST("workouts")
    Call<Workout> createWorkout(@Body Workout workout);

    // Update an existing workout
    @PUT("workouts/{id}")
    Call<Workout> updateWorkout(@Path("id") Long id, @Body Workout workout);

    // Delete a workout by ID
    @DELETE("workouts/{id}")
    Call<Void> deleteWorkout(@Path("id") Long id);

    @GET("api/workouts/search")
    Call<List<Workout>> getWorkoutsByType(
            @Query("workouttype") String workouttype
    );
}

