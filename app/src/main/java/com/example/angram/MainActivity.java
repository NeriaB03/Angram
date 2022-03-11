package com.example.angram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    NetworkStatusReceiver networkStatusReceiver = new NetworkStatusReceiver();
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String myuid;
    BottomNavigationView navigationView;
    ActionBar actionBar;
    Intent backgroundMusic;
    FloatingActionButton mute;
    FloatingActionButton sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        mute = findViewById(R.id.muteFab);
        sound = findViewById(R.id.soundFab);

        actionBar = getSupportActionBar();

        backgroundMusic = new Intent(MainActivity.this, BackgroundMusic.class);
        startService(backgroundMusic);

        mute.setOnClickListener((view) -> {
            stopService(backgroundMusic);
            sound.setVisibility(View.VISIBLE);
            mute.setVisibility(View.INVISIBLE);
        });

        sound.setOnClickListener((view) -> {
            startService(backgroundMusic);
            sound.setVisibility(View.INVISIBLE);
            mute.setVisibility(View.VISIBLE);
        });

        HomeFragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content,fragment,"");
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        stopService(backgroundMusic);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStatusReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkStatusReceiver);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    if(actionBar != null) actionBar.setTitle("Home");
                    HomeFragment fragment = new HomeFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    return true;

                case R.id.nav_profile:
                    if(actionBar != null) actionBar.setTitle("Profile");
                    ProfileFragment fragment1 = new ProfileFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.content, fragment1);
                    fragmentTransaction1.commit();
                    return true;

                case R.id.nav_users:
                    if(actionBar != null) actionBar.setTitle("Users");
                    UsersFragment fragment2 = new UsersFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.commit();
                    return true;

                case R.id.nav_add:
                    if(actionBar != null) actionBar.setTitle("Add Post");
                    AddFragment fragment3 = new AddFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, fragment3, "");
                    fragmentTransaction3.commit();
                    return true;
            }
            return false;
        }
    };
}