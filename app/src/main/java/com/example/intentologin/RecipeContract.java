package com.example.intentologin;

import android.provider.BaseColumns;

public class RecipeContract {

    private RecipeContract() {
        // Constructor privado para evitar instanciación accidental
    }

    public static final class RecipeEntry implements BaseColumns {
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_INGREDIENTS = "ingredients";
        public static final String COLUMN_NAME_STEPS = "steps";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_IMAGE_PATH = "image_path";
    }
}
