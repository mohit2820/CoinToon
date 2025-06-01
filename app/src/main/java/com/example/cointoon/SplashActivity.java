package com.example.cointoon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // code for splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // already existing user
                FirebaseAuth auth = FirebaseAuth.getInstance();

                if (auth.getCurrentUser() != null) {
                    // User is already signed in, navigate to main screen
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    // No user is signed in, navigate to login screen
                    startActivity(new Intent(SplashActivity.this, Login.class));
                    finish();
                }
            }
        },SPLASH_TIME_OUT);


    }


}