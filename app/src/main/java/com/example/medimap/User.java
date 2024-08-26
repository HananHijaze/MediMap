package com.example.medimap;

import java.util.Date;

public class User {

    private Long id; // Primary Key
    private String email;
    private String name;
    private String password;
    private String gender;
    private double height;
    private double weight;
    private Date birthDate;
    private String bodyType;
    private String goal;
    private int stepcountgoal;
    private int hydrationgoal;
    private String wheretoworkout;
    private String dietType;
    private String allergies;
    private int mealsperday;
    private int snackesperday;

    // Constructors
    public User() {}

    public User(Long id, String email, String name, String password, String gender, double height, double weight, Date birthDate,
                String bodyType, String goal, int stepcountgoal, int hydrationgoal, String wheretoworkout, String dietType,
                String allergies, int mealsperday, int snackesperday) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.birthDate = birthDate;
        this.bodyType = bodyType;
        this.goal = goal;
        this.stepcountgoal = stepcountgoal;
        this.hydrationgoal = hydrationgoal;
        this.wheretoworkout = wheretoworkout;
        this.dietType = dietType;
        this.allergies = allergies;
        this.mealsperday = mealsperday;
        this.snackesperday = snackesperday;
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

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
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

    public int getStepcountgoal() {
        return stepcountgoal;
    }

    public void setStepcountgoal(int stepcountgoal) {
        this.stepcountgoal = stepcountgoal;
    }

    public int getHydrationgoal() {
        return hydrationgoal;
    }

    public void setHydrationgoal(int hydrationgoal) {
        this.hydrationgoal = hydrationgoal;
    }

    public String getWheretoworkout() {
        return wheretoworkout;
    }

    public void setWheretoworkout(String wheretoworkout) {
        this.wheretoworkout = wheretoworkout;
    }

    public String getDietType() {
        return dietType;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public int getMealsperday() {
        return mealsperday;
    }

    public void setMealsperday(int mealsperday) {
        this.mealsperday = mealsperday;
    }

    public int getSnackesperday() {
        return snackesperday;
    }

    public void setSnackesperday(int snackesperday) {
        this.snackesperday = snackesperday;
    }
}
