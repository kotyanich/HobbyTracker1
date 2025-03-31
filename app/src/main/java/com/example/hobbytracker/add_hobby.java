package com.example.hobbytracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class add_hobby extends AppCompatActivity {

    EditText ourHobby;
    ImageButton submit;
    ImageView ico1, ico2, ico3, ico4;
    int selectedImageResId = -1;
    ImageView selectedImageView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_hobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ourHobby = findViewById(R.id.ourHobby);
        submit = findViewById(R.id.submit);
        ico1 = findViewById(R.id.ico1);
        ico2 = findViewById(R.id.ico2);
        ico3 = findViewById(R.id.ico3);
        ico4 = findViewById(R.id.ico4);

        ico1.setOnClickListener(v -> selectImage(ico1, R.drawable.cactus_ico));
        ico2.setOnClickListener(v -> selectImage(ico2, R.drawable.roll_ico));
        ico3.setOnClickListener(v -> selectImage(ico3, R.drawable.draw_ico));
        ico4.setOnClickListener(v -> selectImage(ico4, R.drawable.guitar_ico));

        submit.setOnClickListener(v -> {
            String hobby = ourHobby.getText().toString().trim();
            if (selectedImageResId == -1){
                Toast.makeText(this, "Пожалуйста, выберите изображение", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!hobby.isEmpty()){
                Intent intent = new Intent();
                intent.putExtra("Hobby", hobby);
                intent.putExtra("ImageResId", selectedImageResId);
                setResult(AppCompatActivity.RESULT_OK, intent);
                finish();
            }
        });

    }

    private void selectImage(ImageView imageView, int imageResId){
        if (selectedImageView != null){
            selectedImageView.setBackgroundResource(0);
        }
        imageView.setBackgroundResource(R.drawable.check);
        selectedImageView = imageView;
        selectedImageResId = imageResId;
    }

    public void goToMainScreen(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}