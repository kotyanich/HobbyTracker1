package com.example.hobbytracker.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hobbytracker.data.model.Idea;

import java.util.List;

@Dao
public interface IdeaDao {
    @Insert
    long insert(Idea idea);

    @Query("SELECT * FROM ideas")
    List<Idea> getAll();

    @Query("UPDATE ideas SET status = 'archived' WHERE hobbyId = :hobbyId")
    void archiveAllByHobbyId(long hobbyId);

    @Update
    int update(Idea idea);

    @Delete
    void delete(Idea idea);
}
