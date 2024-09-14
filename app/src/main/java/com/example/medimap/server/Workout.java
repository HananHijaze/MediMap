package com.example.medimap.server;


public class Workout {

    private Long workoutID;
    private String name;
    private String description;
    private Integer duration; // Duration in minutes
    private Integer repetitions;
    private Integer sets;
    private String location; // e.g., home, gym
    private String workoutType;

    // Constructors
    public Workout() {}

    public Workout(String name, String description, Integer duration, Integer repetitions, Integer sets, String location, String workoutType) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.repetitions = repetitions;
        this.sets = sets;
        this.location = location;
        this.workoutType = workoutType;
    }

    // Getters and Setters
    public Long getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(Long workoutID) {
        this.workoutID = workoutID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public String getWorkoutType() {
        return workoutType;
    }
}
