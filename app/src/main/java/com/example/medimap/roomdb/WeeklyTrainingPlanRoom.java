package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;
import java.util.Date;

@Entity(tableName = "workout_plan_table")
public class WeeklyTrainingPlanRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id; // Primary Key

    private Long customerID; // Foreign Key referencing User.id

    private Long workoutID; // Foreign Key referencing Workout.workoutID



    private int workoutDay;

    // Constructors
    public WeeklyTrainingPlanRoom(Long customerID, Long workoutID, int workoutDay) {
        this.customerID = customerID;
        this.workoutID = workoutID;
        this.workoutDay = workoutDay;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Long customerID) {
        this.customerID = customerID;
    }

    public Long getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(Long workoutID) {
        this.workoutID = workoutID;
    }

    public int getWorkoutDay() {
        return workoutDay;
    }

    public void setWorkoutDay(int workoutDay) {
        this.workoutDay = workoutDay;
    }
}
