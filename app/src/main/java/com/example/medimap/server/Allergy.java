package com.example.medimap.server;

public class Allergy {

    private Long id;
    private String name;

    // Default constructor
    public Allergy() {}

    // Parameterized constructor
    public Allergy(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
