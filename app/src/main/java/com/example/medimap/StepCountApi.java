package com.example.medimap;


import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface StepCountApi {

    // Get a list of all step count records
    @GET("api/stepcounts")
    Call<List<StepCount>> getAllStepCounts();

    // Get step count records for a specific customer by email
    @GET("api/stepcounts/customer/{email}")
    Call<List<StepCount>> getStepCountsByCustomerEmail(@Path("email") String email);

    // Get step count record by ID
    @GET("api/stepcounts/{id}")
    Call<StepCount> getStepCountById(@Path("id") Long id);

    // Create a new step count record
    @POST("api/stepcounts")
    Call<StepCount> createStepCount(@Body StepCount stepCount);

    // Update an existing step count record
    @PUT("api/stepcounts/{id}")
    Call<StepCount> updateStepCount(@Path("id") Long id, @Body StepCount stepCount);

    // Delete a step count record by ID
    @DELETE("api/stepcounts/{id}")
    Call<Void> deleteStepCount(@Path("id") Long id);
}

