package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.medimap.server.User;

import java.util.Date;

@Entity(tableName = "user_table")
public class UserRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String email;
    private String name;
    private String password;
    private String gender;
    private int height;
    private int weight;
    private String birthDate;
    private String bodyType;
    private String goal;
    private int stepCountGoal;
    private int hydrationGoal;
    private String whereToWorkout;
    private String dietType;
    private int mealsPerDay;
    private int snacksPerDay;
    private int waterDefault;

    // Constructor
    public UserRoom( String email, String name, String password, String gender, int height, int weight,
                String birthDate, String bodyType, String goal, int stepCountGoal, int hydrationGoal,
                String whereToWorkout, String dietType, int mealsPerDay, int snacksPerDay, int waterDefault) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.birthDate = birthDate;
        this.bodyType = bodyType;
        this.goal = goal;
        this.stepCountGoal = stepCountGoal;
        this.hydrationGoal = hydrationGoal;
        this.whereToWorkout = whereToWorkout;
        this.dietType = dietType;
        this.mealsPerDay = mealsPerDay;
        this.snacksPerDay = snacksPerDay;
        this.waterDefault = waterDefault;
    }

    public UserRoom(User user){
        this.email = user.getEmail();
        this.name = user.getName();
        this.password = user.getPassword();
        this.gender = user.getGender();
        this.height = (int) user.getHeight();  // Assuming height is a double in User and int in UserRoom
        this.weight = (int) user.getWeight();  // Assuming weight is a double in User and int in UserRoom
        this.birthDate = user.getBirthDate().toString();  // Assuming you want to save birthDate as a String in Room
        this.bodyType = user.getBodyType();
        this.goal = user.getGoal();
        this.stepCountGoal = user.getStepcountgoal();
        this.hydrationGoal = user.getHydrationgoal();
        this.whereToWorkout = user.getWheretoworkout();
        this.dietType = user.getDietType();
        this.mealsPerDay = user.getMealsperday();
        this.snacksPerDay = user.getSnackesperday();
        this.waterDefault = user.getWaterDefault();
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public int getStepCountGoal() {
        return stepCountGoal;
    }

    public void setStepCountGoal(int stepCountGoal) {
        this.stepCountGoal = stepCountGoal;
    }

    public int getHydrationGoal() {
        return hydrationGoal;
    }

    public void setHydrationGoal(int hydrationGoal) {
        this.hydrationGoal = hydrationGoal;
    }

    public String getWhereToWorkout() {
        return whereToWorkout;
    }

    public void setWhereToWorkout(String whereToWorkout) {
        this.whereToWorkout = whereToWorkout;
    }

    public String getDietType() {
        return dietType;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
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

    public int getWaterDefault() {
        return waterDefault;
    }

    public void setWaterDefault(int waterDefault) {
        this.waterDefault = waterDefault;
    }
}
