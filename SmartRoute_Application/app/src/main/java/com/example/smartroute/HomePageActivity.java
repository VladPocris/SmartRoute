package com.example.smartroute;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ImageView logoImageView = findViewById(R.id.logoImageView);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logoImageView.startAnimation(fadeIn);

        MaterialButton startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {

            Intent intent = new Intent(HomePageActivity.this, PickLocationsActivity.class);
            startActivity(intent);
        });
    }
}
