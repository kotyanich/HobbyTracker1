package com.example.hobbytracker.data.views;

import androidx.room.DatabaseView;

import com.example.hobbytracker.data.model.Task;

@DatabaseView("SELECT * FROM tasks WHERE status = 'active'")
public class ActiveTask extends Task {
    // CLASS TO FILTER ONLY ACTIVE TASKS
}
