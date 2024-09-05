package com.example.intentologin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MisRecetas extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_recetas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        // Verificar si hay datos extras adjuntos al intent
        if (intent != null && intent.hasExtra("USERNAME")) {
            // Recuperar el valor asociado con la clave "PROFILEIMAGE"
            username = intent.getStringExtra("USERNAME");


        }



    }

    public void ver_recetas (View view){
        Intent intent = new Intent(this, MainActivity5.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }


}