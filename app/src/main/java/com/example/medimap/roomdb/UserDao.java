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

    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    UserRoom getUserByEmail(String email);

    @Query("DELETE FROM user_table")
    void deleteAllUsers();

    @Query("SELECT * FROM user_table")
    List<UserRoom> getAllUsers();
}
