package com.example.intentologin;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private Button mRegisterButton; // Nuevo botón para abrir MainActivity2
    private DatabaseHelper mDbHelper;
    private Switch mShowPasswordSwitch; // Agregar Switch

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsernameEditText = findViewById(R.id.nombre_de_usuario);
        mPasswordEditText = findViewById(R.id.contraseña);
        mRegisterButton = findViewById(R.id.registrarse); // Referencia al nuevo botón
        mLoginButton = findViewById(R.id.ingresar);
        mShowPasswordSwitch = findViewById(R.id.show_password_switch);// Referencia al Switch
//Cambio1

        //Se instancia el objeto  a partir de la clase databsehelper
        mDbHelper = new DatabaseHelper(this);

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

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir MainActivity2 para el registro
                Intent intent = new Intent(MainActivity.this, Elegirfoto.class);
                startActivity(intent);

            }
        });
    }

    private void loginUser() {
        String username = mUsernameEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        // Verificar si los campos están vacíos
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            // Mostrar un mensaje de error si algún campo está vacío
            Toast.makeText(this, "Por favor, ingrese nombre de usuario y contraseña", Toast.LENGTH_SHORT).show();
            return; // Detener el proceso de inicio de sesión
        }

        //Se asegura el modo lectura de la base de datos
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Consulta de comprobacion de que si coinciden contraseña y usuario
        String[] projection = {
                UserContract.UserEntry._ID
        };

        String selection = UserContract.UserEntry.COLUMN_NAME_USERNAME + " = ? AND " +
                UserContract.UserEntry.COLUMN_NAME_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // Guardar el nombre de usuario independientemente del resultado del inicio de sesión. Ver si lo cambio de lugar para la memoria
        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.apply();

        // Verificar si el cursor es válido
        if (cursor != null && cursor.getCount() > 0) {
            // Usuario autenticado correctamente
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

            mUsernameEditText.setText("");
            mPasswordEditText.setText("");

            // Iniciar MainActivity3 y pasar el nombre de usuario
            Intent intent = new Intent(this, MainActivity3.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        } else {
            // Credenciales incorrectas
            Toast.makeText(this, "Nombre de Usuario o Contraseña Incorrectas", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

}



