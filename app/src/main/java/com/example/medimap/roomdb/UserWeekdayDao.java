package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserWeekdayDao {

    // Insert a new user weekday record
    @Insert
    void insertUserWeekday(UserWeekdayRoom userWeekday);

    // Update an existing user weekday record
    @Update
    void updateUserWeekday(UserWeekdayRoom userWeekday);

    // Delete a specific user weekday record
    @Delete
    void deleteUserWeekday(UserWeekdayRoom userWeekday);

    // Retrieve a specific user weekday record by ID
    @Query("SELECT * FROM user_weekday_table WHERE id = :id LIMIT 1")
    UserWeekdayRoom getUserWeekdayById(Long id);

    // Retrieve all user weekday records
    @Query("SELECT * FROM user_weekday_table")
    List<UserWeekdayRoom> getAllUserWeekdays();


    // Retrieve all weekday records for a specific user
    @Query("SELECT * FROM user_weekday_table WHERE userId = :userId")
    List<UserWeekdayRoom> getAllUserWeekdaysForUser(Long userId);

    // Delete all user weekday records from the table
    @Query("DELETE FROM user_weekday_table")
    void deleteAllUserWeekdays();


}
