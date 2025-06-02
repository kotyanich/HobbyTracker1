package com.example.hobbytracker.notifications;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationScheduler {

    /**
     * Schedule notifications for the given hobby on the specified days and time.
     *
     * @param context    Context
     * @param hobbyId    Hobby ID (for tagging/cancelling)
     * @param hobbyName  Hobby name (for notification)
     * @param daysOfWeek List of Calendar.DAY_OF_WEEK integers (e.g., Calendar.MONDAY)
     * @param hour       Hour of day (0-23)
     * @param minute     Minute (0-59)
     * @param message    Notification message
     */
    public static void scheduleWeeklyNotifications(
            Context context,
            long hobbyId,
            String hobbyName,
            List<Integer> daysOfWeek,
            int hour,
            int minute,
            String message
    ) {
        for (int dayOfWeek : daysOfWeek) {
            long delay = computeDelayUntil(dayOfWeek, hour, minute);
            Data data = new Data.Builder()
                    .putString("hobbyName", hobbyName)
                    .putString("message", message)
                    .build();

            String uniqueWorkName = "hobby_" + hobbyId + "_day_" + dayOfWeek;

            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag("hobby_" + hobbyId)
                    .addTag(uniqueWorkName)
                    .build();

            WorkManager.getInstance(context).enqueue(request);
        }
    }

    /**
     * Cancel all scheduled notifications for a hobby.
     *
     * @param context Context
     * @param hobbyId Hobby ID
     */
    public static void cancelAllNotificationsForHobby(Context context, long hobbyId) {
        WorkManager.getInstance(context).cancelAllWorkByTag("hobby_" + hobbyId);
    }

    /**
     * Cancel a specific notification for a hobby on a certain day.
     *
     * @param context   Context
     * @param hobbyId   Hobby ID
     * @param dayOfWeek Calendar.DAY_OF_WEEK
     */
    public static void cancelNotificationForDay(Context context, long hobbyId, int dayOfWeek) {
        String uniqueWorkName = "hobby_" + hobbyId + "_day_" + dayOfWeek;
        WorkManager.getInstance(context).cancelAllWorkByTag(uniqueWorkName);
    }

    /**
     * Compute delay in milliseconds until the next specified day and time.
     */
    private static long computeDelayUntil(int dayOfWeek, int hour, int minute) {
        Calendar now = Calendar.getInstance();
        Calendar next = (Calendar) now.clone();
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, minute);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        int today = now.get(Calendar.DAY_OF_WEEK);

        int daysUntil = (dayOfWeek - today + 7) % 7;
        if (daysUntil == 0 && next.before(now)) {
            daysUntil = 7;
        }
        next.add(Calendar.DAY_OF_YEAR, daysUntil);

        return next.getTimeInMillis() - now.getTimeInMillis();
    }
}