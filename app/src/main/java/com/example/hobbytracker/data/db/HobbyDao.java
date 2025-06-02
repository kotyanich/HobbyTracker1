package com.example.hobbytracker.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.hobbytracker.data.model.DetailedHobby;
import com.example.hobbytracker.data.model.Hobby;

import java.util.List;

@Dao
public interface HobbyDao {
    @Insert
    long insert(Hobby hobby);

    @Query("SELECT * FROM hobbies")
    List<Hobby> getAll();

    @Query("SELECT * FROM hobbies WHERE status='active'")
    List<DetailedHobby> getAllDetailed();

    @Transaction
    @Query("SELECT * FROM hobbies WHERE id = :hobbyId")
    DetailedHobby getDetailedHobby(long hobbyId);

    @Update
    int update(Hobby hobby);

    @Delete
    void delete(Hobby hobby);
}
