package com.example.hobbytracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projects")
public class Project {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long hobbyId;
    public String name;
    public String status;
}
