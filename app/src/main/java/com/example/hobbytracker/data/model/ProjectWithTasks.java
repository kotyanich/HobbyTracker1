package com.example.hobbytracker.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.hobbytracker.data.views.ActiveTask;

import java.util.List;

public class ProjectWithTasks {
    @Embedded
    public Project base;

    @Relation(parentColumn = "id", entityColumn = "projectId", entity = ActiveTask.class)
    public List<Task> tasks;
}
