package com.example.intentologin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity6 extends AppCompatActivity {

    private RecyclerView rv1;
    private RecipeManager recipeManager;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        // Obtener el nombre de usuario del intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");

        if (username != null) {
            recipeManager = new RecipeManager(this);

            // Obtener todas las recetas excepto las del usuario actual
            List<Recipe> recipeList = recipeManager.getAllRecipesExceptCurrentUser(username);

            if (recipeList != null && !recipeList.isEmpty()) {
            // Configurar el RecyclerView y el adaptador de recetas
            rv1 = findViewById(R.id.rv1);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            rv1.setLayoutManager(layoutManager);
            rv1.setAdapter(new AdaptadorReceta2(recipeList));
            } else {
                // Si la lista de recetas está vacía, mostrar un mensaje
                Toast.makeText(this, "No se encontraron recetas. Los Usuarios No crearon recetas todavia", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Manejar el caso en que el nombre de usuario sea nulo
            Log.e("Activity6", "El nombre de usuario es nulo");
            startActivity(new Intent(this, MainActivity.class)); // Vuelve a la actividad de inicio de sesión
        }
    }

    private static class AdaptadorReceta2 extends RecyclerView.Adapter<AdaptadorReceta2.AdaptadorRecetaHolder> {
        private final List<Recipe> recipeList;

        public AdaptadorReceta2(List<Recipe> recipeList) {
            this.recipeList = recipeList;
        }

        @NonNull
        @Override
        public AdaptadorRecetaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflar el diseño del elemento de la lista (layout_card.xml)
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tarjeta, parent, false);
            return new AdaptadorRecetaHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AdaptadorRecetaHolder holder, int position) {
            // Obtener los datos de la receta en la posición actual
            Recipe recipe = recipeList.get(position);

            // Actualizar las vistas del ViewHolder con los datos de la receta
            holder.tvNombre.setText(recipe.getTitle());
            holder.tvingredientes.setText(recipe.getIngredients());
            holder.tvpasos.setText(recipe.getSteps());
            holder.tvautor.setText(recipe.getAuthor());

            // Recuperar la ruta de la imagen desde la base de datos cuando se crea la actividad
            String imagePath = recipe.getImagePath();
            if (!TextUtils.isEmpty(imagePath)) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                holder.imageView.setImageBitmap(bitmap);
            }

            // Asignar clic del elemento del RecyclerView
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implementa lo que deseas que ocurra al hacer clic en un elemento de la lista
                    // Crear un Intent para abrir la DescriptionActivity
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DescriptionActivity.class);

                    // Pasar el título de la receta como dato adicional al Intent
                    intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                    intent.putExtra("RECIPE_STEPS", recipe.getSteps());
                    intent.putExtra("RECIPE_INGREDIENTS",recipe.getIngredients());


                    // Iniciar la DescriptionActivity
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return recipeList.size();
        }

        static class AdaptadorRecetaHolder extends RecyclerView.ViewHolder {
            TextView tvNombre; ImageView imageView; TextView tvingredientes;
            TextView tvpasos; Button ButtonAgendar; Context context; TextView tvautor;

            public AdaptadorRecetaHolder(@NonNull View itemView) {
                super(itemView);
                context = itemView.getContext();
                // Inicializar las vistas del ViewHolder
                tvNombre = itemView.findViewById(R.id.tvnombre);
                imageView = itemView.findViewById(R.id.imageView);
                ButtonAgendar = itemView.findViewById(R.id.ButtonAgendar);
                tvingredientes = itemView.findViewById(R.id.tvingredientes);
                tvpasos = itemView.findViewById(R.id.tvpasos);
                tvautor=itemView.findViewById(R.id.tvautor);

                ButtonAgendar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Obtener el título de la receta desde el TextView tvNombre
                        String recipeTitle = tvNombre.getText().toString();
                        String recipeIngredients= tvingredientes.getText().toString();
                        String recipeSteps=tvpasos.getText().toString();

                        //  código para abrir la nueva actividad y enviar titulo
                        Intent intent = new Intent(context, Calendario.class);
                        intent.putExtra("RECIPE_TITLE", recipeTitle);
                        intent.putExtra("RECIPE_INGREDIENTS", recipeIngredients);
                        intent.putExtra("RECIPE_STEPS",recipeSteps);

                        context.startActivity(intent);
                    }
                });

            }
        }
    }


}


