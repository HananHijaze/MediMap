package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "step_count_table")
public class StepCountRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Long userId; // Foreign key to reference the User
    private int steps;
    private String date; // Store as String (e.g., "2024-09-01")

    // Constructor
    public StepCountRoom(Long userId, int steps, String date) {

        this.userId = userId;
        this.steps = steps;
        this.date = date;
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

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
