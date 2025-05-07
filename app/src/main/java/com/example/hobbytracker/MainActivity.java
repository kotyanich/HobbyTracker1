package com.example.hobbytracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements RecycleViewInterface, NotificationsInterface {

    ArrayList<Hobby> hobby = new ArrayList<>();
    ImageButton add;
    HobbyAdapter adapter;
    SharedPreferences sharedPreferences;
    String[] quotes;
    int[] mascots;
    private List<Integer> selectedDays = new ArrayList<>();
    Random random = new Random();
    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;
     int currHobbyPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        quotes = getResources().getStringArray(R.array.quotes);
        TextView bubbleText = findViewById(R.id.bubbleText);
        bubbleText.setText(getRandomQuote());
        mascots = new int[]{R.drawable.regular, R.drawable.peace, R.drawable.love1};
        ImageView mascot = findViewById(R.id.mascot);
        mascot.setImageResource(getRandomMascot());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        add = findViewById(R.id.addHobby);
        sharedPreferences = getSharedPreferences("HobbiesData", MODE_PRIVATE);
        loadHobbies();
        adapter = new HobbyAdapter(this, hobby, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageView logros = findViewById(R.id.logros);
        logros.setOnClickListener(v -> goToLogros());
        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение на уведомления необходимо для напоминаний о хобби", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void goToLogros() {
        Intent intent = new Intent(this, achievements.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private String getRandomQuote() {
        return quotes[random.nextInt(quotes.length)];
    }

    private int getRandomMascot() {
        return mascots[random.nextInt(mascots.length)];
    }

    public void GoToAddHobby(View v) {
        Intent intent = new Intent(this, add_hobby.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String hobbyName = data.getStringExtra("Hobby");
            int imageResId = data.getIntExtra("ImageResId", -1);
            if (hobbyName != null && !hobbyName.isEmpty() && imageResId != -1) {
                hobby.add(new Hobby(hobbyName, imageResId));
                saveHobbies();
                adapter.notifyDataSetChanged();
                AchievementsManager manager = new AchievementsManager(this);
                manager.loadAchievements();
                manager.updateStatistics();
                manager.saveAchievements();
            } else {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveHobbies() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(hobby);
        editor.putString("HobbyList", json);
        editor.apply();
    }

    private void loadHobbies() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("HobbyList", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Hobby>>() {
            }.getType();
            hobby = gson.fromJson(json, type);
        } else {
            hobby = new ArrayList<>();
        }
    }

    public void showAddNotifDialog(int position) {
        currHobbyPos = position;
        View dialogView = getLayoutInflater().inflate(R.layout.notif_dialog, null);

        NumberPicker hPicker = dialogView.findViewById(R.id.hours);
        NumberPicker minPicker = dialogView.findViewById(R.id.minutes);
        hPicker.setMinValue(0);
        hPicker.setMaxValue(23);
        hPicker.setWrapSelectorWheel(true);
        minPicker.setMinValue(0);
        minPicker.setMaxValue(59);
        minPicker.setWrapSelectorWheel(true);

        Hobby currHobby = hobby.get(position);
        NotifSettings settings = currHobby.getNotifSettings();
        if (settings == null) {
            settings = new NotifSettings();
            currHobby.setNotifSettings(settings);
        }
        hPicker.setValue(settings.getHour());
        minPicker.setValue(settings.getMinute());
        selectedDays = new ArrayList<>(settings.getSelectedDays());
        ImageView ok = dialogView.findViewById(R.id.ok);
        ImageView cancel = dialogView.findViewById(R.id.cancel);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        setupDaySelection(dialogView);
        NotifSettings finalSettings = settings;
        ok.setOnClickListener(v -> {
            finalSettings.setHour(hPicker.getValue());
            finalSettings.setMinute(minPicker.getValue());
            finalSettings.setSelectedDays(new ArrayList<>(selectedDays));
            finalSettings.setEnabled(true);
            scheduleNotifications(currHobby);
            Toast.makeText(this, "Уведомления установлены", Toast.LENGTH_SHORT).show();
            saveHobbies();
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void scheduleNotifications(Hobby hobby) {
        NotifSettings settings = hobby.getNotifSettings();
        if (!settings.isEnabled() || settings.getSelectedDays().isEmpty()) return;
        cancelExistingNotifications(hobby);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;
        for (Integer dayOfWeek : settings.getSelectedDays()) {
            int requestCode = generateRequestCode(hobby, dayOfWeek);
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("HOBBY_NAME", hobby.getName());
            intent.putExtra("HOBBY_ID", hobby.hashCode());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
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
            long triggerTime = calendar.getTimeInMillis();
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
            );


        }
    }

    private int convertDayOfWeek(int day) {
        switch (day) {
            case SUNDAY:
                return Calendar.SUNDAY;
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
            default:
                return Calendar.MONDAY;
        }
    }

    private int generateRequestCode(Hobby hobby, int dayOfWeek) {
        return hobby.hashCode() * 10 + dayOfWeek;
    }

    private void setupDaySelector(View dialogView, int layoutId, int notSelectedDayId, int selectedDayId, int dayOfWeek) {
        FrameLayout dayLayout = dialogView.findViewById(layoutId);
        ImageView day = dialogView.findViewById(notSelectedDayId);
        ImageView dayRed = dialogView.findViewById(selectedDayId);
        if (selectedDays.contains(dayOfWeek)) {
            day.setVisibility(View.GONE);
            dayRed.setVisibility(View.VISIBLE);
        } else {
            day.setVisibility(View.VISIBLE);
            dayRed.setVisibility(View.GONE);
        }
        dayLayout.setOnClickListener(v -> {
            if (selectedDays.contains(dayOfWeek)) {
                selectedDays.remove(Integer.valueOf(dayOfWeek));
                day.setVisibility(View.VISIBLE);
                dayRed.setVisibility(View.GONE);
            } else {
                selectedDays.add(dayOfWeek);
                day.setVisibility(View.GONE);
                dayRed.setVisibility(View.VISIBLE);
            }
        });
    }

    private void cancelExistingNotifications(Hobby hobby) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        for (int day = SUNDAY; day <= SATURDAY; day++) {
            int requestCode = generateRequestCode(hobby, day);
            Intent intent = new Intent(this, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

    private void setupDaySelection(View dialogView) {
        setupDaySelector(dialogView, R.id.monday, R.id.mon, R.id.mon_red, MONDAY);
        setupDaySelector(dialogView, R.id.tuesday, R.id.tue, R.id.tue_red, TUESDAY);
        setupDaySelector(dialogView, R.id.wednesday, R.id.wed, R.id.wed_red, WEDNESDAY);
        setupDaySelector(dialogView, R.id.thursday, R.id.thu, R.id.thu_red, THURSDAY);
        setupDaySelector(dialogView, R.id.friday, R.id.fri, R.id.fri_red, FRIDAY);
        setupDaySelector(dialogView, R.id.saturday, R.id.sat, R.id.sat_red, SATURDAY);
        setupDaySelector(dialogView, R.id.sunday, R.id.sun, R.id.sun_red, SUNDAY);
    }

    @Override
    public void OnItemClick(int position) {
        Hobby selectedHobby = hobby.get(position);
        String hobbyJson = new Gson().toJson(selectedHobby);
        Intent intent = new Intent(this, hobby_details.class);
        intent.putExtra("Hobby", hobbyJson);
        startActivity(intent);
    }

    @Override
    public void OnItemLongClick(int position) {
        Toast.makeText(this, "Удерживайте, чтобы удалить хобби", Toast.LENGTH_SHORT).show();
        hobby.remove(position);
        adapter.notifyItemRemoved(position);
        saveHobbies();
    }

    @Override
    public void onNotifClick(int position) {
        showAddNotifDialog(position);
    }
}