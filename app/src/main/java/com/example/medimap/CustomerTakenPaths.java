package com.example.medimap;


import java.time.LocalDate;

public class CustomerTakenPaths {

    private Long id;
    private Path path;
    private User customer;
    private LocalDate dateOfPathTaken;
    private Integer rating;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
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
