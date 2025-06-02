package com.example.hobbytracker.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hobbytracker.models.AchievementsData;
import com.example.hobbytracker.R;
import com.example.hobbytracker.data.db.AppDatabase;
import com.example.hobbytracker.data.model.Hobby;
import com.example.hobbytracker.data.model.Project;
import com.example.hobbytracker.data.model.ProjectWithTasks;
import com.example.hobbytracker.data.model.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AchievementsManager {
    private static final int bronzeLevel = 1;
    private static final int silverLevel = 2;
    private static final int goldLevel = 3;
    private static final int timeMaster = 0;
    private static final int hobbyManiac = 1;
    private static final int goalsCollector = 2;
    private static final int promises = 3;
    private static final int projCollector = 4;
    private static final int masterOfProjects = 5;

    private static final int[][] thresholds = {
            {10, 30, 50},
            {3, 5, 7},
            {50, 100, 150},
            {30, 70, 120},
            {10, 25, 50},
            {5, 15, 30},
    };

    private final Context context;
    private final AppDatabase db;
    private ArrayList<AchievementsData> achievements;

    public AchievementsManager(Context context, AppDatabase db) {
        this.context = context;
        this.db = db;
        initializeAchievements();
    }

    private void initializeAchievements() {
        achievements = new ArrayList<>();
        String[] titles = context.getResources().getStringArray(R.array.achievementsTitle);
        String[] bronzeDesc = context.getResources().getStringArray(R.array.achievementsBronze);
        String[] silverDesc = context.getResources().getStringArray(R.array.achievementsSilver);
        String[] goldDesc = context.getResources().getStringArray(R.array.achievementsGold);
        for (int i = 0; i < titles.length; i++) {
            AchievementsData achievement = new AchievementsData(
                    titles[i],
                    bronzeDesc[i],
                    silverDesc[i],
                    goldDesc[i],
                    0,
                    thresholds[i][0],
                    thresholds[i][1],
                    thresholds[i][2],
                    0
            );
            achievements.add(achievement);
        }
    }

    public void updateStatistics() {
        int totalHobbies = countTotalHobbies();
        int totalGoals = countTotalTasks();
        int completedGoals = countCompletedTasks();
        int totalProj = countTotalProjects();
        int completedProj = countCompletedProjects();
        int totalTime = calculateTotalTime();

        updateAchievement(totalTime, achievements.get(timeMaster));
        updateAchievement(totalHobbies, achievements.get(hobbyManiac));
        updateAchievement(totalGoals, achievements.get(goalsCollector));
        updateAchievement(completedGoals, achievements.get(promises));
        updateAchievement(totalProj, achievements.get(projCollector));
        updateAchievement(completedProj, achievements.get(masterOfProjects));
    }

    public ArrayList<AchievementsData> getAchievements() {
        return achievements;
    }

    public void updateAchievement(int total, AchievementsData achievement) {
        achievement.setCurrProgress(total);
        checkAchievementLevel(achievement);
    }

    private int countTotalHobbies() {
        List<Hobby> hobbies = db.hobbyDao().getAll();
        return hobbies != null ? hobbies.size() : 0;
    }

    private int countTotalTasks() {
        List<Task> allTasks = db.taskDao().getAll();
        return allTasks != null ? allTasks.size() : 0;
    }

    private int countCompletedTasks() {
        List<Task> allTasks = db.taskDao().getAll();
        int completedTasks = 0;

        if (allTasks != null) {
            for (Task task : allTasks) {
                if (task.isDone) {
                    completedTasks++;
                }
            }
        }

        return completedTasks;
    }

    private int countTotalProjects() {
        List<Project> projects = db.projectDao().getAll();
        return projects != null ? projects.size() : 0;
    }

    private int countCompletedProjects() {
        List<ProjectWithTasks> projects = db.projectDao().getAllWithTasks();
        int completedProjects = 0;

        if (projects != null) {
            for (ProjectWithTasks project : projects) {
                if (project.tasks != null && !project.tasks.isEmpty()) {
                    boolean allCompleted = true;

                    for (Task task : project.tasks) {
                        if (!task.isDone) {
                            allCompleted = false;
                            break;
                        }
                    }

                    if (allCompleted) completedProjects++;
                }
            }
        }
        return completedProjects;
    }

    private int calculateTotalTime() {
        Integer totalSeconds = db.timeEntryDao().getTotalTime();
        int totalHours = (totalSeconds != null ? totalSeconds : 0) / 3600;
        return totalHours;
    }

    private void checkAchievementLevel(AchievementsData achievement) {
        int progress = achievement.getCurrProgress();

        if (progress >= achievement.getGoldThreshold() && achievement.getCurrLevel() < goldLevel) {
            achievement.setCurrLevel(goldLevel);
        } else if (progress >= achievement.getSilverThreshold() && achievement.getCurrLevel() < silverLevel) {
            achievement.setCurrLevel(silverLevel);
        } else if (progress >= achievement.getBronzeThreshold() && achievement.getCurrLevel() < bronzeLevel) {
            achievement.setCurrLevel(bronzeLevel);
        }
    }

    public void saveAchievements() {
        SharedPreferences prefs = context.getSharedPreferences("LogroDate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(achievements);
        editor.putString("achievements", json);
        editor.apply();
    }

    public void loadAchievements() {
        SharedPreferences prefs = context.getSharedPreferences("LogroDate", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("achievements", null);
        Type type = new TypeToken<ArrayList<AchievementsData>>() {
        }.getType();
        if (json != null) achievements = gson.fromJson(json, type);
        else initializeAchievements();
    }
}