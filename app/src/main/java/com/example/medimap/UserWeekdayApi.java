package com.example.medimap;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserWeekdayApi {

    // Get a list of all user weekdays
    @GET("api/userweekdays")
    Call<List<UserWeekday>> getAllUserWeekdays();

    // Get a user weekday by ID
    @GET("api/userweekdays/{id}")
    Call<UserWeekday> getUserWeekdayById(@Path("id") Long id);

    // Create a new user weekday
    @POST("api/userweekdays")
    Call<UserWeekday> createUserWeekday(@Body UserWeekday userWeekday);

    // Update an existing user weekday
    @PUT("api/userweekdays/{id}")
    Call<UserWeekday> updateUserWeekday(@Path("id") Long id, @Body UserWeekday userWeekday);

    // Delete a user weekday by ID
    @DELETE("api/userweekdays/{id}")
    Call<Void> deleteUserWeekday(@Path("id") Long id);
}
