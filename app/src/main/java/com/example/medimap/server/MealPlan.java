package com.example.medimap.server;
import java.time.LocalDate;
import java.util.Date;

public class MealPlan {

    private Long id;
    private Long customerID; // Foreign Key referencing User.id
    private Long mealID; // Foreign Key referencing Meal.mealID
    private Date creationdate;
    private String mealDay; // e.g., Sun, Mon, etc.
    private String mealTime; // e.g., breakfast, lunch, etc.

    // Constructors
    public MealPlan() {}

    public MealPlan(Long customerID, Long mealID,Date creationdate, String mealDay, String mealTime) {
        this.customerID = customerID;
        this.mealID = mealID;
        this.creationdate=creationdate;
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

    public Date getCreationdate() {
        return creationdate;
    }
    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
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

