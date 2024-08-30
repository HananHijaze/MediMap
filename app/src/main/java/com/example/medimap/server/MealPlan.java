package com.example.medimap.server;
import java.time.LocalDate;

public class MealPlan {

    private Long id;
    private Long customerID; // Foreign Key referencing User.id
    private Long mealID; // Foreign Key referencing Meal.mealID
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private String mealDay; // e.g., Sun, Mon, etc.
    private String mealTime; // e.g., breakfast, lunch, etc.

    // Constructors
    public MealPlan() {}

    public MealPlan(Long customerID, Long mealID, LocalDate weekStartDate, LocalDate weekEndDate, String mealDay, String mealTime) {
        this.customerID = customerID;
        this.mealID = mealID;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.mealDay = mealDay;
        this.mealTime = mealTime;
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

    public Long getMealID() {
        return mealID;
    }

    public void setMealID(Long mealID) {
        this.mealID = mealID;
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

    public String getMealDay() {
        return mealDay;
    }

    public void setMealDay(String mealDay) {
        this.mealDay = mealDay;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }
}

