package com.example.hobbytracker.notifications;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class NotificationUtils {

    public static List<Integer> stringDaysToArray(String days) {
        List<Integer> selectedDays = new ArrayList<>();
        if (days == null || days.trim().isEmpty()) return selectedDays;
        for (String day : days.split(",")) {
            selectedDays.add(mapDayToCalendar(day.trim().toLowerCase()));
        }
        return selectedDays;
    }

    public static String selectedDaysToString(List<Integer> dayNumbers) {
        return dayNumbers.stream()
                .map(NotificationUtils::calendarIntToDayOfWeek)
                .map(day -> day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .collect(Collectors.joining(", "));
    }

    public static int dayOfWeekToCalendarInt(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY:
                return Calendar.MONDAY;
            case TUESDAY:
                return Calendar.TUESDAY;
            case WEDNESDAY:
                return Calendar.WEDNESDAY;
            case THURSDAY:
                return Calendar.THURSDAY;
            case FRIDAY:
                return Calendar.FRIDAY;
            case SATURDAY:
                return Calendar.SATURDAY;
            case SUNDAY:
                return Calendar.SUNDAY;
            default:
                throw new IllegalArgumentException("Invalid DayOfWeek: " + dayOfWeek);
        }
    }

    public static DayOfWeek calendarIntToDayOfWeek(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return DayOfWeek.SUNDAY;
            case Calendar.MONDAY:
                return DayOfWeek.MONDAY;
            case Calendar.TUESDAY:
                return DayOfWeek.TUESDAY;
            case Calendar.WEDNESDAY:
                return DayOfWeek.WEDNESDAY;
            case Calendar.THURSDAY:
                return DayOfWeek.THURSDAY;
            case Calendar.FRIDAY:
                return DayOfWeek.FRIDAY;
            case Calendar.SATURDAY:
                return DayOfWeek.SATURDAY;
            default:
                throw new IllegalArgumentException("Invalid calendar day: " + day);
        }
    }

    private static int mapDayToCalendar(String day) {
        switch (day) {
            case "sun":
                return Calendar.SUNDAY;
            case "mon":
                return Calendar.MONDAY;
            case "tue":
                return Calendar.TUESDAY;
            case "wed":
                return Calendar.WEDNESDAY;
            case "thu":
                return Calendar.THURSDAY;
            case "fri":
                return Calendar.FRIDAY;
            case "sat":
                return Calendar.SATURDAY;
            default:
                throw new IllegalArgumentException("Invalid day: " + day);
        }
    }


}