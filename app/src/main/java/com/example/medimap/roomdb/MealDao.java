package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MealDao {

    @Insert
    void insertMeal(MealRoom mealPlan);

    // Query to get all meal plans
    @Query("SELECT * FROM meal_room")
    List<MealRoom> getAllMealPlans();

    // Query to get a specific meal plan by ID
    @Query("SELECT * FROM meal_room WHERE id = :mealId")
    MealRoom getMealById(Long mealId);

    // Query to delete all meal plans
    @Query("DELETE FROM meal_room")
    void deleteAllMealPlans();

}
