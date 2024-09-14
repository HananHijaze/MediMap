package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StepCountDao {

    @Insert
    void insertStepCount(StepCountRoom stepCount);

    @Update
    void updateStepCount(StepCountRoom stepCount);

    @Query("DELETE FROM step_count_table WHERE id = :stepCountId")
    void deleteStepCountById(Long stepCountId);

    @Query("SELECT * FROM step_count_table WHERE userId = :userId ORDER BY date DESC LIMIT 7")
    List<StepCountRoom> getLast7DaysStepCount(Long userId);

    @Query("DELETE FROM step_count_table WHERE userId = :userId")
    void deleteAllStepsForUser(Long userId);

    // Query to get the row with the latest date
    @Query("SELECT * FROM step_count_table WHERE date = (SELECT MAX(date) FROM step_count_table)")
    StepCountRoom getLatestStepCount();
}
