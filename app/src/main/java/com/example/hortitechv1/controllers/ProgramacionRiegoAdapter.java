package com.example.hortitechv1.controllers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hortitechv1.R;
import com.example.hortitechv1.models.ProgramacionRiego;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProgramacionRiegoAdapter extends RecyclerView.Adapter<ProgramacionRiegoAdapter.ViewHolder> {

    private Context context;
    private List<ProgramacionRiego> listaProgramaciones;
    private OnItemClickListener listener;

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public interface OnItemClickListener {
        void onActualizarClick(ProgramacionRiego programacion);
        // REMOVIDO: void onDetenerClick(ProgramacionRiego programacion);
        void onEliminarClick(ProgramacionRiego programacion);
        // REMOVIDO: void onReanudarClick(ProgramacionRiego programacion);
    }

    public ProgramacionRiegoAdapter(Context context, List<ProgramacionRiego> listaProgramaciones, OnItemClickListener listener) {
        this.context = context;
        this.listaProgramaciones = listaProgramaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProgramacionRiegoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_programacion_riego,
                parent,
                false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramacionRiegoAdapter.ViewHolder holder, int position) {
        ProgramacionRiego p = listaProgramaciones.get(position);

        holder.tvDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
        holder.tvFechaActivacion.setText("Inicio: " + formatearFecha(p.getFecha_inicio()));
        holder.tvFechaDesactivacion.setText("Fin: " + formatearFecha(p.getFecha_finalizacion()));
        holder.tvTipoRiego.setText("Tipo: " + (p.getTipo_riego() != null ? p.getTipo_riego() : "-"));

        // Lógica simplificada: siempre editable/eliminable
        holder.btnEditar.setEnabled(true);
        holder.btnEliminar.setEnabled(true);

        // Los botones de Detener/Reanudar fueron eliminados del XML, se remueve su lógica de visibilidad.
        holder.btnEditar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.azul_editar)));
        holder.btnEliminar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.rojo_eliminar)));

        // Listeners de botones
        holder.btnEditar.setOnClickListener(v -> listener.onActualizarClick(p));
        // REMOVIDO: holder.btnDetener.setOnClickListener(v -> listener.onDetenerClick(p));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(p));
        // REMOVIDO: holder.btnReanudar.setOnClickListener(v -> listener.onReanudarClick(p));
    }

    @Override
    public int getItemCount() {
        return listaProgramaciones != null ? listaProgramaciones.size() : 0;
    }

    private String formatearFecha(OffsetDateTime fecha) {
        if (fecha == null) {
            return "-";
        }
        return fecha.format(OUTPUT_FORMATTER);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaActivacion, tvFechaDesactivacion, tvDescripcion, tvTipoRiego;
        Button btnEditar, btnEliminar; // REMOVIDO: btnDetener, btnReanudar

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaActivacion = itemView.findViewById(R.id.tvFechaActivacionRiego);
            tvFechaDesactivacion = itemView.findViewById(R.id.tvFechaDesactivacionRiego);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProgramacionRiego);
            tvTipoRiego = itemView.findViewById(R.id.tvTipoRiegoProgramacionRiego);

            // REMOVIDO: btnDetener = itemView.findViewById(R.id.btnDetenerRiego);
            btnEditar = itemView.findViewById(R.id.btnEditarRiego);
            btnEliminar = itemView.findViewById(R.id.btnEliminarRiego);
            // REMOVIDO: btnReanudar = itemView.findViewById(R.id.btnReanudarRiego);
        }
    }
}