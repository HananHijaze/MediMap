package com.example.medimap.server;

public class UserWeekday {

    private Long id; // Primary Key
    private Long userId; // Foreign key to User table
    private Long weekdayId; // Foreign key to Weekdays table

    // Constructors
    public UserWeekday() {}

    public UserWeekday(Long userId, Long weekdayId) {
        this.userId = userId;
        this.weekdayId = weekdayId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getWeekdayId() {
        return weekdayId;
    }

    public void setWeekdayId(Long weekdayId) {
        this.weekdayId = weekdayId;
    }
}
