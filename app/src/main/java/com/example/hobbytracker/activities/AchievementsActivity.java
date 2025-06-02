package com.example.hobbytracker.activities;

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

import com.example.hobbytracker.adapters.AchievementsAdapter;
import com.example.hobbytracker.managers.AchievementsManager;
import com.example.hobbytracker.R;
import com.example.hobbytracker.data.db.AppDatabase;

public class AchievementsActivity extends AppCompatActivity {
    RecyclerView achievementsRecyclerView;
    AchievementsAdapter adapter;
    AchievementsManager achievementsManager;

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

        AppDatabase db = AppDatabase.getInstance(this);

        achievementsRecyclerView = findViewById(R.id.achievementsRecycler);
        achievementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        achievementsManager = new AchievementsManager(this, db);
        achievementsManager.loadAchievements();
        achievementsManager.updateStatistics();
        adapter = new AchievementsAdapter(this, achievementsManager);
        achievementsRecyclerView.setAdapter(adapter);

        ImageView home = findViewById(R.id.home);
        ImageView shop = findViewById(R.id.shop);
        shop.setOnClickListener(v -> {
            Intent intent = new Intent(this, Shop.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }
}