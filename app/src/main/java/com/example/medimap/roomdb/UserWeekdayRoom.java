package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_weekday_table")
public class UserWeekdayRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private Long userId;
    private Long weekdayId;

    // Default constructor
    public UserWeekdayRoom() {}

    // Parameterized constructor
    public UserWeekdayRoom(Long userId, Long weekdayId) {
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

    @Override
    public String toString() {
        return "UserWeekdayRoom{" +
                "id=" + id +
                ", userId=" + userId +
                ", weekdayId=" + weekdayId +
                '}';
    }
}
