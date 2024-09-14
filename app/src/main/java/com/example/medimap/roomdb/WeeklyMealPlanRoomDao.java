package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import retrofit2.http.GET;

@Dao
public interface WeeklyMealPlanRoomDao {

    @Insert
    void insertMealPlan(WeeklyMealPlanRoom mealPlan);

    @Update
    void updateMealPlan(WeeklyMealPlanRoom mealPlan);

    @Query("SELECT * FROM meal_plan_table")
    List<WeeklyMealPlanRoom> getAllMealPlans();

    @Query("DELETE FROM meal_plan_table WHERE id = :mealPlanId")
    void deleteMealPlan(Long mealPlanId);

    @Query("SELECT * FROM meal_plan_table WHERE customerID = :customerId ")
    List<WeeklyMealPlanRoom> getAllMealPlansForCustomer(Long customerId);
}
