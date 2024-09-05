package com.example.intentologin;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MiPerfil extends AppCompatActivity {
    private static final int REQUEST_CODE_CHANGE_USERNAME = 1001;// Código de solicitud para CambiarNombreActivity

    private static final int REQUEST_CODE_PICK_IMAGE= 1002;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        // Obtener el nombre de usuario del Intent
        username = getIntent().getStringExtra("USERNAME");
        Log.d("MiPerfil", "Nombre de usuario recibido: " + username);
    }

    public void cambiarnombre(View view) {
        Intent intent = new Intent(this, CambiarNombre.class);
        intent.putExtra("USERNAME", username);
        startActivityForResult(intent, REQUEST_CODE_CHANGE_USERNAME); // Iniciar CambiarNombreActivity para cambiar el nombre de usuario
    }


    public void  cambiarfoto (View view) {
        Intent intent = new Intent(this, Nuevafoto.class);
        startActivityForResult(intent, REQUEST_CODE_CHANGE_USERNAME);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("ActivityResult", "Result Code: " + resultCode + ", Request Code: " + requestCode+ "Result ok: " +RESULT_OK );

        if (requestCode == REQUEST_CODE_CHANGE_USERNAME && resultCode == RESULT_OK) {
            // Obtener el nuevo nombre de usuario del Intent
            String newUsername = data.getStringExtra("NEW_USERNAME");
            Log.d("MiPerfil", "Nuevo nombre de usuario recibido: " + newUsername);

            // Actualizar el nombre de usuario en MainActivity3
            Intent resultIntent = new Intent();
            resultIntent.putExtra("NEW_USERNAME", newUsername);
            setResult(RESULT_OK, resultIntent);

            // Finalizar esta actividad
            finish();
        }


    }
}
