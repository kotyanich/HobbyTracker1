package com.example.hobbytracker.ui;

import static com.example.hobbytracker.notifications.NotificationUtils.selectedDaysToString;
import static com.example.hobbytracker.notifications.NotificationUtils.stringDaysToArray;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
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

import com.example.hobbytracker.managers.AchievementsManager;
import com.example.hobbytracker.adapters.HobbyAdapter;
import com.example.hobbytracker.listeners.OnNotificationsListener;
import com.example.hobbytracker.R;
import com.example.hobbytracker.listeners.OnRecyclerViewActionListener;
import com.example.hobbytracker.data.db.AppDatabase;
import com.example.hobbytracker.data.model.DetailedHobby;
import com.example.hobbytracker.data.model.Hobby;
import com.example.hobbytracker.data.model.NotificationSettings;
import com.example.hobbytracker.notifications.NotificationScheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnRecyclerViewActionListener, OnNotificationsListener {

    ArrayList<DetailedHobby> hobbiesList;
    AppDatabase db;
    HobbyAdapter hobbyAdapter;
    String[] quotes;
    Random random = new Random();
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
        ImageView mascot = findViewById(R.id.mascot);
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        int[] images = {
                R.drawable.cooker,
                R.drawable.regular,
                R.drawable.rocker,
                R.drawable.painter,
                R.drawable.gardener,
                R.drawable.photographer,
                R.drawable.dancer
        };
        int savedMascot = prefs.getInt("Mascot", -1);
        if (savedMascot == -1) {
            savedMascot = images[1];
            prefs.edit().putInt("Mascot", savedMascot).apply();
        }
        mascot.setImageResource(savedMascot);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        ImageView add = findViewById(R.id.addHobby);

        // DATABASE INIT
        db = AppDatabase.getInstance(this);
//        this.deleteDatabase("hobby_app.db"); // TEST PURPOSE ONLY

        // LIST OF HOBBIES
        hobbiesList = new ArrayList<>();

        // UPDATE hobbiesList
        hobbiesList.addAll(db.hobbyDao().getAllDetailed());

        hobbyAdapter = new HobbyAdapter(this, hobbiesList, this, this);
        hobbyAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(hobbyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageView shop = findViewById(R.id.shop);
        shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, Shop.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 2);
        });
        ImageView achievements = findViewById(R.id.achievements);
        achievements.setOnClickListener(v -> goToAchievements());
        requestNotificationPermission();


    }

    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
        int savedMascot = prefs.getInt("Mascot", R.drawable.regular);
        ImageView mascot = findViewById(R.id.mascot);
        mascot.setImageResource(savedMascot);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
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

    private void goToAchievements() {
        Intent intent = new Intent(this, AchievementsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private String getRandomQuote() {
        return quotes[random.nextInt(quotes.length)];
    }

    public void GoToAddHobby(View v) {
        Intent intent = new Intent(this, AddHobbyActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // CREATING HOBBY
        if (requestCode == 1 && resultCode == RESULT_OK) {

            String hobbyName = data.getStringExtra("Hobby");
            int imageResId = data.getIntExtra("ImageResId", -1);

            if (hobbyName != null && !hobbyName.isEmpty() && imageResId != -1) {
                // INSERT HOBBY TO DATABASE
                Hobby newHobby = new Hobby();
                newHobby.name = hobbyName;
                newHobby.iconId = imageResId;
                newHobby.status = "active";

                db.hobbyDao().insert(newHobby);

                refreshHobbiesList();

                AchievementsManager manager = new AchievementsManager(this, db);
                manager.loadAchievements();
                manager.updateStatistics();
                manager.saveAchievements();

            } else {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }

        // CHOOSING MASCOT
        if (requestCode == 2 && resultCode == RESULT_OK) {
            int mascotImage = data.getIntExtra("Mascot", -1);
            if (mascotImage != -1) {
                ImageView mascot = findViewById(R.id.mascot);
                mascot.setImageResource(mascotImage);
                SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                prefs.edit().putInt("Mascot", mascotImage).apply();
            }
        }
    }

    public void showAddNotificationDialog(int position) {
        currHobbyPos = position;
        List<Integer> selectedDays = new ArrayList<>();

        // VIEWS
        View dialogView = getLayoutInflater().inflate(R.layout.notif_dialog, null);
        ImageView ok = dialogView.findViewById(R.id.ok);
        ImageView cancel = dialogView.findViewById(R.id.cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // CREATE TIME PICKERS
        NumberPicker hourPicker = dialogView.findViewById(R.id.hours);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        hourPicker.setWrapSelectorWheel(true);

        NumberPicker minutePicker = dialogView.findViewById(R.id.minutes);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setWrapSelectorWheel(true);

        // GET HOBBY NOTIFICATION SETTINGS
        DetailedHobby currentHobby = hobbiesList.get(position);
        NotificationSettings settings =
                db.notificationSettingsDao().getNotificationsForHobby(currentHobby.base.id);

        // HOBBY HASN'T SETTINGS
        if (settings == null) {
            settings = new NotificationSettings();
            settings.hobbyId = currentHobby.base.id;
            settings.days = "";
            settings.hour = 12;
            settings.minute = 0;
            settings.isEnabled = true;

            db.notificationSettingsDao().insert(settings);
            settings = db.notificationSettingsDao().getNotificationsForHobby(currentHobby.base.id);
        }

        // APPLY SETTINGS TO UI
        hourPicker.setValue(settings.hour);
        minutePicker.setValue(settings.minute);
        selectedDays.addAll(stringDaysToArray(settings.days));

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        setupDaySelection(dialogView, selectedDays);

        // FINAL NOTIFICATION SETTINGS
        NotificationSettings finalSettings = settings;

        // SAVE CHANGES
        ok.setOnClickListener(v -> {
            int hour = hourPicker.getValue();
            int minute = minutePicker.getValue();
            finalSettings.hour = hour;
            finalSettings.minute = minute;
            finalSettings.days = selectedDaysToString(selectedDays);
            // TODO: finalSettings.isEnabled = ...;

            // UPDATE SETTINGS IN DATABASE
            db.notificationSettingsDao().update(finalSettings);

            // CANCEL OLD NOTIFICATIONS
            NotificationScheduler.cancelAllNotificationsForHobby(this, currentHobby.base.id);

            // SCHEDULE NEW NOTIFICATIONS
            NotificationScheduler.scheduleWeeklyNotifications(
                    this,
                    currentHobby.base.id,
                    currentHobby.base.name,
                    selectedDays,
                    hour,
                    minute,
                    "Время позаниматься хобби!"
            );

            Toast.makeText(
                    this,
                    "Уведомления установлены",
                    Toast.LENGTH_SHORT
            ).show();

            dialog.dismiss();
        });

        // CANCEL CHANGES
        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void setupDaySelection(View dialogView, List<Integer> selectedDays) {
        setupDaySelector(dialogView, R.id.monday, R.id.mon, R.id.mon_red, Calendar.MONDAY, selectedDays);
        setupDaySelector(dialogView, R.id.tuesday, R.id.tue, R.id.tue_red, Calendar.TUESDAY, selectedDays);
        setupDaySelector(dialogView, R.id.wednesday, R.id.wed, R.id.wed_red, Calendar.WEDNESDAY, selectedDays);
        setupDaySelector(dialogView, R.id.thursday, R.id.thu, R.id.thu_red, Calendar.THURSDAY, selectedDays);
        setupDaySelector(dialogView, R.id.friday, R.id.fri, R.id.fri_red, Calendar.FRIDAY, selectedDays);
        setupDaySelector(dialogView, R.id.saturday, R.id.sat, R.id.sat_red, Calendar.SATURDAY, selectedDays);
        setupDaySelector(dialogView, R.id.sunday, R.id.sun, R.id.sun_red, Calendar.SUNDAY, selectedDays);
    }

    private void setupDaySelector(
            View dialogView,
            int layoutId,
            int notSelectedDayId,
            int selectedDayId,
            int dayOfWeek,
            List<Integer> selectedDays
    ) {
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

    private void refreshHobbiesList() {
        hobbiesList.clear();
        hobbiesList.addAll(db.hobbyDao().getAllDetailed());
        hobbyAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnItemClick(int position) {
        DetailedHobby selectedHobby = hobbiesList.get(position);
        long hobbyId = selectedHobby.base.id;

        Intent intent = new Intent(this, HobbyDetailsActivity.class);
        intent.putExtra("hobbyId", hobbyId);

        startActivity(intent);
    }

    @Override
    public void OnItemLongClick(int position) {
        Hobby hobby = hobbiesList.get(position).base;
        long hobbyId = hobby.id;

        // Cancel all notifications for this hobby
        NotificationScheduler.cancelAllNotificationsForHobby(this, hobbyId);

        // Archive all projects for this hobby
        db.projectDao().archiveAllHobbyProjects(hobbyId);

        // Archive all tasks in those projects
        db.taskDao().archiveAllProjectTasksByHobbyId(hobbyId);

        // Archive all hobby tasks (not in projects)
        db.taskDao().archiveAllHobbyTasksByHobbyId(hobbyId);

        // Archive all ideas for this hobby
        db.ideaDao().archiveAllByHobbyId(hobbyId);

        // Archive the hobby itself
        hobby.status = "archived";
        db.hobbyDao().update(hobby);

        Toast.makeText(this, "Хобби удалено!", Toast.LENGTH_SHORT).show();
        refreshHobbiesList();
    }

    @Override
    public void onNotificationClick(int position) {
        showAddNotificationDialog(position);
    }
}