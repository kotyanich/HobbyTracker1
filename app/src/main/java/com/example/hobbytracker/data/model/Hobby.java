package com.example.hobbytracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hobbies")
public class Hobby {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public int iconId;
    public String status;
}
