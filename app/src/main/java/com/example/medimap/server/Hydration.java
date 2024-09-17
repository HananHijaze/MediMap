package com.example.medimap.server;

import com.example.medimap.roomdb.HydrationRoom;
import com.example.medimap.roomdb.TempHydrationRoom;

import java.time.LocalDate;

public class Hydration {

    private Long id; // Primary Key
    private Long customerId; // Foreign Key referencing User.email
    private Double drank; // Amount of water consumed (e.g., in liters)
    private LocalDate date;

    // Constructors
    public Hydration() {}

    public Hydration(Long customerId, Double drank, LocalDate date) {
        this.customerId = customerId;
        this.drank = drank;
        this.date = date;
    }

    public Hydration(HydrationRoom hydrationRoom){
        this.customerId = hydrationRoom.getCustomerId();
        this.drank = hydrationRoom.getDrank();
        this.date = hydrationRoom.getDate();
    }

    public Hydration(TempHydrationRoom tempHydrationRoom){
        this.customerId = tempHydrationRoom.getCustomerId();
        this.drank = tempHydrationRoom.getDrank();
        this.date = tempHydrationRoom.getDate();
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
