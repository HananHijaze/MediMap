package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users_allergies_table")
public class UsersAllergiesRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Long userId;
    private Long allergyId;

    // Default constructor
    public UsersAllergiesRoom() {}

    // Parameterized constructor
    public UsersAllergiesRoom(Long userId, Long allergyId) {
        this.userId = userId;
        this.allergyId = allergyId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(Long allergyId) {
        this.allergyId = allergyId;
    }

    @Override
    public String toString() {
        return "UsersAllergiesRoom{" +
                "id=" + id +
                ", userId=" + userId +
                ", allergyId=" + allergyId +
                '}';
    }
}
