package com.example.intentologin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Calendario extends AppCompatActivity {

    private static final int REQUEST_CODE_CALENDAR_APP = 101;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String eventoTitulo;
    private String eventoDescripcion;
    private EditText selectedDateText;
    private boolean isDateSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        selectedDateText = findViewById(R.id.TextView3);
    }

    public void volver_recetas(View view) {
        finish();
    }

    public void seleccionarFecha(View view) {
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, year, monthOfYear, dayOfMonth) -> {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    // Después de seleccionar la fecha, mostrar el diálogo para seleccionar la hora
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (view1, hourOfDay, minute) -> {
                                mHour = hourOfDay;
                                mMinute = minute;

                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(mYear, mMonth, mDay, mHour, mMinute);
                                String dateString = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year + " " + hourOfDay + ":" + String.format("%02d", minute);
                                selectedDateText.setText(dateString);
                                isDateSelected = true; // Marcar que se ha seleccionado una fecha
                            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void agendarReceta(View view) {
        if (!isDateSelected) {
            Toast.makeText(this, "Por favor, seleccione una fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        String tituloReceta = getIntent().getStringExtra("RECIPE_TITLE");
        String pasosReceta = getIntent().getStringExtra("RECIPE_STEPS");

        if (!TextUtils.isEmpty(tituloReceta)) {
            eventoTitulo = tituloReceta;
            eventoDescripcion = pasosReceta;
            crearEventoCalendario();
        } else {
            Toast.makeText(this, "No se proporcionó un título para la receta", Toast.LENGTH_SHORT).show();
        }
    }

    public void agendarCompraIngredientes(View view) {
        if (!isDateSelected) {
            Toast.makeText(this, "Por favor, seleccione una fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        String ingredientes = getIntent().getStringExtra("RECIPE_INGREDIENTS");

        if (!TextUtils.isEmpty(ingredientes)) {
            eventoTitulo = "Comprar: " + ingredientes;
            eventoDescripcion = "";
            crearEventoCalendario();
        } else {
            Toast.makeText(this, "No hay ingredientes para comprar", Toast.LENGTH_SHORT).show();
        }
    }

    private void crearEventoCalendario() {
        // Crear los objetos Calendar para el tiempo de inicio y finalización del evento
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(mYear, mMonth, mDay, mHour, mMinute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(mYear, mMonth, mDay, mHour + 1, mMinute);

        // Crear el intent para abrir la aplicación de calendario
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.TITLE, eventoTitulo);

        // Agregar la descripción si está disponible
        if (!TextUtils.isEmpty(eventoDescripcion)) {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, eventoDescripcion);
        }

        // Iniciar la actividad de la aplicación de calendario con startActivityForResult
        startActivityForResult(intent, REQUEST_CODE_CALENDAR_APP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CALENDAR_APP && resultCode == RESULT_OK) {
            // Si el resultado es OK, significa que el usuario guardó el evento en el calendario
            // Borrar la fecha seleccionada y restablecer el estado
            selectedDateText.setText("");
            isDateSelected = false;
        }
    }}