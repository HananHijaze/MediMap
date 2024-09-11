package com.example.medimap.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insertUser(UserRoom user);

    @Update
    void updateUser(UserRoom user);

    @Delete
    void deleteUser(UserRoom user);

    @Query("SELECT * FROM user_table WHERE id = :userId LIMIT 1")
    UserRoom getUserById(Long userId);

    // Removed static keyword to allow Room to implement this method
    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    UserRoom getUserByEmail(String email);

    @Query("DELETE FROM user_table")
    void deleteAllUsers();

    @Query("SELECT * FROM user_table")
    List<UserRoom> getAllUsers();

    @Query("SELECT * FROM user_table ORDER BY id ASC LIMIT 1")
    UserRoom getFirstUser();


}
