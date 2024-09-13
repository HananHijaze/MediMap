package com.example.medimap.server;


import java.util.Date;
import java.time.LocalDate;

public class WorkoutPlan {

    private Long id; // Primary Key
    private Long customerID; // Foreign Key referencing User.id
    private Long workoutID; // Foreign Key referencing Workout.workoutID
    private Date creationdate;
    private String workoutDay; // e.g., Mon, Tue, etc.

    // Constructors
    public WorkoutPlan() {}

    public WorkoutPlan(Long customerID, Long workoutID,Date creationdate, String workoutDay) {
        this.customerID = customerID;
        this.workoutID = workoutID;
        this.creationdate=creationdate;
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

    public Date getCreationdate() {
        return creationdate;
    }

    public void setWeekCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }
    public String getWorkoutDay() {
        return workoutDay;
    }

    public void setWorkoutDay(String workoutDay) {
        this.workoutDay = workoutDay;
    }
}
