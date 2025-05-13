package com.example.hobbytracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        balanceText.setText(setBalance);
        for (int i =0; i < images.length; i++)
            shopItems.add(new ShopItem(images[i]));
        RecyclerView shopRecycler = findViewById(R.id.recycler);
        shopRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        shopRecycler.setAdapter(new ShopAdapter(shopItems));
    }
    private int getSavedBalance() {
        SharedPreferences prefs = getSharedPreferences("AchData", MODE_PRIVATE);
        return prefs.getInt("Balance", 0);
    }

}