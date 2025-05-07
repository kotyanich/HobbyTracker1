package com.example.hobbytracker;

import java.util.ArrayList;
import java.util.List;

public class NotifSettings {
    private List<Integer> selectedDays;
    private int hour, minute;
    private boolean isEnabled;

    public NotifSettings() {
        this.hour = 12;
        this.isEnabled = false;
        this.minute = 0;
        this.selectedDays = new ArrayList<>();
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public List<Integer> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<Integer> selectedDays) {
        this.selectedDays = selectedDays;
    }
}
