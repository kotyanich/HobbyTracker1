package com.example.hobbytracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> hobbies = new ArrayList<>();
    ArrayList<Integer> hobbyImages = new ArrayList<>();
    ImageButton add;
    HobbyAdapter adapter;
    SharedPreferences sharedPreferences;

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
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        add = findViewById(R.id.addHobby);
        sharedPreferences = getSharedPreferences("HobbiesData", MODE_PRIVATE);
        loadHobbies();
        adapter = new HobbyAdapter(this, hobbies, hobbyImages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void GoToAddHobby(View v){
        Intent intent = new Intent(this, add_hobby.class);
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String hobby = data.getStringExtra("Hobby");
            int imageResId = data.getIntExtra("ImageResId", -1);
            if (hobby != null && !hobby.isEmpty() && imageResId != -1) {
                hobbies.add(hobby);
                hobbyImages.add(imageResId);
                saveHobbies();
                adapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveHobbies(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("HobbyCount", hobbies.size());
        for (int i =0; i < hobbies.size(); i++){
            editor.putString("Hobby_" + i, hobbies.get(i));
            editor.putInt("Image_" + i, hobbyImages.get(i));
        }
        editor.apply();
    }

    private void loadHobbies(){
        int hobbyCount = sharedPreferences.getInt("HobbyCount", 0);
        for (int i = 0; i < hobbyCount; i++){
            String hobby = sharedPreferences.getString("Hobby_" + i, "");
            int imageResId = sharedPreferences.getInt("Image_" + i, -1);
            if (!hobby.isEmpty() && imageResId != -1){
                hobbies.add(hobby);
                hobbyImages.add(imageResId);
            }
        }
    }
}