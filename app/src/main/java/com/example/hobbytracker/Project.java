package com.example.hobbytracker;

public class Project{
    private final String projectName;
    private boolean isCompleted;

    public Project(String projectName) {
        this.projectName = projectName;
        this.isCompleted = false;
    }
    public String getProjectName() {
        return projectName;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

}
