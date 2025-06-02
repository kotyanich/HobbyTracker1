package com.example.hobbytracker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbytracker.R;
import com.example.hobbytracker.adapters.ShopAdapter;
import com.example.hobbytracker.models.ShopItem;

import java.util.ArrayList;

public class Shop extends AppCompatActivity {
    ArrayList<ShopItem> shopItems = new ArrayList<>();
    TextView balanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

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
        balanceText = findViewById(R.id.balanceText);
        String setBalance = getString(R.string.money, getSavedBalance());
        updateBalanceText(getSavedBalance());
        balanceText.setText(setBalance);
        for (int i = 0; i < images.length; i++)
            shopItems.add(new ShopItem(images[i]));
        RecyclerView shopRecycler = findViewById(R.id.recycler);
        shopRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        shopRecycler.setAdapter(new ShopAdapter(shopItems, this, this, balanceText));
        ImageView home = findViewById(R.id.home);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        ImageView achievements = findViewById(R.id.achievements);
        achievements.setOnClickListener(v -> {
            Intent intent = new Intent(this, AchievementsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private int getSavedBalance() {
        SharedPreferences prefs = getSharedPreferences("AchData", MODE_PRIVATE);
        return prefs.getInt("Balance", 0);
    }

    public void updateBalanceText(int balance) {
        balanceText.setText(getString(R.string.money, balance));
    }

}