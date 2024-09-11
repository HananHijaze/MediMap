package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserWeekdayDao {

    @Insert
    void insertUserWeekday(UserWeekdayRoom userWeekday);

    @Update
    void updateUserWeekday(UserWeekdayRoom userWeekday);

    @Delete
    void deleteUserWeekday(UserWeekdayRoom userWeekday);

    @Query("SELECT * FROM user_weekday_table WHERE id = :id LIMIT 1")
    UserWeekdayRoom getUserWeekdayById(Long id);

    @Query("SELECT * FROM user_weekday_table")
    List<UserWeekdayRoom> getAllUserWeekdays();

    @Query("DELETE FROM user_weekday_table")
    void deleteAllUserWeekdays();
}
