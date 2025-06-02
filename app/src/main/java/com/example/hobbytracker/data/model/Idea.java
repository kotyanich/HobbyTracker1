package com.example.hobbytracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ideas")
public class Idea {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long hobbyId;
    public String title;
    public String url;
    public String status;
}
