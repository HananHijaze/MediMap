package com.example.medimap.server;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MealPlanApi {

    // Get a list of all meal plans
    @GET("mealplans")
    Call<List<MealPlan>> getAllMealPlans();

    // Get meal plan by ID
    @GET("mealplans/{id}")
    Call<MealPlan> getMealPlanById(@Path("id") Long id);

    // Create a new meal plan
    @POST("mealplans")
    Call<MealPlan> createMealPlan(@Body MealPlan mealPlan);

    // Update an existing meal plan
    @PUT("mealplans/{id}")
    Call<MealPlan> updateMealPlan(@Path("id") Long id, @Body MealPlan mealPlan);

    // Delete a meal plan by ID
    @DELETE("mealplans/{id}")
    Call<Void> deleteMealPlan(@Path("id") Long id);
}

