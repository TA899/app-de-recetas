package com.example.intentologin;


import android.provider.BaseColumns;

public final class UserContract {
    private UserContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_PROFILE_IMAGE_PATH = "profileimagepath";
    }
}
