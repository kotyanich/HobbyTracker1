package com.example.hobbytracker.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hobbytracker.data.model.NotificationSettings;

import java.util.List;

@Dao
public interface NotificationSettingsDao {
    @Insert
    long insert(NotificationSettings notification_settings);

    @Query("SELECT * FROM notification_settings")
    List<NotificationSettings> getAll();

    @Query("SELECT * FROM notification_settings WHERE hobbyId = :hobbyId")
    NotificationSettings getNotificationsForHobby(long hobbyId);

    @Update
    int update(NotificationSettings notification_settings);

    @Delete
    void delete(NotificationSettings notification_settings);
}
