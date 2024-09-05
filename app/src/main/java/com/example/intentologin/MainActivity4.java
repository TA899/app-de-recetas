package com.example.intentologin;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity4 extends AppCompatActivity {

    // Declaración de variables
private String username;
    Button btcamara;
    ImageView imageView;
    EditText mTitleEditText, mIngredientesEditText, mHistoriaEditText;

    private DatabaseHelper mDbHelper;
    private String imagePath;

    private static final int REQUEST_CODE_STORAGE = 100;
    private static final int REQUEST_CODE_CREATE_RECIPE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);

        // Inicializar variables de la interfaz
        btcamara = findViewById(R.id.btcamara);
        imageView = findViewById(R.id.imageView);
        mTitleEditText = findViewById(R.id.mTitleEditText);
        mIngredientesEditText= findViewById(R.id.mIngredientesEditText);
        mHistoriaEditText= findViewById(R.id.mHistoriaEditText);


        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("USERNAME");
        }

        mDbHelper = new DatabaseHelper(this);

        // Aplicar insets a la vista principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Asignar un escuchador de clic al botón de la cámara
        btcamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de la cámara
                camaraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, iniciar cámara
                camaraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
            } else {
                // Permiso denegado, informar al usuario
                Toast.makeText(this, "Se necesita permiso para guardar fotos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ActivityResultLauncher<Intent> camaraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Bundle extras = result.getData().getExtras();
                Bitmap imgBitmap = (Bitmap) extras.get("data"); // Obtener la imagen capturada
                imageView.setImageBitmap(imgBitmap); // Mostrar la imagen en el ImageView

                // Guardar la imagen capturada en el almacenamiento
                saveImageToInternalStorage(imgBitmap);

            }
        }
    });

    private void saveImageToInternalStorage(Bitmap bitmap) {
        try {
            // Obtener el directorio de almacenamiento interno de la aplicación
            File directory = getFilesDir();

            // Crear un archivo para la imagen en el directorio de almacenamiento interno
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";
            File imageFile = new File(directory, imageFileName);

            // Abrir un flujo de salida para escribir los datos de la imagen
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            // Comprimir el bitmap en formato JPEG y escribirlo al flujo de salida
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // Limpiar y cerrar el flujo de salida
            outputStream.flush();
            outputStream.close();

            // Actualizar la variable imagePath con la ruta de la imagen guardada
            imagePath = imageFile.getAbsolutePath();

            // Mostrar un mensaje emergente indicando que se guardó la imagen
            Toast.makeText(this, "Imagen guardada exitosamente en: " + imagePath, Toast.LENGTH_SHORT).show();


        } catch (IOException e) {
            // Manejar cualquier excepción que ocurra durante el guardado
            e.printStackTrace();
            Toast.makeText(this, "¡Error al guardar la imagen!", Toast.LENGTH_SHORT).show();
        }
    }


    // Se registra la receta y se guarda el _id del usuario tabla user en el name_user_id
    private void registerRecipe() {
        String title = mTitleEditText.getText().toString().trim();
        String ingredientes = mIngredientesEditText.getText().toString().trim();
        String historia = mHistoriaEditText.getText().toString().trim();

        // Verificar si la ruta de la imagen no está vacía
        if (!TextUtils.isEmpty(imagePath)) {
            // Obtener el ID del usuario a partir de su nombre de usuario
            int userId = mDbHelper.getUserIdByUsername(username);

            // Verificar si se encontró el ID del usuario
            if (userId != -1) {
                // Crear ContentValues para la receta se preran los valores para enviarlos
                ContentValues values = new ContentValues();
                values.put(RecipeContract.RecipeEntry.COLUMN_NAME_TITLE, title);
                values.put(RecipeContract.RecipeEntry.COLUMN_NAME_INGREDIENTS, ingredientes);
                values.put(RecipeContract.RecipeEntry.COLUMN_NAME_STEPS, historia);
                // Agregar la ruta de la imagen a los ContentValues
                values.put(RecipeContract.RecipeEntry.COLUMN_NAME_IMAGE_PATH, imagePath);
                // Insertar el ID del usuario en la tabla de recetas
                values.put(RecipeContract.RecipeEntry.COLUMN_NAME_USER_ID, userId);

                // Obtener una instancia de SQLiteDatabase para escribir en la base de datos
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                // Insertar la nueva fila en la tabla de la base de datos, se insertan los valores
                long newRowId = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);

                // Comprobar si la inserción fue exitosa
                if (newRowId == -1) {
                    // Si newRowId es -1, hubo un error al insertar la receta
                    Log.e("RegisterRecipe", "Error al registrar receta");
                    Toast.makeText(this, "Error al registrar receta", Toast.LENGTH_SHORT).show();
                } else {
                    // Si newRowId es diferente de -1, la receta se registró correctamente
                    Log.d("RegisterRecipe", "Receta registrada con éxito");
                    Toast.makeText(this, "Receta registrada con éxito", Toast.LENGTH_SHORT).show();
                    // Puedes agregar cualquier acción adicional aquí después de registrar la receta
                }
            } else {
                // Manejar el caso en el que no se encontró el ID del usuario
                Log.e("RegisterRecipe", "Error: No se encontró el ID del usuario");
                Toast.makeText(this, "Error: No se encontró el ID del usuario", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Si la ruta de la imagen está vacía, mostrar un mensaje de error
            Log.e("RegisterRecipe", "Error: La ruta de la imagen está vacía");
            Toast.makeText(this, "Error: La ruta de la imagen está vacía", Toast.LENGTH_SHORT).show();
        }
    }


    public void finalizar(View view) {
        // Guardar los campos que componen la receta
        registerRecipe();
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);

        finish();}
    }


