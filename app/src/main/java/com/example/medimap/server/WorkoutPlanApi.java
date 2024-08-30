package com.example.medimap.server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WorkoutPlanApi {

    // Get a list of all workout plans
    @GET("api/workoutplans")
    Call<List<WorkoutPlan>> getAllWorkoutPlans();

    // Get a workout plan by ID
    @GET("api/workoutplans/{id}")
    Call<WorkoutPlan> getWorkoutPlanById(@Path("id") Long id);

    // Create a new workout plan
    @POST("api/workoutplans")
    Call<WorkoutPlan> createWorkoutPlan(@Body WorkoutPlan workoutPlan);

    // Update an existing workout plan
    @PUT("api/workoutplans/{id}")
    Call<WorkoutPlan> updateWorkoutPlan(@Path("id") Long id, @Body WorkoutPlan workoutPlan);

    // Delete a workout plan by ID
    @DELETE("api/workoutplans/{id}")
    Call<Void> deleteWorkoutPlan(@Path("id") Long id);
}

