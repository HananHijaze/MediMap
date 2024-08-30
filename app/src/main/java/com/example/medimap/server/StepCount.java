package com.example.medimap.server;

import java.time.LocalDate;

public class StepCount {

    private Long id; // Primary Key
    private Long customerId; // Foreign Key referencing User.email
    private Integer steps;
    private LocalDate date;

    // Constructors
    public StepCount() {}

    public StepCount(Long customerId, Integer steps, LocalDate date) {
        this.customerId = customerId;
        this.steps = steps;
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

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
