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
import com.example.hortitechv1.controllers.ProgramacionRiegoAdapter;
import com.example.hortitechv1.models.ProgramacionRiego;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiProRiego;
// Se quita la importación de java.time.format.DateTimeFormatter
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgramacionRiegoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnNueva;
    private ProgramacionRiegoAdapter adapter;
    private List<ProgramacionRiego> listaProgramaciones = new ArrayList<>();
    private ApiProRiego api;
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
        setContentView(R.layout.activity_programacion_riego);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rvProgramacionRiego);
        btnNueva = findViewById(R.id.btnNuevaProgramacionRiego);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        api = ApiClient.getClient().create(ApiProRiego.class);

        adapter = new ProgramacionRiegoAdapter(this, listaProgramaciones, new ProgramacionRiegoAdapter.OnItemClickListener() {
            public void onActualizarClick(ProgramacionRiego programacion) {
                actualizarProgramacion(programacion);
            }

            @Override
            public void onDetenerClick(ProgramacionRiego programacion) {
                mostrarDialogoDetener(programacion);
            }

            @Override
            public void onEliminarClick(ProgramacionRiego programacion) {
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
            Intent intent = new Intent(ProgramacionRiegoActivity.this, FormProRiegoActivity.class);
            intent.putExtra("zona_id", idZona);
            formLauncher.launch(intent);
        });
    }

    private void getProgramacionesFuturas() {
        api.getProgramacionesFuturas(idZona).enqueue(new Callback<List<ProgramacionRiego>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProgramacionRiego>> call, @NonNull Response<List<ProgramacionRiego>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProgramaciones.clear();
                    listaProgramaciones.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ProgramacionRiegoActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProgramacionRiego>> call, @NonNull Throwable t) {
                Toast.makeText(ProgramacionRiegoActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDialogoEliminar(ProgramacionRiego p) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("Esta acción es permanente. ¿Deseas eliminar esta programación?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarProgramacion(p))
                .setNegativeButton("Cancelar", null)
                .setIcon(R.drawable.ic_delete)
                .show();
    }

    private void mostrarDialogoDetener(ProgramacionRiego p) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Detención")
                .setMessage("¿Estás seguro de que deseas detener esta programación? Cambiará su estado a inactivo.")
                .setPositiveButton("Detener", (dialog, which) -> detenerProgramacion(p))
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void detenerProgramacion(ProgramacionRiego p) {
        ProgramacionRiego programacionConEstado = new ProgramacionRiego();
        programacionConEstado.setEstado(false);

        api.cambiarEstadoProgramacion(p.getId_pg_riego(), programacionConEstado).enqueue(new Callback<ProgramacionRiego>() {
            @Override
            public void onResponse(@NonNull Call<ProgramacionRiego> call, @NonNull Response<ProgramacionRiego> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProgramacionRiegoActivity.this, "Programación detenida", Toast.LENGTH_SHORT).show();
                    getProgramacionesFuturas();
                } else {
                    Toast.makeText(ProgramacionRiegoActivity.this, "No se pudo detener", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProgramacionRiego> call, @NonNull Throwable t) {
                Toast.makeText(ProgramacionRiegoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void eliminarProgramacion(ProgramacionRiego p) {
        api.eliminarProgramacion(p.getId_pg_riego()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProgramacionRiegoActivity.this, "Programación eliminada", Toast.LENGTH_SHORT).show();
                    getProgramacionesFuturas();
                } else {
                    Toast.makeText(ProgramacionRiegoActivity.this, "No se pudo eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ProgramacionRiegoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarProgramacion(ProgramacionRiego p) {
        Intent intent = new Intent(this, FormProRiegoActivity.class);
        intent.putExtra("zona_id", idZona);
        intent.putExtra("programacion_id", p.getId_pg_riego());
        intent.putExtra("descripcion", p.getDescripcion());
        intent.putExtra("tipo_riego", p.getTipo_riego());

        // [CORREGIDO] Se pasa directamente el String
        intent.putExtra("fecha_inicio", p.getFecha_inicio());
        intent.putExtra("fecha_fin", p.getFecha_finalizacion());
        formLauncher.launch(intent);
    }
}