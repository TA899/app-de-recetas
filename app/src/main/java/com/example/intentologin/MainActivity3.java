package com.example.intentologin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private String username;
    private DatabaseHelper mDbHelper;
    private static final int REQUEST_CODE_CHANGE_USERNAME = 1001;
    private RecetasPropias recetasPropiasFragment;
    private OtrasRecetas OtrasRecetasFragment;
    private TodasRecetas TodasRecetasFragment;

    Button buttonTodasRecetas;
    Button buttonRecetasPropias;
    Button buttonOtrasRecetas;
    private static final int REQUEST_CODE_CREATE_RECIPE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Configurar los listeners para los botones
      buttonTodasRecetas = findViewById(R.id.buttonFragmentPrincipal);
      buttonRecetasPropias = findViewById(R.id.buttonRecetasPropias);
      buttonOtrasRecetas = findViewById(R.id.buttonOtrasRecetas);

        buttonTodasRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cargar el fragmento TodasRecetas en el contenedor fragment_container
                cargarTodasRecetasFragment();
                setSelectedButton(buttonTodasRecetas);
            }
        });

        buttonRecetasPropias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cargar el fragmento RecetasPropias en el contenedor fragment_container
                cargarRecetasPropiasFragment();
                setSelectedButton(buttonRecetasPropias);
            }
        });

        buttonOtrasRecetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cargar el fragmento OtrasRecetas en el contenedor fragment_container
                cargarOtrasRecetasFragment();
                setSelectedButton(buttonOtrasRecetas);
            }
        });

        // Inicializar la base de datos
        mDbHelper = new DatabaseHelper(this);

        // Obtener el nombre de usuario actualizado desde SharedPreferences
        updateUsername();

        // Cargar el fragmento TodasRecetas en el contenedor fragment_container por defecto
        cargarTodasRecetasFragment();
        setSelectedButton(buttonTodasRecetas);
    }


    private void cargarOtrasRecetasFragment() {
        // Crear una instancia del fragmento RecetasPropias
        OtrasRecetasFragment= new OtrasRecetas();

        // Pasar el nombre de usuario como argumento al fragmento
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        OtrasRecetasFragment.setArguments(args);

        // Obtener el FragmentManager y comenzar la transacción
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, OtrasRecetasFragment) // Reemplazar el contenido actual con el fragmento
                .commit();
    }

    private void cargarRecetasPropiasFragment() {
        // Crear una instancia del fragmento RecetasPropias
        recetasPropiasFragment = new RecetasPropias();

        // Pasar el nombre de usuario como argumento al fragmento
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        recetasPropiasFragment.setArguments(args);

        // Obtener el FragmentManager y comenzar la transacción
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, recetasPropiasFragment) // Reemplazar el contenido actual con el fragmento
                .commit();
    }



    private void cargarTodasRecetasFragment() {
        // Crear una instancia del fragmento RecetasPropias
        TodasRecetasFragment = new TodasRecetas();

        // Pasar el nombre de usuario como argumento al fragmento
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        TodasRecetasFragment.setArguments(args);

        // Obtener el FragmentManager y comenzar la transacción
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, TodasRecetasFragment) // Reemplazar el contenido actual con el fragmento
                .commit();
    }


    private void setSelectedButton(Button selectedButton) {
        if (selectedButton == null) {
            return; // Si selectedButton es null, salimos del método
        }

        Button[] buttons = {
                findViewById(R.id.buttonFragmentPrincipal),
                findViewById(R.id.buttonRecetasPropias),
                findViewById(R.id.buttonOtrasRecetas)
        };

        for (Button button : buttons) {
            if (button.getId() == selectedButton.getId()) {
                // Cambiar el color de fondo del botón seleccionado
                button.setBackgroundColor(Color.BLACK); // Puedes cambiar Color.TRANSPARENT por cualquier otro color que desees
            } else {
                // Restaurar el color de fondo predeterminado para los otros botones
                button.setBackgroundColor(Color.parseColor("#ff6750a4"));
            }
        }
    }





    private void updateUsername() {
        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        username = preferences.getString("username", "");

        TextView textViewUsername = findViewById(R.id.textView);
        String welcomeMessage = "Hola, " + username;
        textViewUsername.setText(welcomeMessage);

        Log.d("MainActivity3", "Mensaje de bienvenida actualizado: " + welcomeMessage);

        // Cargar la imagen de perfil si está disponible
        String profileImagePath = mDbHelper.getProfileImagePath(username);
        ImageView imageViewProfile = findViewById(R.id.imageView3);

        if (profileImagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(profileImagePath);
            if (bitmap != null) {
                imageViewProfile.setImageBitmap(bitmap);
            } else {
                imageViewProfile.setImageResource(R.drawable.icon_circle);
            }
        } else {
            imageViewProfile.setImageResource(R.drawable.icon_circle);
        }
    }

    public void salir(View view) {

        finish();

    }

    public void irperfil(View view) {
        Intent intent = new Intent(this, MiPerfil.class);
        intent.putExtra("USERNAME", username);
        startActivityForResult(intent, REQUEST_CODE_CHANGE_USERNAME);
    }

    public void crear_receta(View view) {

        buttonRecetasPropias.setBackgroundColor(Color.BLACK);
        buttonOtrasRecetas.setBackgroundColor(Color.parseColor("#ff6750a4"));
        buttonTodasRecetas.setBackgroundColor(Color.parseColor("#ff6750a4"));

        // Reemplazar el Fragmento actual por el Fragmento de tus recetas
        if (recetasPropiasFragment == null) {
            recetasPropiasFragment = new RecetasPropias();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, recetasPropiasFragment)
                .commit();

        // Iniciar la actividad para crear una nueva receta
        Intent intent = new Intent(this, MainActivity4.class);
        intent.putExtra("USERNAME", username);
        startActivityForResult(intent, REQUEST_CODE_CREATE_RECIPE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("ActivityResult mainactivity3", "Result Code: " + resultCode + ", Request Code: " + requestCode+ "Result ok: " +RESULT_OK );
        if (requestCode == REQUEST_CODE_CHANGE_USERNAME && resultCode == RESULT_OK ) {
            // Obtener el nuevo nombre de usuario del Intent
            String newUsername = data.getStringExtra("NEW_USERNAME");
            Log.d("MainActivity3", "Nuevo nombre de usuario recibido: " + newUsername);

            // Actualizar el nombre de usuario en SharedPreferences
            if (!TextUtils.isEmpty(newUsername)) {
                SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("username", newUsername);
                editor.apply();
                Log.d("MainActivity3", "Nuevo nombre de usuario guardado en SharedPreferences: " + newUsername);

                // Actualizar el nombre de usuario en la base de datos
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                boolean updateSuccess = dbHelper.updateUsername(username, newUsername);
                if (updateSuccess) {
                    Log.d("MainActivity3", "Nombre de usuario actualizado en la base de datos");
                } else {
                    Log.d("MainActivity3", "Error al actualizar el nombre de usuario en la base de datos");
                }
            }
            // Actualizar el nombre de usuario en el TextView
            updateUsername();

            // Actualizar el nombre de usuario en el fragmento RecetasPropias si está adjunto
            if (recetasPropiasFragment != null && recetasPropiasFragment.isAdded()) {
                recetasPropiasFragment.updateUsername(newUsername);
            }

            // Actualizar el nombre de usuario en el fragmento OtrasRecetas si está adjunto
            if (OtrasRecetasFragment != null && OtrasRecetasFragment.isAdded()) {
                OtrasRecetasFragment.updateUsername(newUsername);
            }

            // Actualizar el nombre de usuario en el fragmento TodasRecetas si está adjunto
            if (TodasRecetasFragment != null && TodasRecetasFragment.isAdded()) {
                TodasRecetasFragment.updateUsername(newUsername);
            }
        }

        if (requestCode == REQUEST_CODE_CREATE_RECIPE && resultCode == RESULT_OK ) {
            // Actualizar las recetas en el fragmento TodasRecetas si está adjunto
            if (TodasRecetasFragment != null && TodasRecetasFragment.isAdded()) {
                TodasRecetasFragment.updaterecipe();
            }
            if (recetasPropiasFragment != null && recetasPropiasFragment.isAdded()) {
                recetasPropiasFragment.updaterecipe();
            }
        }
    }
}

