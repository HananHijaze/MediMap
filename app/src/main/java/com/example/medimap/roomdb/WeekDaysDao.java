package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WeekDaysDao {

    @Insert
    void insertWeekDays(WeekDaysRoom weekDays);

    @Update
    void updateWeekDays(WeekDaysRoom weekDays);

    @Delete
    void deleteWeekDays(WeekDaysRoom weekDays);

    @Query("SELECT * FROM weekdays_table WHERE id = :id LIMIT 1")
    WeekDaysRoom getWeekDaysById(Long id);

    @Query("SELECT * FROM weekdays_table")
    List<WeekDaysRoom> getAllWeekDays();

    @Query("DELETE FROM weekdays_table")
    void deleteAllWeekDays();
}
