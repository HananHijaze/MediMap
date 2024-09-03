package com.example.medimap.server;

public class Pathloc {

    private Long id;
    private String name;
    private String description;
    private double startLatitude;
    private double startLongitude;
    private int difficulty;
    private Double rating;

    // Constructors
    public Pathloc() {}

    // Parameterized constructor
    public Pathloc(String name, String description, double startLatitude, double startLongitude, int difficulty) {
        this.name = name;
        this.description = description;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.difficulty = difficulty;
        this.rating = null;
    }

    // Getters and Setters

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
