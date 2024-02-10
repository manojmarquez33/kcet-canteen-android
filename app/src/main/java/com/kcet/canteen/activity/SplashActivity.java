package com.kcet.canteen.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.kcet.canteen.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));

        RelativeLayout splash = findViewById(R.id.splash);
        ImageView image = findViewById(R.id.splash_image);

        splash.setBackgroundColor(getResources().getColor(R.color.white));
        image.setImageResource(R.mipmap.ic_launcher_round);

        Handler handler = new Handler();
        Runnable runnable = () -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        };

        handler.postDelayed(runnable, 2000);
    }
}