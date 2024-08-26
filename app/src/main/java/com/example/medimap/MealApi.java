package com.example.medimap;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MealApi {

    // Get a list of all meals
    @GET("api/meals")
    Call<List<Meal>> getAllMeals();

    // Get meal by ID
    @GET("api/meals/{id}")
    Call<Meal> getMealById(@Path("id") Long id);

    // Create a new meal
    @POST("api/meals")
    Call<Meal> createMeal(@Body Meal meal);

    // Update an existing meal
    @PUT("api/meals/{id}")
    Call<Meal> updateMeal(@Path("id") Long id, @Body Meal meal);

    // Delete a meal by ID
    @DELETE("api/meals/{id}")
    Call<Void> deleteMeal(@Path("id") Long id);
}
