package com.example.hobbytracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity implements RecycleViewInterface{

    ArrayList<Hobby> hobby = new ArrayList<>();
    ImageButton add;
    HobbyAdapter adapter;
    SharedPreferences sharedPreferences;
    String[] quotes;
    int[] mascots;
    Random random = new Random();

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
        adapter = new HobbyAdapter(this, hobby, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageView logros = findViewById(R.id.logros);
        logros.setOnClickListener(v -> goToLogros());
    }
    private void goToLogros(){
        Intent intent = new Intent(this, achievements.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private String getRandomQuote(){
        return quotes[random.nextInt(quotes.length)];
    }

    private int getRandomMascot(){
        return mascots[random.nextInt(mascots.length)];
    }
    public void GoToAddHobby(View v){
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
            }
            else {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveHobbies(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(hobby);
        editor.putString("HobbyList", json);
        editor.apply();
    }

    private void loadHobbies(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString("HobbyList", null);
        if (json != null){
            Type type = new TypeToken<ArrayList<Hobby>>(){}.getType();
            hobby = gson.fromJson(json, type);
        }
        else{
            hobby = new ArrayList<>();
        }
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
}