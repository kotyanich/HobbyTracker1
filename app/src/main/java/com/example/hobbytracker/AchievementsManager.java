package com.example.hobbytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

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
            {50,100,150},
            {30,70,120},
            {10,25,50},
            {5,15,30},
    };

    private final Context context;
    private ArrayList<AchievementsData> achievements;

    public AchievementsManager(Context context) {
        this.context = context;
    }

    public void saveAchievements(){
        SharedPreferences prefs = context.getSharedPreferences("LogroDate", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(achievements);
        editor.putString("achievements", json);
        editor.apply();
    }
    public void loadAchievements(){
        SharedPreferences prefs = context.getSharedPreferences("LogroDate", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("achievements", null);
        Type type = new TypeToken<ArrayList<AchievementsData>>() {}.getType();
        if (json != null) achievements = gson.fromJson(json, type);
        else initializeAchievements();
    }
    private void initializeAchievements(){
        achievements = new ArrayList<>();
        String[] titles = context.getResources().getStringArray(R.array.logrosTitle);
        String[] bronzeDesc = context.getResources().getStringArray(R.array.logrosBronze);
        String[] silverDesc = context.getResources().getStringArray(R.array.logrosSilver);
        String[] goldDesc = context.getResources().getStringArray(R.array.logrosGold);
        for (int i = 0; i < titles.length; i++){
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

    public void updateStatistics(){
        int totalHobbies = countTotalHobbies();
        int totalGoals = countTotalGoals();
        int completedGoals = countCompletedGoals();
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

    public ArrayList<AchievementsData> getAchievements() {return achievements;}

    public void updateAchievement(int total, AchievementsData logro){
        logro.setCurrProgress(total);
        checkAchievementLevel(logro);
        saveAchievements();
    }

    private int countTotalHobbies(){
        Log.d("AchievementsManager", "countTotalHobbies called");
        SharedPreferences prefs = context.getSharedPreferences("HobbiesData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("HobbyList", "");
        Log.d("AchievementsManager", "Hobbies JSON: " + json);
        if (json.isEmpty()){
            Log.d("AchievementsManager", "No hobbies found");
            return 0;
        }
        Type type = new TypeToken<ArrayList<Hobby>>() {}.getType();
        ArrayList<Hobby> hobbies = gson.fromJson(json, type);
        int count = hobbies != null ? hobbies.size() : 0;
        Log.d("AchievementsManager", "Hobbies count: " + count);
        return count;
    }

    private int countTotalGoals(){
        int totalGoals = 0;
        SharedPreferences prefs = context.getSharedPreferences("HobbyTasks", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry: allEntries.entrySet()){
            {
                String json = (String) entry.getValue();
                Type type = new TypeToken<ArrayList<Task>>() {}.getType();
                ArrayList<Task> goals = new Gson().fromJson(json, type);
                if (goals != null) totalGoals += goals.size();
            }
        }
        return totalGoals;
    }

    private int countCompletedGoals(){
        int completedGoals = 0;
        SharedPreferences prefs = context.getSharedPreferences("HobbyTasks", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry: allEntries.entrySet()){
            {
                String json = (String) entry.getValue();
                Type type = new TypeToken<ArrayList<Task>>() {}.getType();
                ArrayList<Task> goals = new Gson().fromJson(json, type);
                if (goals != null) {
                    for (Task goal: goals){
                        if (goal.isCompleted()) completedGoals++;
                    }
                }
            }
        }
        return completedGoals;
    }

    private int countTotalProjects(){
        int totalProjects = 0;
        SharedPreferences prefs = context.getSharedPreferences("ProjectsData", Context.MODE_PRIVATE);
        Map<String,?> allEntries = prefs.getAll();
        for (Map.Entry<String,?> entry: allEntries.entrySet()){
            String json = (String) entry.getValue();
            Type type = new TypeToken<ArrayList<ProjectList>>() {}.getType();
            ArrayList<ProjectList> projectLists = new Gson().fromJson(json,type);
            if (projectLists != null) totalProjects += projectLists.size();
        }
        return totalProjects;
    }

    private int countCompletedProjects(){
        int completedProjects = 0;
        SharedPreferences prefs = context.getSharedPreferences("ProjectsData", Context.MODE_PRIVATE);
        Map<String,?> allEntries = prefs.getAll();
        for (Map.Entry<String,?> entry:allEntries.entrySet()){
            String json = (String) entry.getValue();
            Type type = new TypeToken<ArrayList<ProjectList>>() {}.getType();
            ArrayList<ProjectList> projectLists = new Gson().fromJson(json, type);
            if (projectLists != null){
                for (ProjectList projectList : projectLists){
                    SharedPreferences goalsPrefs = context.getSharedPreferences("GoalsData", Context.MODE_PRIVATE);
                    String goalsJson = goalsPrefs.getString("goals_" + projectList.getName(), null);
                    if (goalsJson != null){
                        Type goalsType = new TypeToken<ArrayList<Project>>() {}.getType();
                        ArrayList<Project> goals = new Gson().fromJson(goalsJson, goalsType);
                        if (goals != null && !goals.isEmpty()){
                            boolean allCompleted = true;
                            for (Project goal: goals){
                                if (!goal.isCompleted()){
                                    allCompleted = false;
                                    break;
                                }
                            }
                            if (allCompleted) completedProjects++;
                        }
                    }
                }
            }
        }
        return completedProjects;
    }

    private int calculateTotalTime(){
        int totalTime = 0;
        SharedPreferences prefs = context.getSharedPreferences("TimeData", Context.MODE_PRIVATE);
        Map<String,?> allEntries = prefs.getAll();
        for (Map.Entry<String,?> entry: allEntries.entrySet()){
            String json = (String) entry.getValue();
            Type type = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
            Map<String, ArrayList<String>> timeData = new Gson().fromJson(json, type);
            if (timeData != null){
                for (Map.Entry<String, ArrayList<String>> dateEntry : timeData.entrySet()){
                    ArrayList<String> timeEntries = dateEntry.getValue();
                    if (timeEntries != null){
                        for (String timeEntry : timeEntries){
                            totalTime += Integer.parseInt(timeEntry);
                        }
                    }
                }
            }
        }
        return (totalTime/60);
    }

    private void checkAchievementLevel(AchievementsData achievement){
        int progress = achievement.getCurrProgress();
        if (progress >= achievement.getGoldThreshold() && achievement.getCurrLevel() < goldLevel) achievement.setCurrLevel(goldLevel);
        else if (progress >= achievement.getSilverThreshold() && achievement.getCurrLevel() < silverLevel) achievement.setCurrLevel(silverLevel);
        else if (progress >= achievement.getBronzeThreshold() && achievement.getCurrLevel() < bronzeLevel) achievement.setCurrLevel(bronzeLevel);
    }
}