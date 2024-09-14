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

public interface WorkoutPlanApi {

    // Get a list of all workout plans
    @GET("workoutplans")
    Call<List<WorkoutPlan>> getAllWorkoutPlans();

    // Get a workout plan by ID
    @GET("workoutplans/{id}")
    Call<WorkoutPlan> getWorkoutPlanById(@Path("id") Long id);

    // Create a new workout plan
    @POST("workoutplans")
    Call<WorkoutPlan> createWorkoutPlan(@Body WorkoutPlan workoutPlan);

    // Update an existing workout plan
    @PUT("workoutplans/{id}")
    Call<WorkoutPlan> updateWorkoutPlan(@Path("id") Long id, @Body WorkoutPlan workoutPlan);

    // Delete a workout plan by ID
    @DELETE("workoutplans/{id}")
    Call<Void> deleteWorkoutPlan(@Path("id") Long id);

    @GET("workoutplans/latest/{customerID}")
    Call<WorkoutPlan> getLatestWorkoutPlan(@Path("customerID") Long customerID);

    @GET("workoutplans/latest")
    Call<List<WorkoutPlan>> getDatedTrainingPlans(
            @Query("customerId") Long customerId,
            @Query("inputDate") String inputDate // Pass date as String in the expected format
    );


}

