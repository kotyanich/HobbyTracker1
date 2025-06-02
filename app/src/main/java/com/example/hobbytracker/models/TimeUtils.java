package com.example.hobbytracker.models;

import com.example.hobbytracker.data.model.TimeEntry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TimeUtils {

    public static int sumSeconds(List<TimeEntry> entries) {
        int total = 0;
        for (TimeEntry entry : entries) {
            total += entry.time;
        }
        return total;
    }

    public static int sumSecondsToday(List<TimeEntry> entries) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        int total = 0;
        for (TimeEntry entry : entries) {
            LocalDate entryDate = LocalDate.parse(entry.date, formatter);
            if (entryDate.isEqual(today)) {
                total += entry.time;
            }
        }
        return total;
    }

    public static int sumSecondsThisWeek(List<TimeEntry> entries) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays((today.getDayOfWeek().getValue() + 6) % 7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        int total = 0;
        for (TimeEntry entry : entries) {
            LocalDate entryDate = LocalDate.parse(entry.date, formatter);
            if (!entryDate.isBefore(monday)) {
                total += entry.time;
            }
        }
        return total;
    }

    public static int sumSecondsThisMonth(List<TimeEntry> entries) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        int total = 0;
        for (TimeEntry entry : entries) {
            LocalDate entryDate = LocalDate.parse(entry.date, formatter);
            if (!entryDate.isBefore(startOfMonth)) {
                total += entry.time;
            }
        }
        return total;
    }

    public static String getCurrentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }


    public static TimeParts breakdownSeconds(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return new TimeParts(hours, minutes, seconds);
    }

    public static class TimeParts {
        public final int hours;
        public final int minutes;
        public final int seconds;

        public TimeParts(int hours, int minutes, int seconds) {
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
        }
    }
}