package com.example.intentologin;

import android.content.Context;


import java.util.Collections;
import java.util.List;

public class RecipeManager {
    private DatabaseHelper dbHelper;

    public RecipeManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    //Funcion de la clase: Utiliza la base de datos para recuperar la info de receta
    public List<Recipe> getAllRecipesForUser(String username) {
        List<Recipe> listaforuser=dbHelper.getAllRecipesForUser(username);
        Collections.reverse(listaforuser);
        return listaforuser;
    }

    //Funcion de esto es para recuperar todas las recetas menos la del usuario actual
    public List<Recipe> getAllRecipesExceptCurrentUser(String username) {
        List<Recipe> listaforexceptuser;
        listaforexceptuser = dbHelper.getAllRecipesExceptCurrentUser(username);
        Collections.reverse(listaforexceptuser);
        return listaforexceptuser ;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = dbHelper.getAllRecipes();
        Collections.reverse(recipes);
        return recipes;
    }


}
