package com.example.intentologin;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Nuevafoto extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    String username;
    CardView cardViewAbrirGaleria;
    CardView cardViewGuardar;
    ImageView imageView;
    Uri selectedImageUri;
    String newprofileImagePath;
    DatabaseHelper userDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevafoto);

        imageView = findViewById(R.id.imageView);

        cardViewAbrirGaleria = findViewById(R.id.cardViewMisRecetas);
        cardViewGuardar = findViewById(R.id.cardViewGuardar);

        cardViewAbrirGaleria.setOnClickListener(v -> abrirGaleria());
        cardViewGuardar.setOnClickListener(v -> guardarImagen());

        SharedPreferences preferences = getSharedPreferences("usuario", MODE_PRIVATE);
        username = preferences.getString("username", "");

        userDbHelper = new DatabaseHelper(this);

        Toast.makeText(this, "Seleccione su imagen de perfil", Toast.LENGTH_SHORT).show();
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void guardarImagen() {
        if (selectedImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                guardarImagenEnAlmacenamientoInterno(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarImagenEnAlmacenamientoInterno(Bitmap bitmap) {
        try {
            // Obtener el directorio de almacenamiento interno de la aplicación
            File directorio = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "imagenes");
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // Crear un archivo para la imagen en el directorio de almacenamiento interno
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";
            File archivo = new File(directorio, imageFileName);

            // Abrir un flujo de salida para escribir los datos de la imagen
            FileOutputStream outputStream = new FileOutputStream(archivo);

            // Comprimir el bitmap en formato JPEG y escribirlo al flujo de salida
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // Limpiar y cerrar el flujo de salida
            outputStream.flush();
            outputStream.close();

            // Asignar la ruta de la imagen guardada a la variable profileImagePath
            newprofileImagePath = archivo.getAbsolutePath();


            userDbHelper.updateProfileImagePathByUsername(username, newprofileImagePath);

            // Mostrar un mensaje emergente indicando que se guardó la imagen
            Toast.makeText(this, "Imagen guardada exitosamente en: " +  newprofileImagePath, Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("NEW_USERNAME",username);
            setResult(RESULT_OK, resultIntent);
            finish();

        } catch (IOException e) {
            // Manejar cualquier excepción que ocurra durante el guardado
            e.printStackTrace();
            Toast.makeText(this, "¡Error al guardar la imagen!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Muestra la imagen seleccionada en el ImageView
            imageView.setImageURI(selectedImageUri);
        }
    }
}