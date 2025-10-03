package com.example.hortitechv1.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hortitechv1.R;
import com.example.hortitechv1.models.ProgramacionIluminacion;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiProIluminacion;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormProIluminacionActivity extends AppCompatActivity {

    private EditText etDescripcion, etFechaInicio, etFechaFin;
    private Button btnAccion, btnCancelar;
    private TextView tvTitulo;
    private ApiProIluminacion api;
    private int idZona;
    private int programacionId = -1;

    // Formato ISO para enviar al backend
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    // Formato visible en pantalla
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pro_iluminacion);

        etDescripcion = findViewById(R.id.etP);
        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        btnAccion = findViewById(R.id.btnCrear);
        btnCancelar = findViewById(R.id.btnCancelarFormI);
        tvTitulo = findViewById(R.id.tvTituloForm);

        api = ApiClient.getClient().create(ApiProIluminacion.class);

        idZona = getIntent().getIntExtra("zona_id", -1);
        if (idZona == -1) {
            Toast.makeText(this, "No se recibió el ID de la zona", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        programacionId = getIntent().getIntExtra("programacion_id", -1);
        if (programacionId != -1) {
            String descripcion = getIntent().getStringExtra("descripcion");
            String fechaInicio = getIntent().getStringExtra("fecha_inicio");
            String fechaFin = getIntent().getStringExtra("fecha_fin");

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

            tvTitulo.setText("Editar Programación");
            btnAccion.setText("Actualizar");
        } else {
            tvTitulo.setText("Crear Programación");
            btnAccion.setText("Crear");
        }

        etFechaInicio.setOnClickListener(v -> mostrarDateTimePicker(etFechaInicio));
        etFechaFin.setOnClickListener(v -> mostrarDateTimePicker(etFechaFin));

        btnCancelar.setOnClickListener(v -> {
            Intent intent = new Intent(FormProIluminacionActivity.this, ProgramacionIluminacionActivity.class);
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

    private void mostrarDateTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePicker = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);

                                // Convertir Calendar a OffsetDateTime
                                OffsetDateTime odt = calendar.toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toOffsetDateTime();

                                // Mostrar bonito
                                editText.setText(odt.format(DISPLAY_FORMATTER));
                                // Guardar ISO real en tag
                                editText.setTag(odt.format(ISO_FORMATTER));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePicker.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show();
    }

    private void crearProgramacion() {
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaInicioIso = etFechaInicio.getTag() != null ? etFechaInicio.getTag().toString() : "";
        String fechaFinIso = etFechaFin.getTag() != null ? etFechaFin.getTag().toString() : "";

        if (descripcion.isEmpty() || fechaInicioIso.isEmpty() || fechaFinIso.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        OffsetDateTime inicio = OffsetDateTime.parse(fechaInicioIso, ISO_FORMATTER);
        OffsetDateTime fin = OffsetDateTime.parse(fechaFinIso, ISO_FORMATTER);

        if (!fin.isAfter(inicio)) {
            Toast.makeText(this, "La fecha de fin debe ser posterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
            return;
        }

        ProgramacionIluminacion programacion = new ProgramacionIluminacion();
        programacion.setDescripcion(descripcion);
        programacion.setFecha_inicio(inicio);
        programacion.setFecha_finalizacion(fin);
        programacion.setId_zona(idZona);
        programacion.setEstado(true);

        api.crearProgramacion(programacion).enqueue(new Callback<ProgramacionIluminacion>() {
            @Override
            public void onResponse(Call<ProgramacionIluminacion> call, Response<ProgramacionIluminacion> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FormProIluminacionActivity.this,
                            "Programación creada correctamente", Toast.LENGTH_SHORT).show();
                    volverALista();
                } else {
                    Toast.makeText(FormProIluminacionActivity.this,
                            "Error al crear: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProgramacionIluminacion> call, Throwable t) {
                Toast.makeText(FormProIluminacionActivity.this,
                        "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarProgramacion() {
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaInicioIso = etFechaInicio.getTag() != null ? etFechaInicio.getTag().toString() : "";
        String fechaFinIso = etFechaFin.getTag() != null ? etFechaFin.getTag().toString() : "";

        if (descripcion.isEmpty() || fechaInicioIso.isEmpty() || fechaFinIso.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        OffsetDateTime inicio = OffsetDateTime.parse(fechaInicioIso, ISO_FORMATTER);
        OffsetDateTime fin = OffsetDateTime.parse(fechaFinIso, ISO_FORMATTER);

        if (!fin.isAfter(inicio)) {
            Toast.makeText(this, "La fecha de fin debe ser posterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
            return;
        }

        ProgramacionIluminacion programacion = new ProgramacionIluminacion();
        programacion.setDescripcion(descripcion);
        programacion.setFecha_inicio(inicio);
        programacion.setFecha_finalizacion(fin);
        programacion.setId_zona(idZona);
        programacion.setEstado(true);

        api.actualizarProgramacion(programacionId, programacion).enqueue(new Callback<ProgramacionIluminacion>() {
            @Override
            public void onResponse(Call<ProgramacionIluminacion> call, Response<ProgramacionIluminacion> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FormProIluminacionActivity.this,
                            "Programación actualizada correctamente", Toast.LENGTH_SHORT).show();
                    volverALista();
                } else {
                    Toast.makeText(FormProIluminacionActivity.this,
                            "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProgramacionIluminacion> call, Throwable t) {
                Toast.makeText(FormProIluminacionActivity.this,
                        "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void volverALista() {
        Intent intent = new Intent(FormProIluminacionActivity.this, ProgramacionIluminacionActivity.class);
        intent.putExtra("zona_id", idZona);
        startActivity(intent);
        finish();
    }
}
