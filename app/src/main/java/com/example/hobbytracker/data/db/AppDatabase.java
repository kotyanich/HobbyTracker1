package com.example.hobbytracker.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.hobbytracker.data.views.ActiveIdea;
import com.example.hobbytracker.data.views.ActiveProject;
import com.example.hobbytracker.data.views.ActiveTask;
import com.example.hobbytracker.data.model.Hobby;
import com.example.hobbytracker.data.model.Idea;
import com.example.hobbytracker.data.model.NotificationSettings;
import com.example.hobbytracker.data.model.Project;
import com.example.hobbytracker.data.model.Task;
import com.example.hobbytracker.data.model.TimeEntry;

@Database(
        entities = {
                Hobby.class,
                TimeEntry.class,
                Task.class,
                Project.class,
                Idea.class,
                NotificationSettings.class
        },
        views = {
                ActiveTask.class,
                ActiveIdea.class,
                ActiveProject.class
        },
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract HobbyDao hobbyDao();

    public abstract TaskDao taskDao();

    public abstract ProjectDao projectDao();

    public abstract TimeEntryDao timeEntryDao();

    public abstract IdeaDao ideaDao();

    public abstract NotificationSettingsDao notificationSettingsDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                            AppDatabase.class, "hobby_app.db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}