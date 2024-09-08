package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AllergyDao {

    @Insert
    void insertAllergy(AllergyRoom allergy);

    @Update
    void updateAllergy(AllergyRoom allergy);

    @Delete
    void deleteAllergy(AllergyRoom allergy);

    @Query("SELECT * FROM allergy_table WHERE id = :allergyId LIMIT 1")
    AllergyRoom getAllergyById(Long allergyId);

    @Query("SELECT * FROM allergy_table")
    List<AllergyRoom> getAllAllergies();

    @Query("DELETE FROM allergy_table")
    void deleteAllAllergies();
}
