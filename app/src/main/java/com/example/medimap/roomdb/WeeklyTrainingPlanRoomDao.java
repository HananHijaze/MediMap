package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface WeeklyTrainingPlanRoomDao {

    @Insert
    void insertWorkoutPlan(WeeklyTrainingPlanRoom workoutPlan);

    @Update
    void updateWorkoutPlan(WeeklyTrainingPlanRoom workoutPlan);

    @Query("SELECT * FROM workout_plan_table")
    List<WeeklyTrainingPlanRoom> getAllWorkoutPlans();

    @Query("DELETE FROM workout_plan_table WHERE id = :workoutPlanId")
    void deleteWorkoutPlan(Long workoutPlanId);

    @Query("SELECT * FROM workout_plan_table WHERE customerID = :customerId")
    List<WeeklyTrainingPlanRoom> getWorkoutPlansForWeek(Long customerId);

    @Query("SELECT * FROM workout_plan_table WHERE customerID = :customerId")
    List<WeeklyTrainingPlanRoom> getAllWorkoutPlansForCustomer(Long customerId);

    @Query("DELETE FROM workout_plan_table")
    void deleteAllWorkoutPlans();

}

