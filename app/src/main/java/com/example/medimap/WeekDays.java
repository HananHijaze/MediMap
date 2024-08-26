package com.example.medimap;


public class WeekDays {

    private Long id;
    private String dayName;

    // Constructors
    public WeekDays() {}

    public WeekDays(String dayName) {
        this.dayName = dayName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }
}

