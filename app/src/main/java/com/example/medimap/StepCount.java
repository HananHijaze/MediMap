package com.example.medimap;


import java.time.LocalDate;

public class StepCount {

    private Long id;
    private String customerEmail;
    private Integer steps;
    private LocalDate date;

    // Constructors
    public StepCount() {}

    public StepCount(String customerEmail, Integer steps, LocalDate date) {
        this.customerEmail = customerEmail;
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

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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
