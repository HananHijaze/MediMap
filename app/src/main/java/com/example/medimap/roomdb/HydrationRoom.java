package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.LocalDate;

@Entity(tableName = "hydration_table")
public class HydrationRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id; // Primary Key

    private Long customerId; // Foreign Key referencing User.email

    private Double drank; // Amount of water consumed (e.g., in liters)

    private LocalDate date; // LocalDate field

    // Constructors
    public HydrationRoom(Long customerId, Double drank, LocalDate date) {
        this.customerId = customerId;
        this.drank = drank;
        this.date = date;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Double getDrank() {
        return drank;
    }

    public void setDrank(Double drank) {
        this.drank = drank;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
