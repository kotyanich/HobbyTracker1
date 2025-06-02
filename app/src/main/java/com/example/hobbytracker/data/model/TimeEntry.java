package com.example.hobbytracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "time_entries")
public class TimeEntry {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long hobbyId;
    public int time;
    public String date;
}
