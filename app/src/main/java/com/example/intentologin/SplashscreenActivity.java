package com.example.intentologin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.intentologin.databinding.ActivitySplashscreenBinding;

public class SplashscreenActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 2000; // Duración de la pantalla de inicio (2 segundos)
    private ActivitySplashscreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Iniciar la actividad principal después de un retraso
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashscreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Finaliza la actividad del splash screen
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
