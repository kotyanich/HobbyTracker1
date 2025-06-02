package com.example.hobbytracker.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notification_settings")
public class NotificationSettings {
    @PrimaryKey
    public long hobbyId;

    public String days;
    public int hour;
    public int minute;
    public boolean isEnabled;
}
