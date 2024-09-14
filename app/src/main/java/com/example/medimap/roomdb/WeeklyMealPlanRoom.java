package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;
import java.util.Date;

@Entity(tableName = "meal_plan_table")
public class WeeklyMealPlanRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id; // Primary Key

    private Long customerID; // Foreign Key referencing User.id

    private Long mealID; // Foreign Key referencing Meal.mealID

    private Date createdDate;

    private int mealDay; // e.g., Sun, Mon, etc.

    private String mealTime; // e.g., breakfast, lunch, etc.

    // Constructors
    public WeeklyMealPlanRoom(Long customerID, Long mealID, Date createdDate,int mealDay, String mealTime) {
        this.customerID = customerID;
        this.mealID = mealID;
        this.createdDate=createdDate;
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

   public Date getCreatedDate() {
        return createdDate;
   }

   public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
   }

    public int getMealDay() {
        return mealDay;
    }

    public void setMealDay(int mealDay) {
        this.mealDay = mealDay;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }
}

