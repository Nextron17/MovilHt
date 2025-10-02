package com.example.hortitechv1.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime; // [MANTENIDO para validación local]
// Se quitan las importaciones de OffsetDateTime y ZoneOffset
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

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

    private final String[] tiposRiego = {"aspersión", "goteo", "manual"};

    // Formato sin segundos (yyyy-MM-dd HH:mm)
    private final DateTimeFormatter apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    // SimpleDateFormat sin segundos para visualización
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_pro_riego);

        etDescripcion = findViewById(R.id.etPR);
        etFechaInicio = findViewById(R.id.etFechaInicioR);
        etFechaFin = findViewById(R.id.etFechaFinR);
        autoCompleteTipoRiego = findViewById(R.id.spinnerTipoRiego);
        btnAccion = findViewById(R.id.btnCrearR);
        btnCancelar = findViewById(R.id.btnCancelarFormR);
        tvTitulo = findViewById(R.id.tvTituloFormR);

        api = ApiClient.getClient().create(ApiProRiego.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tiposRiego);
        autoCompleteTipoRiego.setAdapter(adapter);

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
            String tipoRiego = getIntent().getStringExtra("tipo_riego");

            etDescripcion.setText(descripcion);
            etFechaInicio.setText(fechaInicio);
            etFechaFin.setText(fechaFin);

            if (tipoRiego != null) {
                autoCompleteTipoRiego.setText(tipoRiego, false);
            }

            tvTitulo.setText("Editar Programación");
            btnAccion.setText("Actualizar");
        } else {
            tvTitulo.setText("Crear Programación");
            btnAccion.setText("Crear");
        }

        etFechaInicio.setOnClickListener(v -> mostrarDateTimePicker(etFechaInicio));
        etFechaFin.setOnClickListener(v -> mostrarDateTimePicker(etFechaFin));

        btnCancelar.setOnClickListener(v -> finish());

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
            TimePickerDialog timePicker = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                // Se quitan los segundos
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                editText.setText(sdf.format(calendar.getTime()));

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePicker.show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Mantiene la validación para no seleccionar fechas/horas en el pasado
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void crearProgramacion() {
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaInicioStr = etFechaInicio.getText().toString().trim();
        String fechaFinStr = etFechaFin.getText().toString().trim();
        String tipoSeleccionado = autoCompleteTipoRiego.getText().toString();

        if (descripcion.isEmpty() || fechaInicioStr.isEmpty() || fechaFinStr.isEmpty() || tipoSeleccionado.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Se usa LocalDateTime para la validación local (inicio < fin)
            LocalDateTime localFechaInicio = LocalDateTime.parse(fechaInicioStr, apiFormatter);
            LocalDateTime localFechaFin = LocalDateTime.parse(fechaFinStr, apiFormatter);

            if (localFechaFin.isBefore(localFechaInicio) || localFechaFin.isEqual(localFechaInicio)) {
                Toast.makeText(this, "La fecha de fin debe ser posterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
                return;
            }

            // AJUSTE CLAVE: Asegurar la capitalización correcta para el backend
            String tipoRiegoAjustado = tipoSeleccionado;
            if ("manual".equalsIgnoreCase(tipoSeleccionado.trim())) {
                tipoRiegoAjustado = "Manual";
            }

            ProgramacionRiego programacion = new ProgramacionRiego();
            programacion.setDescripcion(descripcion);
            programacion.setFecha_inicio(fechaInicioStr); // [MODIFICADO] Envía como String
            programacion.setFecha_finalizacion(fechaFinStr); // [MODIFICADO] Envía como String
            programacion.setTipo_riego(tipoRiegoAjustado);
            programacion.setId_zona(idZona);
            programacion.setEstado(true);

            api.crearProgramacion(programacion).enqueue(new Callback<ProgramacionRiego>() {
                @Override public void onResponse(Call<ProgramacionRiego> call, Response<ProgramacionRiego> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(FormProRiegoActivity.this, "Programación de riego creada correctamente", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        try {
                            String err = response.errorBody() != null ? response.errorBody().string() : "sin errorBody";
                            Log.e("API_CREATE", "HTTP " + response.code() + " -> " + err);
                            Toast.makeText(FormProRiegoActivity.this, "Error al crear: " + response.code(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("API_CREATE", "No se pudo leer errorBody", e);
                            Toast.makeText(FormProRiegoActivity.this, "Error al crear: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                @Override public void onFailure(Call<ProgramacionRiego> call, Throwable t) {
                    Log.e("API_CREATE", "Fallo", t);
                    Toast.makeText(FormProRiegoActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Formato de fecha incorrecto o tipo de riego inválido: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarProgramacion() {
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaInicioStr = etFechaInicio.getText().toString().trim();
        String fechaFinStr = etFechaFin.getText().toString().trim();
        String tipoSeleccionado = autoCompleteTipoRiego.getText().toString();

        if (descripcion.isEmpty() || fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocalDateTime localFechaInicio = LocalDateTime.parse(fechaInicioStr, apiFormatter);
            LocalDateTime localFechaFin = LocalDateTime.parse(fechaFinStr, apiFormatter);

            if (localFechaFin.isBefore(localFechaInicio) || localFechaFin.isEqual(localFechaInicio)) {
                Toast.makeText(this, "La fecha de fin debe ser posterior a la fecha de inicio.", Toast.LENGTH_LONG).show();
                return;
            }

            // AJUSTE CLAVE: Asegurar la capitalización correcta para el backend
            String tipoRiegoAjustado = tipoSeleccionado;
            if ("manual".equalsIgnoreCase(tipoSeleccionado.trim())) {
                tipoRiegoAjustado = "Manual";
            }

            ProgramacionRiego programacion = new ProgramacionRiego();
            programacion.setDescripcion(descripcion);
            programacion.setFecha_inicio(fechaInicioStr); // [MODIFICADO] Envía como String
            programacion.setFecha_finalizacion(fechaFinStr); // [MODIFICADO] Envía como String
            programacion.setTipo_riego(tipoRiegoAjustado);
            programacion.setId_zona(idZona);
            programacion.setEstado(true);

            api.actualizarProgramacion(programacionId, programacion).enqueue(new Callback<ProgramacionRiego>() {
                @Override
                public void onResponse(Call<ProgramacionRiego> call, Response<ProgramacionRiego> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(FormProRiegoActivity.this, "Programación de riego actualizada correctamente", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(FormProRiegoActivity.this, "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ProgramacionRiego> call, Throwable t) {
                    Toast.makeText(FormProRiegoActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Formato de fecha incorrecto o tipo de riego inválido: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}