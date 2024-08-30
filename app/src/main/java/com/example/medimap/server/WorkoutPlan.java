package com.example.medimap.server;


import java.time.LocalDate;

public class WorkoutPlan {

    private Long id; // Primary Key
    private Long customerID; // Foreign Key referencing User.id
    private Long workoutID; // Foreign Key referencing Workout.workoutID
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private String workoutDay; // e.g., Mon, Tue, etc.

    // Constructors
    public WorkoutPlan() {}

    public WorkoutPlan(Long customerID, Long workoutID, LocalDate weekStartDate, LocalDate weekEndDate, String workoutDay) {
        this.customerID = customerID;
        this.workoutID = workoutID;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
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

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public void setWeekStartDate(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
    }

    public LocalDate getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(LocalDate weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public String getWorkoutDay() {
        return workoutDay;
    }

    public void setWorkoutDay(String workoutDay) {
        this.workoutDay = workoutDay;
    }
}
