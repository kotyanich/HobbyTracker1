package com.example.hobbytracker.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.hobbytracker.data.model.Project;
import com.example.hobbytracker.data.model.ProjectWithTasks;

import java.util.List;

@Dao
public interface ProjectDao {
    @Insert
    long insert(Project project);

    @Update
    int update(Project project);

    @Query("SELECT * FROM projects")
    List<Project> getAll();

    @Transaction
    @Query("SELECT * FROM projects")
        // ACHIEVEMENTS
    List<ProjectWithTasks> getAllWithTasks();

    @Query("UPDATE projects SET status = 'archived' WHERE hobbyId = :hobbyId")
    void archiveAllHobbyProjects(long hobbyId);

    @Delete
    void delete(Project project);
}
