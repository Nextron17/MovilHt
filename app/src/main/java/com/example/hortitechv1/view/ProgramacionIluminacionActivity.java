package com.example.hortitechv1.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.ProgramacionIlumiAdapter;
import com.example.hortitechv1.models.ProgramacionIluminacion;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiProIluminacion;
// Se quita la importación de java.time.format.DateTimeFormatter
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgramacionIluminacionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnNueva;
    private ProgramacionIlumiAdapter adapter;
    private List<ProgramacionIluminacion> listaProgramaciones = new ArrayList<>();
    private ApiProIluminacion api;
    private int idZona;

    private final ActivityResultLauncher<Intent> formLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    getProgramacionesFuturas();
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programacion_iluminacion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rvProgramacionIlumi);
        btnNueva = findViewById(R.id.btnNuevaProgramacionIlu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        api = ApiClient.getClient().create(ApiProIluminacion.class);

        adapter = new ProgramacionIlumiAdapter(this, listaProgramaciones, new ProgramacionIlumiAdapter.OnItemClickListener() {
            public void onActualizarClick(ProgramacionIluminacion programacion) {
                actualizarProgramacion(programacion);
            }

            @Override
            public void onDetenerClick(ProgramacionIluminacion programacion) {
                mostrarDialogoDetener(programacion);
            }

            @Override
            public void onEliminarClick(ProgramacionIluminacion programacion) {
                mostrarDialogoEliminar(programacion);
            }
        });
        recyclerView.setAdapter(adapter);

        idZona = getIntent().getIntExtra("zona_id", -1);
        if (idZona != -1) {
            getProgramacionesFuturas();
        } else {
            Toast.makeText(this, "No se recibió el ID de la zona", Toast.LENGTH_SHORT).show();
        }

        btnNueva.setOnClickListener(v -> {
            Intent intent = new Intent(ProgramacionIluminacionActivity.this, FormProIluminacionActivity.class);
            intent.putExtra("zona_id", idZona);
            formLauncher.launch(intent);
        });
    }

    private void getProgramacionesFuturas() {
        api.getProgramacionesFuturas(idZona).enqueue(new Callback<List<ProgramacionIluminacion>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProgramacionIluminacion>> call, @NonNull Response<List<ProgramacionIluminacion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProgramaciones.clear();
                    listaProgramaciones.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProgramacionIluminacionActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProgramacionIluminacion>> call, @NonNull Throwable t) {
                Toast.makeText(ProgramacionIluminacionActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoEliminar(ProgramacionIluminacion p) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("Esta acción es permanente. ¿Deseas eliminar esta programación?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarProgramacion(p))
                .setNegativeButton("Cancelar", null)
                .setIcon(R.drawable.ic_delete)
                .show();
    }

    private void mostrarDialogoDetener(ProgramacionIluminacion p) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Detención")
                .setMessage("¿Estás seguro de que deseas detener esta programación? Cambiará su estado a inactivo.")
                .setPositiveButton("Detener", (dialog, which) -> detenerProgramacion(p))
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void detenerProgramacion(ProgramacionIluminacion p) {
        ProgramacionIluminacion programacionConEstado = new ProgramacionIluminacion();
        programacionConEstado.setEstado(false);

        api.cambiarEstadoProgramacion(p.getId_iluminacion(), programacionConEstado).enqueue(new Callback<ProgramacionIluminacion>() {
            @Override
            public void onResponse(@NonNull Call<ProgramacionIluminacion> call, @NonNull Response<ProgramacionIluminacion> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProgramacionIluminacionActivity.this, "Programación detenida", Toast.LENGTH_SHORT).show();
                    getProgramacionesFuturas();
                } else {
                    Toast.makeText(ProgramacionIluminacionActivity.this, "No se pudo detener", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProgramacionIluminacion> call, @NonNull Throwable t) {
                Toast.makeText(ProgramacionIluminacionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void eliminarProgramacion(ProgramacionIluminacion p) {
        api.eliminarProgramacion(p.getId_iluminacion()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProgramacionIluminacionActivity.this, "Programación eliminada", Toast.LENGTH_SHORT).show();
                    getProgramacionesFuturas();
                } else {
                    Toast.makeText(ProgramacionIluminacionActivity.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ProgramacionIluminacionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarProgramacion(ProgramacionIluminacion p) {
        Intent intent = new Intent(this, FormProIluminacionActivity.class);
        intent.putExtra("zona_id", idZona);
        intent.putExtra("programacion_id", p.getId_iluminacion());
        intent.putExtra("descripcion", p.getDescripcion());

        // [CORREGIDO] Se pasa directamente el String
        intent.putExtra("fecha_inicio", p.getFecha_inicio());
        intent.putExtra("fecha_fin", p.getFecha_finalizacion());
        formLauncher.launch(intent);
    }
}