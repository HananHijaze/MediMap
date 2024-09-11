package com.example.medimap.server;

public class MLData {

    private Long id;
    private String dietType;
    private String birthDate; // Keep as String for simplicity in Android; you can handle Date conversion if needed
    private String bodyType;
    private String gender;
    private String goal;
    private double height;
    private int mealsPerDay;
    private int snacksPerDay;
    private double weight;
    private String whereToWorkout;
    private String workoutPlan;
    private int breakfast;
    private int lunch;
    private int dinner;
    private int snack;

    // Default constructor
    public MLData() {
    }

    // Constructor
    public MLData(Long id, String dietType, String birthDate, String bodyType, String gender, String goal, double height, int mealsPerDay, int snacksPerDay, double weight, String whereToWorkout, String workoutPlan, int breakfast, int lunch, int dinner, int snack) {
        this.id = id;
        this.dietType = dietType;
        this.birthDate = birthDate;
        this.bodyType = bodyType;
        this.gender = gender;
        this.goal = goal;
        this.height = height;
        this.mealsPerDay = mealsPerDay;
        this.snacksPerDay = snacksPerDay;
        this.weight = weight;
        this.whereToWorkout = whereToWorkout;
        this.workoutPlan = workoutPlan;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
        this.snack = snack;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDietType() {
        return dietType;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getMealsPerDay() {
        return mealsPerDay;
    }

    public void setMealsPerDay(int mealsPerDay) {
        this.mealsPerDay = mealsPerDay;
    }

    public int getSnacksPerDay() {
        return snacksPerDay;
    }

    public void setSnacksPerDay(int snacksPerDay) {
        this.snacksPerDay = snacksPerDay;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getWhereToWorkout() {
        return whereToWorkout;
    }

    public void setWhereToWorkout(String whereToWorkout) {
        this.whereToWorkout = whereToWorkout;
    }

    public String getWorkoutPlan() {
        return workoutPlan;
    }

    public void setWorkoutPlan(String workoutPlan) {
        this.workoutPlan = workoutPlan;
    }

    public int getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(int breakfast) {
        this.breakfast = breakfast;
    }

    public int getLunch() {
        return lunch;
    }

    public void setLunch(int lunch) {
        this.lunch = lunch;
    }

    public int getDinner() {
        return dinner;
    }

    public void setDinner(int dinner) {
        this.dinner = dinner;
    }

    public int getSnack() {
        return snack;
    }

    public void setSnack(int snack) {
        this.snack = snack;
    }
}
