package com.example.hobbytracker;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hobby {
    public String name;
    public int image;
    private final ArrayList<Task> tasks;
    private final Map<String, List<String>> mapData;
    private NotifSettings notifSettings;

    public Hobby(String name, int image) {
        this.name = name;
        this.image = image;
        this.tasks = new ArrayList<>();
        this.mapData = new HashMap<>();
        this.notifSettings = new NotifSettings();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }


    public void setMapData(Map<String, ArrayList<String>> mapData){
        this.mapData.clear();
        this.mapData.putAll(mapData);
    }

    public int[] getTimeArray(int total){
        int hours;
        int minutes;
        int[] array = new int[2];
        if (total < 60){
            minutes = total;
            hours = 0;
        }
        else{
            hours = total/60;
            minutes = total - hours*60;
        }
        array[0] = hours;
        array[1] = minutes;
        return array;
    }
    public int[] sumTime(Map<String, ArrayList<String>> mapData){
        int totalTime = 0;
        if (mapData == null) return new int[]{0,0};
        int[] array = new int[2];
        for ( List<String> times : mapData.values()) {
            for (String time : times) {
                if (time!= null && !time.isEmpty()){
                    totalTime += Integer.parseInt(time);
                    array = getTimeArray(totalTime);
                }
                else array = new int[]{0,0};
            }
        }
        return array;
    }

    public int[] getDayTime(String date){
        int totalTime =0;
        int[] array = new int[2];
        if (mapData.containsKey(date)){
            for (String time : mapData.get(date)){
                totalTime += Integer.parseInt(time);
                array = getTimeArray(totalTime);
            }
        }
        return array;
    }

    public int[] getWeekTime(){
        LocalDate currDate = LocalDate.now();
        int totalTime = 0;
        int[] array = new int[2];
        for(int i = 0; i < 7; i++){
           LocalDate date = currDate.minusDays(i);
           String dateStr = date.toString();
           if (mapData.containsKey(dateStr)){
               for (String time : mapData.get(dateStr)){
                   totalTime += Integer.parseInt(time);
                   array = getTimeArray(totalTime);
               }
           }
        }
        return array;
    }

    public int[] getMonthTime(){
        LocalDate currDate = LocalDate.now();
        int totalTime = 0;
        int[] array = new int[2];
        YearMonth yearMonth = YearMonth.from(currDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        for(int i = 1; i<= daysInMonth; i++){
            LocalDate date = yearMonth.atDay(i);
            String dateStr = date.toString();
            if (mapData.containsKey(dateStr)){
                for (String time: mapData.get(dateStr)){
                    totalTime += Integer.parseInt(time);
                    array = getTimeArray(totalTime);
                }
            }
        }
        return array;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }


    public NotifSettings getNotifSettings() {
        return notifSettings;
    }

    public void setNotifSettings(NotifSettings notifSettings) {
        this.notifSettings = notifSettings;
    }
}
