package com.example.hobbytracker.data.model;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @Nullable
    public Long hobbyId;         // FOR TO-DO CASES
    @Nullable
    public Long projectId;       // FOR PROJECTS CASES

    public String text;
    public String status;
    public boolean isDone;
}
