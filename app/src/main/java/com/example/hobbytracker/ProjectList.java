package com.example.hobbytracker;

import java.util.ArrayList;

public class ProjectList {
    private String name;
    private ArrayList<Project> projects;

    public ProjectList(String name, ArrayList<Project> projects) {
        this.name = name;
        this.projects = projects;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }
}
