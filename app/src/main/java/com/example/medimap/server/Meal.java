package com.example.medimap.server;


public class Meal {

    private Long mealID;
    private String name;
    private Integer calories;
    private Double protein;
    private Double carbs;
    private Double fats;
    private String type; // e.g., breakfast, lunch, dinner, snack
    private String Diet_type;//vegetarian, vegan, keto, gluten-free, dairy-free,pescatarian

    // Constructors
    public Meal() {}

    public Meal(String name, Integer calories, Double protein, Double carbs, Double fats, String type, String diet_type) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.type = type;
        this.Diet_type= diet_type;
    }

    // Getters and Setters
    public Long getMealID() {
        return mealID;
    }

    public void setMealID(Long mealID) {
        this.mealID = mealID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiet_type() {
        return Diet_type;
    }

    public void setDiet_type(String diet_type) {
        this.Diet_type = diet_type;
    }
}
