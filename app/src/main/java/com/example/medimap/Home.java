package com.example.medimap;
import com.bumptech.glide.Glide;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageButton map =findViewById(R.id.imageButton2);

        ImageView imageView = findViewById(R.id.waterImage);

        Glide.with(this)
                .asGif()
                .load(R.drawable.waterbottle2) // Replace with your GIF resource
                .into(imageView);
        ImageView sports= findViewById(R.id.imageView5);
        Glide.with(this)
                .asGif()
                .load(R.drawable.sports) // Replace with your GIF resource
                .into(sports);
        map.isClickable();
        map.setOnClickListener(view -> {
            Intent in = new Intent(this,Map.class);
            startActivity(in);
        });
    }
}