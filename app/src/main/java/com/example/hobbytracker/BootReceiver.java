package com.example.hobbytracker;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("HobbiesData", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString("HobbyList", null);
            if (json != null) {
                Type type = new TypeToken<ArrayList<Hobby>>() {
                }.getType();
                ArrayList<Hobby> hobbies = gson.fromJson(json, type);
                if (hobbies != null) {
                    for (Hobby hobby : hobbies) {
                        NotifSettings settings = hobby.getNotifSettings();
                        if (settings != null && settings.isEnabled() && !settings.getSelectedDays().isEmpty()) {
                            scheduleNotificationsOnBoot(context, hobby);
                        }
                    }
                }
            }
        }
    }

    private void scheduleNotificationsOnBoot(Context context, Hobby hobby) {
        NotifSettings settings = hobby.getNotifSettings();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (Integer dayOfWeek : settings.getSelectedDays()) {
            int requestCode = generateRequestCode(hobby, dayOfWeek);
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("HOBBY_NAME", hobby.getName());
            intent.putExtra("HOBBY_ID", hobby.hashCode());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, convertDayOfWeek(dayOfWeek));
            calendar.set(Calendar.HOUR_OF_DAY, settings.getHour());
            calendar.set(Calendar.MINUTE, settings.getMinute());
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_WEEK, 7);
            }

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, // Repeat weekly
                    pendingIntent
            );
        }
    }

    private int convertDayOfWeek(int day) {
        switch (day) {
            case MainActivity.SUNDAY: return Calendar.SUNDAY;
            case MainActivity.MONDAY: return Calendar.MONDAY;
            case MainActivity.TUESDAY: return Calendar.TUESDAY;
            case MainActivity.WEDNESDAY: return Calendar.WEDNESDAY;
            case MainActivity.THURSDAY: return Calendar.THURSDAY;
            case MainActivity.FRIDAY: return Calendar.FRIDAY;
            case MainActivity.SATURDAY: return Calendar.SATURDAY;
            default: return Calendar.MONDAY;
        }
    }

    private int generateRequestCode(Hobby hobby, int dayOfWeek) {
        return hobby.hashCode() * 10 + dayOfWeek;
    }
}

