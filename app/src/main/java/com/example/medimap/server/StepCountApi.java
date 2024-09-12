package com.example.medimap.server;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface StepCountApi {

    @GET("stepcount/{id}")
    Call<StepCount> getStepCountById(@Path("id") Long id);

    @POST("stepcount")
    Call<StepCount> createStepCount(@Body StepCount stepCount);

    @PUT("stepcount/{id}")
    Call<StepCount> updateStepCount(@Path("id") Long id, @Body StepCount stepCount);

    @GET("stepcount")
    Call<List<StepCount>> getStepCountsByCustomerId(@Query("customerId") Long customerId);

    @DELETE("stepcount/{id}")
    Call<Void> deleteStepCount(@Path("id") Long id);

    @GET("steps/last7days/{customerId}")
    Call<List<StepCount>> getLast7DaysSteps(@Path("customerId") Long customerId);

}
