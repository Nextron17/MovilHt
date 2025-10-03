package com.example.hortitechv1.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hortitechv1.R;
import com.example.hortitechv1.models.ProgramacionRiego;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiProRiego;
import com.google.android.material.textfield.TextInputEditText;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormProRiegoActivity extends AppCompatActivity {

    private TextInputEditText etDescripcion, etFechaInicio, etFechaFin;
    private AutoCompleteTextView autoCompleteTipoRiego;
    private Button btnAccion, btnCancelar;
    private TextView tvTitulo;
    private ApiProRiego api;
    private int idZona;
    private int programacionId = -1;

    private final String[] tiposRiego = {"Aspersión", "Goteo", "Manual"};

    // Formatos iguales a iluminación
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pro_riego);

        // Vinculación de vistas
        etDescripcion = findViewById(R.id.etPR);
        etFechaInicio = findViewById(R.id.etFechaInicioR);
        etFechaFin = findViewById(R.id.etFechaFinR);
        autoCompleteTipoRiego = findViewById(R.id.spinnerTipoRiego);
        btnAccion = findViewById(R.id.btnCrearR);
        btnCancelar = findViewById(R.id.btnCancelarFormR);
        tvTitulo = findViewById(R.id.tvTituloFormR);

        api = ApiClient.getClient().create(ApiProRiego.class);

        // Adaptador para los tipos de riego
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tiposRiego);
        autoCompleteTipoRiego.setAdapter(adapter);

        // Recibir zona
        idZona = getIntent().getIntExtra("zona_id", -1);
        if (idZona == -1) {
            Toast.makeText(this, "No se recibió el ID de la zona", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Si se recibe un programacion_id significa que estamos en modo edición
        programacionId = getIntent().getIntExtra("programacion_id", -1);
        if (programacionId != -1) {
            String descripcion = getIntent().getStringExtra("descripcion");
            String fechaInicio = getIntent().getStringExtra("fecha_inicio");
            String fechaFin = getIntent().getStringExtra("fecha_fin");
            String tipoRiego = getIntent().getStringExtra("tipo_riego");

            etDescripcion.setText(descripcion);

            if (fechaInicio != null) {
                OffsetDateTime inicio = OffsetDateTime.parse(fechaInicio, ISO_FORMATTER);
                etFechaInicio.setText(inicio.format(DISPLAY_FORMATTER));
                etFechaInicio.setTag(inicio.format(ISO_FORMATTER));
            }
            if (fechaFin != null) {
                OffsetDateTime fin = OffsetDateTime.parse(fechaFin, ISO_FORMATTER);
                etFechaFin.setText(fin.format(DISPLAY_FORMATTER));
                etFechaFin.setTag(fin.format(ISO_FORMATTER));
            }

            if (tipoRiego != null) {
                autoCompleteTipoRiego.setText(tipoRiego, false);
            }

            tvTitulo.setText("Editar Programación");
            btnAccion.setText("Actualizar");
        } else {
            tvTitulo.setText("Crear Programación");
            btnAccion.setText("Crear");
        }

        // Listeners
        etFechaInicio.setOnClickListener(v -> mostrarDateTimePicker(etFechaInicio));
        etFechaFin.setOnClickListener(v -> mostrarDateTimePicker(etFechaFin));

        btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(FormProRiegoActivity.this, ProgramacionRiegoActivity.class);
            intent.putExtra("zona_id", idZona);
            startActivity(intent);
            finish();
        });

        btnAccion.setOnClickListener(v -> {
            if (programacionId == -1) {
                crearProgramacion();
            } else {
                actualizarProgramacion();
            }
        });
    }

    private void mostrarDateTimePicker(final TextInputEditText editText) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePicker = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                OffsetDateTime odt = calendar.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toOffsetDateTime();

                // Mostrar bonito
                editText.setText(odt.format(DISPLAY_FORMATTER));
                // Guardar ISO real en tag
                editText.setTag(odt.format(ISO_FORMATTER));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePicker.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void crearProgramacion() {
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaInicioIso = etFechaInicio.getTag() != null ? etFechaInicio.getTag().toString() : "";
        String fechaFinIso = etFechaFin.getTag() != null ? etFechaFin.getTag().toString() : "";
        String tipoSeleccionado = autoCompleteTipoRiego.getText().toString();

        if (descripcion.isEmpty() || fechaInicioIso.isEmpty() || fechaFinIso.isEmpty() || tipoSeleccionado.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        OffsetDateTime inicio = OffsetDateTime.parse(fechaInicioIso, ISO_FORMATTER);
        OffsetDateTime fin = OffsetDateTime.parse(fechaFinIso, ISO_FORMATTER);

        if (!fin.isAfter(inicio)) {
            Toast.makeText(this, "La fecha de fin debe ser posterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
            return;
        }

        ProgramacionRiego programacion = new ProgramacionRiego();
        programacion.setDescripcion(descripcion);
        programacion.setFecha_inicio(inicio);
        programacion.setFecha_finalizacion(fin);
        programacion.setTipo_riego(tipoSeleccionado);
        programacion.setId_zona(idZona);
        programacion.setEstado(true);

        api.crearProgramacion(programacion).enqueue(new Callback<ProgramacionRiego>() {
            @Override
            public void onResponse(Call<ProgramacionRiego> call, Response<ProgramacionRiego> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FormProRiegoActivity.this, "Programación creada correctamente", Toast.LENGTH_SHORT).show();
                    volverALista();
                } else {
                    Toast.makeText(FormProRiegoActivity.this, "Error al crear: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProgramacionRiego> call, Throwable t) {
                Toast.makeText(FormProRiegoActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarProgramacion() {
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaInicioIso = etFechaInicio.getTag() != null ? etFechaInicio.getTag().toString() : "";
        String fechaFinIso = etFechaFin.getTag() != null ? etFechaFin.getTag().toString() : "";
        String tipoSeleccionado = autoCompleteTipoRiego.getText().toString();

        if (descripcion.isEmpty() || fechaInicioIso.isEmpty() || fechaFinIso.isEmpty() || tipoSeleccionado.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        OffsetDateTime inicio = OffsetDateTime.parse(fechaInicioIso, ISO_FORMATTER);
        OffsetDateTime fin = OffsetDateTime.parse(fechaFinIso, ISO_FORMATTER);

        if (!fin.isAfter(inicio)) {
            Toast.makeText(this, "La fecha de fin debe ser posterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
            return;
        }

        ProgramacionRiego programacion = new ProgramacionRiego();
        programacion.setDescripcion(descripcion);
        programacion.setFecha_inicio(inicio);
        programacion.setFecha_finalizacion(fin);
        programacion.setTipo_riego(tipoSeleccionado);
        programacion.setId_zona(idZona);
        programacion.setEstado(true);

        api.actualizarProgramacion(programacionId, programacion).enqueue(new Callback<ProgramacionRiego>() {
            @Override
            public void onResponse(Call<ProgramacionRiego> call, Response<ProgramacionRiego> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FormProRiegoActivity.this, "Programación actualizada correctamente", Toast.LENGTH_SHORT).show();
                    volverALista();
                } else {
                    Toast.makeText(FormProRiegoActivity.this, "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProgramacionRiego> call, Throwable t) {
                Toast.makeText(FormProRiegoActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void volverALista() {
        Intent intent = new Intent(FormProRiegoActivity.this, ProgramacionRiegoActivity.class);
        intent.putExtra("zona_id", idZona);
        startActivity(intent);
        finish();
    }
}
