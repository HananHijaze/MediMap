package com.example.medimap.server;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface WeekDaysApi {

    // Get a list of all weekdays
    @GET("api/weekdays")
    Call<List<WeekDays>> getAllWeekDays();

    // Get a weekday by ID
    @GET("api/weekdays/{id}")
    Call<WeekDays> getWeekDayById(@Path("id") Long id);

    // Create a new weekday
    @POST("api/weekdays")
    Call<WeekDays> createWeekDay(@Body WeekDays weekDays);

    // Update an existing weekday
    @PUT("api/weekdays/{id}")
    Call<WeekDays> updateWeekDay(@Path("id") Long id, @Body WeekDays weekDays);

    // Delete a weekday by ID
    @DELETE("api/weekdays/{id}")
    Call<Void> deleteWeekDay(@Path("id") Long id);
}
