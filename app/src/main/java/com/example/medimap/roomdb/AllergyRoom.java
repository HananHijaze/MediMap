package com.example.medimap.roomdb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "allergy_table")
public class AllergyRoom {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String name;

    // Default constructor
    public AllergyRoom() {}

    // Parameterized constructor
    public AllergyRoom(Long id, String name) {
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

    @Override
    public String toString() {
        return "AllergyRoom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
