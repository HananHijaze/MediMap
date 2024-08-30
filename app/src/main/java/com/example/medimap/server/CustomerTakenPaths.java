package com.example.medimap.server;

import java.time.LocalDate;

public class CustomerTakenPaths {

    private Long id; // Primary Key
    private Long pathId; // Foreign key to Path table
    private Long customerId; // Foreign key to User table
    private LocalDate dateOfPathTaken;
    private Integer rating;

    // Constructors
    public CustomerTakenPaths() {}

    public CustomerTakenPaths(Long pathId, Long customerId, LocalDate dateOfPathTaken, Integer rating) {
        this.pathId = pathId;
        this.customerId = customerId;
        this.dateOfPathTaken = dateOfPathTaken;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getDateOfPathTaken() {
        return dateOfPathTaken;
    }

    public void setDateOfPathTaken(LocalDate dateOfPathTaken) {
        this.dateOfPathTaken = dateOfPathTaken;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
