package com.example.medimap;


import java.time.LocalDate;

public class Hydration {

    private Long id;
    private String customerEmail;
    private Double drank;
    private LocalDate date;

    // Constructors
    public Hydration() {}

    public Hydration(String customerEmail, Double drank, LocalDate date) {
        this.customerEmail = customerEmail;
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

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
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
