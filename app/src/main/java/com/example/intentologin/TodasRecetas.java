package com.example.intentologin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TodasRecetas extends Fragment {

    private RecyclerView rv1;
    private RecipeManager recipeManager;
    private String username;
    private AdaptadorReceta adaptadorReceta;
    private List<Recipe> recipeList;  // Lista completa de recetas
    private List<Recipe> filteredList;  // Lista filtrada de recetas
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_todas_recetas, container, false);

        // Recuperar el nombre de usuario del SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        username = preferences.getString("username", "");

        recipeManager = new RecipeManager(requireContext());

        // Inicializar listas
        recipeList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Configurar RecyclerView y Adaptador
        rv1 = rootView.findViewById(R.id.rv1);
        rv1.setLayoutManager(new LinearLayoutManager(requireContext()));
        adaptadorReceta = new AdaptadorReceta(filteredList);
        rv1.setAdapter(adaptadorReceta);

        // Configurar SearchView
        SearchView searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        // Actualizar recetas
        updaterecipe();

        return rootView;
    }

    public void updaterecipe() {
        // Verificar si el fragmento está adjunto a un contexto
        if (!isAdded()) {
            Log.e("TodasRecetas", "Fragment not attached to a context");
            return;
        }

        List<Recipe> recipes = recipeManager.getAllRecipes();
        Log.e("Todas recetas", "Se paso por aca");

        if (recipes != null && !recipes.isEmpty()) {
            recipeList.clear();
            recipeList.addAll(recipes);
            filteredList.clear();
            filteredList.addAll(recipes);
            adaptadorReceta.notifyDataSetChanged();
        } else {
            Toast.makeText(requireContext(), "No se encontraron recetas. Para crear presione CREAR RECETAS", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUsername(String newUsername) {
        this.username = newUsername;

        if (!isAdded()) {
            Log.e("RecetasPropias", "Fragment not attached to a context");
            return;
        }

        // Actualizar las recetas con el nuevo nombre de usuario
        updaterecipe();
    }

    private void filter(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(recipeList);
        } else {
            for (Recipe recipe : recipeList) {
                if (recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(recipe);
                }
            }
        }
        adaptadorReceta.notifyDataSetChanged();
    }

    private static class AdaptadorReceta extends RecyclerView.Adapter<AdaptadorReceta.AdaptadorRecetaHolder> {
        private final List<Recipe> recipeList;

        public AdaptadorReceta(List<Recipe> recipeList) {
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implementa lo que deseas que ocurra al hacer clic en un elemento de la lista
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DescriptionActivity.class);

                    // Pasar el título de la receta como dato adicional al Intent
                    intent.putExtra("RECIPE_TITLE", recipe.getTitle());
                    intent.putExtra("RECIPE_STEPS", recipe.getSteps());
                    intent.putExtra("RECIPE_INGREDIENTS", recipe.getIngredients());

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
            TextView tvNombre;
            ImageView imageView;
            Button ButtonAgendar;
            Context context;
            TextView tvingredientes;
            TextView tvpasos;
            TextView tvautor;

            public AdaptadorRecetaHolder(@NonNull View itemView) {
                super(itemView);
                context = itemView.getContext();
                // Inicializar las vistas del ViewHolder
                tvNombre = itemView.findViewById(R.id.tvnombre);
                ButtonAgendar = itemView.findViewById(R.id.ButtonAgendar);
                imageView = itemView.findViewById(R.id.imageView);
                tvingredientes = itemView.findViewById(R.id.tvingredientes);
                tvpasos = itemView.findViewById(R.id.tvpasos);
                tvautor = itemView.findViewById(R.id.tvautor);

                ButtonAgendar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Obtener el título de la receta desde el TextView tvNombre
                        String recipeTitle = tvNombre.getText().toString();
                        String recipeIngredients = tvingredientes.getText().toString();
                        String recipeSteps = tvpasos.getText().toString();

                        // Código para abrir la nueva actividad y enviar título
                        Intent intent = new Intent(context, Calendario.class);
                        intent.putExtra("RECIPE_TITLE", recipeTitle);
                        intent.putExtra("RECIPE_INGREDIENTS", recipeIngredients);
                        intent.putExtra("RECIPE_STEPS", recipeSteps);

                        context.startActivity(intent);
                    }
                });
            }
        }
    }
}
