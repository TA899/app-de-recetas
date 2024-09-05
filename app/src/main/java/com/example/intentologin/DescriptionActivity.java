package com.example.intentologin;


import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        // Recuperar el título,steps y ingredientes de la receta pasado desde MainActivity5
        String recipeTitle = getIntent().getStringExtra("RECIPE_TITLE");
        String recipeSteps = getIntent().getStringExtra("RECIPE_STEPS");
     String recipeingredients = getIntent().getStringExtra("RECIPE_INGREDIENTS");
        // Mostrar el título de la receta en un TextView
        TextView titleTextView = findViewById(R.id.titleDescriptionTextView);
        titleTextView.setText(recipeTitle);
        // Mostrar pasos de la receta
        TextView stepsTextView = findViewById(R.id.pasosTextView);
        stepsTextView.setText(recipeSteps);
       //Mostrar ingredientes
        TextView ingredientesTextView = findViewById(R.id.ingredientesTextView);
        ingredientesTextView.setText(recipeingredients);


    }



}
