package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface WeeklyMealPlanRoomDao {

    @Insert
    void insertMealPlan(WeeklyMealPlanRoom mealPlan);

    @Update
    void updateMealPlan(WeeklyMealPlanRoom mealPlan);

    @Query("DELETE FROM meal_plan_table WHERE id = :mealPlanId")
    void deleteMealPlan(Long mealPlanId);

    @Query("SELECT * FROM meal_plan_table WHERE customerID = :customerId AND weekStartDate = :weekStartDate")
    List<WeeklyMealPlanRoom> getMealPlansForWeek(Long customerId, String weekStartDate);

    @Query("SELECT * FROM meal_plan_table WHERE customerID = :customerId ORDER BY weekStartDate DESC")
    List<WeeklyMealPlanRoom> getAllMealPlansForCustomer(Long customerId);
}
