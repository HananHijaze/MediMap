package com.example.medimap;


public class UserWeekday {

    private Long id;
    private User user;
    private WeekDays weekday;

    // Constructors
    public UserWeekday() {}

    public UserWeekday(User user, WeekDays weekday) {
        this.user = user;
        this.weekday = weekday;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WeekDays getWeekday() {
        return weekday;
    }

    public void setWeekday(WeekDays weekday) {
        this.weekday = weekday;
    }
}
