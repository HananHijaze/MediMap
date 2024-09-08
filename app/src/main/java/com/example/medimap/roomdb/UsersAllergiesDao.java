package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UsersAllergiesDao {

    @Insert
    void insertUsersAllergies(UsersAllergiesRoom usersAllergies);

    @Update
    void updateUsersAllergies(UsersAllergiesRoom usersAllergies);

    @Delete
    void deleteUsersAllergies(UsersAllergiesRoom usersAllergies);

    @Query("SELECT * FROM users_allergies_table WHERE id = :id LIMIT 1")
    UsersAllergiesRoom getUsersAllergiesById(Long id);

    @Query("SELECT * FROM users_allergies_table")
    List<UsersAllergiesRoom> getAllUsersAllergies();

    @Query("DELETE FROM users_allergies_table")
    void deleteAllUsersAllergies();
}
