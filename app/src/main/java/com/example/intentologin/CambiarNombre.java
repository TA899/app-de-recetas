package com.example.intentologin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CambiarNombre extends AppCompatActivity {

    private EditText mNewUsernameEditText;
    private Button mChangeUsernameButton;
    private String mCurrentUsername;

    DatabaseHelper dbHelper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_nombre);

        // Inicializar vistas
        mNewUsernameEditText = findViewById(R.id.editTextNewUsername);
        mChangeUsernameButton = findViewById(R.id.buttonChangeUsername);

        // Obtener el nombre de usuario actual de SharedPreferences
        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        mCurrentUsername = preferences.getString("username", "");

        // Configurar el botón para cambiar el nombre de usuario
        mChangeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername();
            }
        });
    }

    private void changeUsername() {
        String newUsername = mNewUsernameEditText.getText().toString().trim();
        // Verificar si el nuevo nombre de usuario es válido
        if (!TextUtils.isEmpty(newUsername)  && !dbHelper.checkUsernameExists(newUsername)) {
            // Devolver el nuevo nombre de usuario a MainActivity3
            Intent resultIntent = new Intent();
            resultIntent.putExtra("NEW_USERNAME", newUsername);
            setResult(RESULT_OK, resultIntent);
            Log.d("CambiarNombre", "Nuevo nombre de usuario enviado a MainActivity3: " + newUsername);

            // Actualizar el nombre de usuario en SharedPreferences
            SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", newUsername);
            editor.apply();
            Log.d("CambiarNombre", "Nuevo nombre de usuario guardado en SharedPreferences: " + newUsername);

            // Actualizar el nombre de usuario en la base de datos

            boolean updateSuccess = dbHelper.updateUsername(mCurrentUsername, newUsername);
            if (updateSuccess) {
                Log.d("CambiarNombre", "Nombre de usuario actualizado en la base de datos");
            } else {
                Log.d("CambiarNombre", "Error al actualizar el nombre de usuario en la base de datos");
            }

            // Finalizar esta actividad
            finish();
        } else {
            // Mostrar un mensaje si el campo está vacío
            Toast.makeText(this, "Ingrese otro nombre de usuario", Toast.LENGTH_SHORT).show();
        }
    }
}
