package com.example.medimap.server;

public class UsersAllergies {

    private Long id;
    private Long userId;
    private Long allergyId;

    // Default constructor
    public UsersAllergies() {}

    // Parameterized constructor
    public UsersAllergies(Long userId, Long allergyId) {
        this.userId = userId;
        this.allergyId = allergyId;
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

    public Long getAllergyId() {
        return allergyId;
    }

    public void setAllergyId(Long allergyId) {
        this.allergyId = allergyId;
    }
}
