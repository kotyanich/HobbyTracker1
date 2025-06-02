package com.example.hobbytracker.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hobbytracker.data.model.TimeEntry;

import java.util.List;

@Dao
public interface TimeEntryDao {
    @Insert
    long insert(TimeEntry timeEntry);

    @Query("SELECT * FROM time_entries")
    List<TimeEntry> getAll();

    @Query("SELECT SUM(time) FROM time_entries")
    Integer getTotalTime();

    @Delete
    void delete(TimeEntry timeEntry);
}
