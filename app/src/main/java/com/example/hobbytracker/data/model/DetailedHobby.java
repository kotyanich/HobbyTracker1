package com.example.hobbytracker.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.hobbytracker.data.views.ActiveIdea;
import com.example.hobbytracker.data.views.ActiveProject;
import com.example.hobbytracker.data.views.ActiveTask;

import java.util.List;

public class DetailedHobby {
    @Embedded
    public Hobby base;

    @Relation(parentColumn = "id", entityColumn = "hobbyId", entity = ActiveProject.class)
    public List<ProjectWithTasks> projects;

    @Relation(parentColumn = "id", entityColumn = "hobbyId", entity = ActiveTask.class)
    public List<Task> hobbyTasks;

    @Relation(parentColumn = "id", entityColumn = "hobbyId")
    public List<TimeEntry> timeEntries;

    @Relation(parentColumn = "id", entityColumn = "hobbyId", entity = NotificationSettings.class)
    public NotificationSettings notificationSettings;

    @Relation(parentColumn = "id", entityColumn = "hobbyId", entity = ActiveIdea.class)
    public List<Idea> ideas;
}
