package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface HydrationRoomDao {

    @Insert
    void insertHydration(HydrationRoom hydration);

    @Update
    void updateHydration(HydrationRoom hydration);

    @Query("DELETE FROM hydration_table WHERE id = :hydrationId")
    void deleteHydration(Long hydrationId);

    @Query("SELECT * FROM hydration_table WHERE customerId = :customerId AND date = :date")
    HydrationRoom getHydrationByDate(Long customerId, String date);

    @Query("SELECT * FROM hydration_table WHERE customerId = :customerId ORDER BY date DESC")
    List<HydrationRoom> getAllHydrationsForCustomer(Long customerId);

    @Query("DELETE FROM hydration_table WHERE date = (SELECT MIN(date) FROM hydration_table)")
    void deleteOldestHydration();
}

