package com.example.angram;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    private Intent backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();

        backgroundMusic = new Intent(SplashScreen.this, BackgroundMusic.class);
        startService(backgroundMusic);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseHandler.firebaseAuth != null) {
                    if(FirebaseHandler.firebaseAuth.getCurrentUser() != null) {
                        Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(mainIntent);
                    } else {
                        Intent loginIntent = new Intent(SplashScreen.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                } else {
                    Intent loginIntent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        Log.d("Exit111","Exit");
        stopService(backgroundMusic);
        super.onDestroy();
    }
}