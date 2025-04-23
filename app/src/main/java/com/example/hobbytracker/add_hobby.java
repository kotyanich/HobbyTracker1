package com.example.hobbytracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
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
    ImageView ico1, ico2, ico3, ico4, ico1Red, ico2Red, ico3Red, ico4Red;
    FrameLayout guitar, cactus, drawing, crocheting;
    int selectedImageResId = -1;
    ImageView selectedIcoRed = null;
    FrameLayout selectedLayout = null;

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
        ico1Red = findViewById(R.id.ico1_red);
        ico2Red = findViewById(R.id.ico2_red);
        ico3Red = findViewById(R.id.ico3_red);
        ico4Red = findViewById(R.id.ico4_red);
        cactus = findViewById(R.id.cactus);
        guitar = findViewById(R.id.guitar);
        crocheting = findViewById(R.id.crocheting);
        drawing = findViewById(R.id.drawing);
        ImageView logros = findViewById(R.id.logrosBut);

        ico1.setOnClickListener(v -> selectImage(cactus, ico1Red, R.drawable.select_icon_frame));
        ico2.setOnClickListener(v -> selectImage(crocheting, ico2Red, R.drawable.crocheting_red));
        ico3.setOnClickListener(v -> selectImage(drawing, ico3Red, R.drawable.drawing_red));
        ico4.setOnClickListener(v -> selectImage(guitar, ico4Red, R.drawable.guitar_red));
        logros.setOnClickListener(v ->{
            Intent intent = new Intent(this, achievements.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

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

    private void selectImage(FrameLayout layout, ImageView icoRed, int imageResId){
        if (selectedLayout != null && selectedIcoRed!=null){
            selectedIcoRed.setVisibility(View.GONE);
        }
        icoRed.setImageResource(imageResId);
        icoRed.setVisibility(View.VISIBLE);
        selectedIcoRed = icoRed;
        selectedLayout = layout;
        selectedImageResId = imageResId;
    }

    public void goToMainScreen(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}