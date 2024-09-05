package com.example.intentologin;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity2 extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mEmailEditText;

    String profileImagePath;
    private Button mRegisterButton;
    private Switch mShowPasswordSwitch;
    private DatabaseHelper mDbHelper;

    private static final String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mUsernameEditText = findViewById(R.id.nombre);
        mPasswordEditText = findViewById(R.id.contraseñareg);
        mEmailEditText = findViewById(R.id.mail);
        mRegisterButton=findViewById(R.id.crearcuenta);
        mShowPasswordSwitch = findViewById(R.id.show_password_switch); // Cambio en la inicialización

        Intent intent = getIntent();

        // Verificar si hay datos extras adjuntos al intent
        if (intent != null && intent.hasExtra("PROFILEIMAGE")) {
            // Recuperar el valor asociado con la clave "PROFILEIMAGE"
             profileImagePath = intent.getStringExtra("PROFILEIMAGE");

        }

        Log.d("El  perfil foto usuario es", "El  perfil foto usuario es"+ profileImagePath +"");


        // Configurar el listener para el Switch
        mShowPasswordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Mostrar la contraseña
                    mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    // Ocultar la contraseña
                    mPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        mDbHelper = new DatabaseHelper(this);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public static boolean isValidEmail (String email) {
        return email != null && email.matches(emailPattern);
    }


    private void registerUser() {
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String email = mEmailEditText.getText().toString().trim();


        // Verificar si algún campo obligatorio está vacío
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return; // Detener el registro del usuario
        }

        if (!isValidEmail(email)){
            Toast.makeText(this, "Ingrese un email valido", Toast.LENGTH_SHORT).show();
            return; // Detener el registro del usuario
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Verificar si el nombre de usuario ya existe en la base de datos
        if (mDbHelper.checkUsernameExists(username)) {
            Toast.makeText(this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show();
            return; // Detener el registro del usuario
        }

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_USERNAME, username);
        values.put(UserContract.UserEntry.COLUMN_NAME_PASSWORD, password);
        values.put(UserContract.UserEntry.COLUMN_NAME_EMAIL, email);
        values.put(UserContract.UserEntry.COLUMN_NAME_PROFILE_IMAGE_PATH, profileImagePath);



    long newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e("RegisterUser", "Error al registrar usuario");
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("RegisterUser", "Usuario registrado con éxito");
            Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
            SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", username);
            editor.apply();

            // Redirigir a MainActivity3
            Intent intent = new Intent(this, MainActivity3.class);
            startActivity(intent);
            finish();
        }
    }






}


