package com.example.hobbytracker.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hobbytracker.data.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    long insert(Task task);

    @Update
    int update(Task task);

    @Query("SELECT * FROM tasks")
    List<Task> getAll();

    @Query("SELECT * FROM tasks WHERE projectId = :projectId AND status = 'active'")
    List<Task> getAllProjectTasks(long projectId);
    
    @Query("UPDATE tasks SET status = 'archived' WHERE projectId IN (SELECT id FROM projects WHERE hobbyId = :hobbyId)")
    void archiveAllProjectTasksByHobbyId(long hobbyId);

    @Query("UPDATE tasks SET status = 'archived' WHERE hobbyId = :hobbyId AND projectId IS NULL")
    void archiveAllHobbyTasksByHobbyId(long hobbyId);

    @Delete
    void delete(Task task);
}
