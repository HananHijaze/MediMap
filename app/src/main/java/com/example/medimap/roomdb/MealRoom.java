package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_room")
public class MealRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String dietType;
    private int calories;
    private Double carbs;
    private Double fats;
    private String mealName;
    private Double protein;
    private String mealType;

    // Constructors
    public MealRoom(String dietType, int calories, Double carbs, Double fats, String mealName, Double protein, String mealType) {
        this.dietType = dietType;
        this.calories = calories;
        this.carbs = carbs;
        this.fats = fats;
        this.mealName = mealName;
        this.protein = protein;
        this.mealType = mealType;
    }

    // Getters and setters
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

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public Double getFats() {
        return fats;
    }

    public void setFats(Double fats) {
        this.fats = fats;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}
