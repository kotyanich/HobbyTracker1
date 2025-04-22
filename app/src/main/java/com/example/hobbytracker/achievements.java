package com.example.hobbytracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class achievements extends AppCompatActivity {
    private RecyclerView logrosRecyclerView;
    private LogrosAdapter adapter;
    private AchievementsManager achievementsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_achievements);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        logrosRecyclerView = findViewById(R.id.logrosRecycler);
        logrosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        achievementsManager = new AchievementsManager(this);
        achievementsManager.loadAchievements();
        achievementsManager.updateStatistics();
        ArrayList<AchievementsData> logros = achievementsManager.getAchievements();
        adapter = new LogrosAdapter(this, logros);
        logrosRecyclerView.setAdapter(adapter);
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}