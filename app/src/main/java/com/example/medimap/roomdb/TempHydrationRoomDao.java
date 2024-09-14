package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface TempHydrationRoomDao {
    @Insert
    void insertTempHydration(TempHydrationRoom tempHydration);

    @Update
    void updateTempHydration(TempHydrationRoom hydration);

    @Query("SELECT * FROM temp_hydration_table")
    List<TempHydrationRoom> getAllTempHydrations();

    @Query("DELETE FROM temp_hydration_table")
    void deleteAllTempHydration();

    @Query("SELECT * FROM temp_hydration_table WHERE date = :date")
    TempHydrationRoom getTempHydByDate(LocalDate date);
}

