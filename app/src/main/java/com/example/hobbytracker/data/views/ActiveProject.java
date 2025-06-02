package com.example.hobbytracker.data.views;

import androidx.room.DatabaseView;

import com.example.hobbytracker.data.model.ProjectWithTasks;

@DatabaseView("SELECT * FROM projects WHERE status = 'active'")
public class ActiveProject extends ProjectWithTasks {
}
