package com.example.hortitechv1.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.models.Bitacora;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiBitacora;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearEditarBitacoraActivity extends AppCompatActivity {

    private boolean isEditMode = false;
    private Bitacora bitacoraActual;
    private ApiBitacora apiService;
    private SessionManager sessionManager;
    private TextInputEditText etTitulo, etContenido;
    private AutoCompleteTextView spinnerTipoEvento, spinnerImportancia, spinnerInvernadero, spinnerZona, spinnerAutor;
    private Button btnGuardar;

    private Bitacora.Invernadero selectedInvernadero;
    private Bitacora.Zona selectedZona;
    private Bitacora.Autor selectedAutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_editar_bitacora);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        apiService = ApiClient.getClient().create(ApiBitacora.class);
        sessionManager = new SessionManager(this);

        etTitulo = findViewById(R.id.etTitulo);
        etContenido = findViewById(R.id.etContenido);
        spinnerTipoEvento = findViewById(R.id.spinnerTipoEvento);
        spinnerImportancia = findViewById(R.id.spinnerImportancia);
        spinnerInvernadero = findViewById(R.id.spinnerInvernadero);
        spinnerZona = findViewById(R.id.spinnerZona);
        spinnerAutor = findViewById(R.id.spinnerAutor);
        btnGuardar = findViewById(R.id.btnGuardar);
        TextView tvFormTitle = findViewById(R.id.tvFormTitle);

        if (getIntent().hasExtra("BITACORA_EDITAR")) {
            isEditMode = true;
            bitacoraActual = (Bitacora) getIntent().getSerializableExtra("BITACORA_EDITAR");
            tvFormTitle.setText("Editar Bitácora");
            etTitulo.setText(bitacoraActual.getTitulo());
            etContenido.setText(bitacoraActual.getContenido());
        } else {
            tvFormTitle.setText("Nueva Bitácora");
        }

        cargarSpinnersEstaticos();
        cargarSpinnersDinamicos();
        btnGuardar.setOnClickListener(v -> guardarBitacora());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarSpinnersEstaticos() {
        ArrayAdapter<String> tipoEventoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Arrays.asList("riego", "iluminacion", "cultivo", "alerta", "mantenimiento", "hardware", "general"));
        spinnerTipoEvento.setAdapter(tipoEventoAdapter);

        ArrayAdapter<String> importanciaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Arrays.asList("baja", "media", "alta"));
        spinnerImportancia.setAdapter(importanciaAdapter);

        if (isEditMode) {
            spinnerTipoEvento.setText(bitacoraActual.getTipo_evento(), false);
            spinnerImportancia.setText(bitacoraActual.getImportancia(), false);
        }
    }

    private void cargarSpinnersDinamicos() {
        apiService.getAutores().enqueue(new Callback<List<Bitacora.Autor>>() {
            @Override
            public void onResponse(Call<List<Bitacora.Autor>> call, Response<List<Bitacora.Autor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Bitacora.Autor> autores = response.body();
                    ArrayAdapter<Bitacora.Autor> adapter = new ArrayAdapter<>(CrearEditarBitacoraActivity.this, android.R.layout.simple_dropdown_item_1line, autores);
                    spinnerAutor.setAdapter(adapter);
                    if (isEditMode) {
                        autores.stream().filter(a -> a.getId_persona() == bitacoraActual.getAutor_id()).findFirst().ifPresent(a -> spinnerAutor.setText(a.toString(), false));
                    }
                }
            }
            @Override public void onFailure(Call<List<Bitacora.Autor>> call, Throwable t) {}
        });

        apiService.getInvernaderos().enqueue(new Callback<List<Bitacora.Invernadero>>() {
            @Override
            public void onResponse(Call<List<Bitacora.Invernadero>> call, Response<List<Bitacora.Invernadero>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int responsableId = sessionManager.getUserId();
                    List<Bitacora.Invernadero> misInvernaderos = response.body().stream()
                            .filter(inv -> inv.getResponsable_id() == responsableId)
                            .collect(Collectors.toList());

                    ArrayAdapter<Bitacora.Invernadero> adapter = new ArrayAdapter<>(CrearEditarBitacoraActivity.this, android.R.layout.simple_dropdown_item_1line, misInvernaderos);
                    spinnerInvernadero.setAdapter(adapter);
                    if (isEditMode) {
                        misInvernaderos.stream().filter(i -> i.getId_invernadero() == bitacoraActual.getId_invernadero()).findFirst().ifPresent(i -> {
                            spinnerInvernadero.setText(i.toString(), false);
                            cargarZonas(i.getId_invernadero());
                        });
                    }
                }
            }
            @Override public void onFailure(Call<List<Bitacora.Invernadero>> call, Throwable t) {}
        });

        spinnerInvernadero.setOnItemClickListener((parent, view, position, id) -> {
            selectedInvernadero = (Bitacora.Invernadero) parent.getItemAtPosition(position);
            spinnerZona.setText("", false);
            selectedZona = null;
            if (selectedInvernadero != null) {
                cargarZonas(selectedInvernadero.getId_invernadero());
            }
        });

        spinnerZona.setOnItemClickListener((parent, view, position, id) -> selectedZona = (Bitacora.Zona) parent.getItemAtPosition(position));
        spinnerAutor.setOnItemClickListener((parent, view, position, id) -> selectedAutor = (Bitacora.Autor) parent.getItemAtPosition(position));
    }

    private void cargarZonas(int idInvernadero) {
        apiService.getZonasPorInvernadero(idInvernadero).enqueue(new Callback<List<Bitacora.Zona>>() {
            @Override
            public void onResponse(Call<List<Bitacora.Zona>> call, Response<List<Bitacora.Zona>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Bitacora.Zona> zonas = response.body();
                    ArrayAdapter<Bitacora.Zona> adapter = new ArrayAdapter<>(CrearEditarBitacoraActivity.this, android.R.layout.simple_dropdown_item_1line, zonas);
                    spinnerZona.setAdapter(adapter);
                    if (isEditMode && bitacoraActual.getId_zona() != null) {
                        zonas.stream().filter(z -> z.getId_zona() == bitacoraActual.getId_zona()).findFirst().ifPresent(z -> spinnerZona.setText(z.toString(), false));
                    }
                }
            }
            @Override public void onFailure(Call<List<Bitacora.Zona>> call, Throwable t) {}
        });
    }

    private void guardarBitacora() {
        if (etTitulo.getText().toString().isEmpty() || etContenido.getText().toString().isEmpty()) {
            Toast.makeText(this, "Título y contenido son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitacora bitacora = isEditMode ? bitacoraActual : new Bitacora();
        bitacora.setTitulo(etTitulo.getText().toString());
        bitacora.setContenido(etContenido.getText().toString());
        bitacora.setTipo_evento(spinnerTipoEvento.getText().toString());
        bitacora.setImportancia(spinnerImportancia.getText().toString());

        if (selectedInvernadero != null) bitacora.setId_invernadero(selectedInvernadero.getId_invernadero());
        if (selectedZona != null) bitacora.setId_zona(selectedZona.getId_zona());
        if (selectedAutor != null) bitacora.setAutor_id(selectedAutor.getId_persona());

        Call<ResponseBody> call = isEditMode ?
                apiService.actualizarBitacora(bitacora.getId_publicacion(), bitacora) :
                apiService.crearBitacora(bitacora);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CrearEditarBitacoraActivity.this, "Bitácora guardada", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CrearEditarBitacoraActivity.this, "Error al guardar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CrearEditarBitacoraActivity.this, "Fallo de conexión al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}