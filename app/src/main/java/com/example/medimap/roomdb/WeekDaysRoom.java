package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "weekdays_table")
public class WeekDaysRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String dayName;

    // Default constructor
    public WeekDaysRoom() {}

    // Parameterized constructor
    public WeekDaysRoom(String dayName) {
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

    @Override
    public String toString() {
        return "WeekDaysRoom{" +
                "id=" + id +
                ", dayName='" + dayName + '\'' +
                '}';
    }
}
