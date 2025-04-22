package com.example.hobbytracker;

public class Task {

    private final String taskName;
    private boolean isCompleted;
    public Task(String taskName) {
        this.taskName = taskName;
        this.isCompleted = false;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isCompleted(){
        return isCompleted;
    }
}
