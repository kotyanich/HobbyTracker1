package com.example.hobbytracker.listeners;

import com.example.hobbytracker.data.model.Task;

public interface OnTaskChangedListener {
    void onTaskCheckedChanged(Task task);

    void addTaskToProject(long projectId, String taskText);

    void deleteProjectTask(Task task);
}
