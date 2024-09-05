package com.example.intentologin;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 3; // Versión incrementada para el cambio de esquema

    // Sentencia de creación de la tabla de usuarios existente (con el campo modificado)
    private static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserContract.UserEntry.COLUMN_NAME_USERNAME + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_PASSWORD + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    UserContract.UserEntry.COLUMN_NAME_PROFILE_IMAGE_PATH + " TEXT)";


    // Nueva sentencia de creación de la tabla de recetas
    private static final String SQL_CREATE_RECIPE_TABLE =
            "CREATE TABLE " + RecipeContract.RecipeEntry.TABLE_NAME + " (" +
                    RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    RecipeContract.RecipeEntry.COLUMN_NAME_TITLE + " TEXT," +
                    RecipeContract.RecipeEntry.COLUMN_NAME_INGREDIENTS + " TEXT," +
                    RecipeContract.RecipeEntry.COLUMN_NAME_STEPS + " TEXT," +
                    RecipeContract.RecipeEntry.COLUMN_NAME_USER_ID + " INTEGER, " +
                    RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE_PATH + " TEXT, " +
                    "FOREIGN KEY (" + RecipeContract.RecipeEntry.COLUMN_NAME_USER_ID + ") REFERENCES " +
                    UserContract.UserEntry.TABLE_NAME + "(" + UserContract.UserEntry._ID + ")" + ")";

    private static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

    private static final String SQL_DELETE_RECIPE_TABLE =
            "DROP TABLE IF EXISTS " + RecipeContract.RecipeEntry.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crea ambas tablas, de usuarios y de recetas
        db.execSQL(SQL_CREATE_USER_TABLE);
        db.execSQL(SQL_CREATE_RECIPE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Gestiona la lógica de actualización de la base de datos si es necesario según oldVersion
        // Por ahora, simplemente eliminamos y recreamos ambas tablas
        db.execSQL(SQL_DELETE_USER_TABLE);
        db.execSQL(SQL_DELETE_RECIPE_TABLE);
        onCreate(db);
    }



    public List<Recipe> getAllRecipesForUser(String username) {
        List<Recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Defino las columnas que recupero de la tabla
        String[] projection = {
                RecipeContract.RecipeEntry.TABLE_NAME + "." + RecipeContract.RecipeEntry._ID,
                RecipeContract.RecipeEntry.COLUMN_NAME_TITLE,
                RecipeContract.RecipeEntry.COLUMN_NAME_INGREDIENTS,
                RecipeContract.RecipeEntry.COLUMN_NAME_STEPS,
                RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE_PATH,
                UserContract.UserEntry.COLUMN_NAME_USERNAME
        };

        // Realizar una consulta JOIN para combinar las tablas de recetas y usuarios
        String selection = RecipeContract.RecipeEntry.COLUMN_NAME_USER_ID + " = " +
                UserContract.UserEntry.TABLE_NAME + "." + UserContract.UserEntry._ID +
                " AND " + UserContract.UserEntry.TABLE_NAME + "." + UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                RecipeContract.RecipeEntry.TABLE_NAME + " INNER JOIN " + UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // Iterar a través del cursor para obtener las recetas y el nombre del autor
        while (cursor.moveToNext()) {
            // Obtener los datos de la receta y el nombre del autor del cursor
            String title = cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_INGREDIENTS));
            String steps = cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_STEPS));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE_PATH));
            String author = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_USERNAME));

            // Crear un objeto Recipe y configurar sus atributos
            Recipe recipe = new Recipe(title, ingredients, steps, imagePath, author);
            // Agregar la receta a la lista
            recipeList.add(recipe);
        }

        // Cerrar el cursor después de usarlo
        cursor.close();

        return recipeList;
    }



    //Recuperar id del usuario
    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1; // Valor predeterminado en caso de que no se encuentre el usuario

        // Columna que queremos recuperar (en este caso, el ID del usuario)
        String[] projection = {UserContract.UserEntry._ID};

        // Cláusula WHERE para filtrar por el nombre de usuario
        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};

        // Ejecutar la consulta
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // Verificar si el cursor contiene datos
        if (cursor != null && cursor.moveToFirst()) {
            // Obtener el ID del usuario del cursor
            int idIndex = cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID);
            userId = cursor.getInt(idIndex);
            cursor.close(); // Cerrar el cursor después de usarlo
        }

        return userId;
    }

    //Query que recupera todas las recetas menos las del usuario actual
    public List<Recipe> getAllRecipesExceptCurrentUser(String username) {
        List<Recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Definir las columnas que deseas recuperar de la tabla de recetas
        String[] projection = {
                RecipeContract.RecipeEntry.TABLE_NAME + "." + RecipeContract.RecipeEntry._ID,
                RecipeContract.RecipeEntry.COLUMN_NAME_TITLE,
                RecipeContract.RecipeEntry.COLUMN_NAME_INGREDIENTS,
                RecipeContract.RecipeEntry.COLUMN_NAME_STEPS,
                RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE_PATH,
                // También queremos recuperar el nombre del autor
                UserContract.UserEntry.COLUMN_NAME_USERNAME
        };

        // Realizar una consulta JOIN para combinar las tablas de recetas y usuarios
        String selection = RecipeContract.RecipeEntry.COLUMN_NAME_USER_ID + " = " +
                UserContract.UserEntry.TABLE_NAME + "." + UserContract.UserEntry._ID +
                " AND " + UserContract.UserEntry.TABLE_NAME + "." + UserContract.UserEntry.COLUMN_NAME_USERNAME + " != ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                RecipeContract.RecipeEntry.TABLE_NAME + " INNER JOIN " + UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // Iterar a través del cursor para obtener las recetas y el nombre del autor
        while (cursor.moveToNext()) {
            // Obtener los datos de la receta y el nombre del autor del cursor
            String title = cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_INGREDIENTS));
            String steps = cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_STEPS));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE_PATH));
            String author = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_USERNAME));

            // Crear un objeto Recipe y configurar sus atributos
            Recipe recipe = new Recipe(title, ingredients, steps, imagePath, author);
            // Agregar la receta a la lista
            recipeList.add(recipe);
        }

        // Cerrar el cursor después de usarlo
        cursor.close();

        return recipeList;
    }


    public String getProfileImagePath(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {UserContract.UserEntry.COLUMN_NAME_PROFILE_IMAGE_PATH};
        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String profileImagePath = null;
        if (cursor.moveToFirst()) {
            profileImagePath = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_PROFILE_IMAGE_PATH));
        }
        cursor.close();
        return profileImagePath;
    }


    public boolean updateUsername(String currentUsername, String newUsername) {
        // Obtiene la referencia de la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        // Crea un objeto ContentValues para almacenar los nuevos valores
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_USERNAME, newUsername);

        // Define la cláusula WHERE para identificar el usuario que se va a actualizar
        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { currentUsername };

        // Actualiza el registro en la tabla con el nuevo nombre de usuario
        int count = db.update(
                UserContract.UserEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        // Verifica si se actualizó correctamente algún registro
        if (count > 0) {
            return true; // Indica que la actualización fue exitosa
        } else {
            return false; // Indica que no se realizó ninguna actualización (usuario no encontrado, etc.)
        }
    }


    public void updateProfileImagePathByUsername(String username, String newImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_PROFILE_IMAGE_PATH, newImagePath);

        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,         // Nombre de la tabla
                new String[] { UserContract.UserEntry._ID },   // Columnas que deseas consultar
                selection,                                 // Cláusula WHERE
                selectionArgs,                             // Valores para la cláusula WHERE
                null,                                     // No agrupar las filas
                null,                                     // No filtrar por grupos de filas
                null                                      // No ordenar las filas
        );

        long userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID));
            cursor.close();
        }

        if (userId != -1) {
            // Si se encuentra el usuario, actualiza la ruta de la imagen
            String updateSelection = UserContract.UserEntry._ID + " = ?";
            String[] updateSelectionArgs = { String.valueOf(userId) };

            int count = db.update(
                    UserContract.UserEntry.TABLE_NAME,
                    values,
                    updateSelection,
                    updateSelectionArgs);
        }

        db.close();
    }


    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {UserContract.UserEntry._ID};
        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    public List<Recipe> getAllRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Recipe> recipes = new ArrayList<>();

        // Consulta combinada (JOIN) entre las tablas Recipe y User
        String query = "SELECT " +
                "r." + RecipeContract.RecipeEntry.COLUMN_NAME_TITLE + ", " +
                "r." + RecipeContract.RecipeEntry.COLUMN_NAME_INGREDIENTS + ", " +
                "r." + RecipeContract.RecipeEntry.COLUMN_NAME_STEPS + ", " +
                "u." + UserContract.UserEntry.COLUMN_NAME_USERNAME + ", " +
                "r." + RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE_PATH +
                " FROM " + RecipeContract.RecipeEntry.TABLE_NAME + " r" +
                " JOIN " + UserContract.UserEntry.TABLE_NAME + " u" +
                " ON r." + RecipeContract.RecipeEntry.COLUMN_NAME_USER_ID + " = u." + UserContract.UserEntry._ID;

        Cursor cursor = db.rawQuery(query, null);

        // Verificar si el cursor contiene datos
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int titleIndex = cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE);
                int ingredientsIndex = cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_INGREDIENTS);
                int stepsIndex = cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_STEPS);
                int authorIndex = cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME_USERNAME);
                int imagePathIndex = cursor.getColumnIndexOrThrow(RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE_PATH);

                String title = cursor.getString(titleIndex);
                String ingredients = cursor.getString(ingredientsIndex);
                String steps = cursor.getString(stepsIndex);
                String author = cursor.getString(authorIndex);
                String imagePath = cursor.getString(imagePathIndex);

                Recipe recipe = new Recipe(title, ingredients, steps, imagePath, author);
                recipes.add(recipe); // Agregar la receta a la lista
            } while (cursor.moveToNext());

            cursor.close(); // Cerrar el cursor después de usarlo
        }

        return recipes;
    }






}

