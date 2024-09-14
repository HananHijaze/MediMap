package com.example.medimap.roomdb;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WorkoutDao {

    @Insert
    void insertWorkout(WorkoutRoom workout);

    @Query("SELECT * FROM workout_table")
    List<WorkoutRoom> getAllWorkouts();

    @Query("SELECT * FROM workout_table WHERE workoutID = :id LIMIT 1")
    WorkoutRoom getWorkoutById(Long id);


    @Query("DELETE FROM workout_table")
    void deletallWorkouts();


}
