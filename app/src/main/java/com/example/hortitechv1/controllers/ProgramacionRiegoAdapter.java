package com.example.hortitechv1.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    // Formateador de fechas
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public interface OnItemClickListener {
        void onActualizarClick(ProgramacionRiego programacion);
        void onDetenerClick(ProgramacionRiego programacion);
        void onEliminarClick(ProgramacionRiego programacion);
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

        // Descripción
        holder.tvDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");

        // Fecha inicio
        holder.tvFechaActivacion.setText("Inicio: " + formatearFecha(p.getFecha_inicio()));

        // Fecha fin
        holder.tvFechaDesactivacion.setText("Fin: " + formatearFecha(p.getFecha_finalizacion()));

        // Tipo de riego
        holder.tvTipoRiego.setText("Tipo: " + (p.getTipo_riego() != null ? p.getTipo_riego() : "-"));

        // Listeners de botones
        holder.btnActualizar.setOnClickListener(v -> listener.onActualizarClick(p));
        holder.btnDetener.setOnClickListener(v -> listener.onDetenerClick(p));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(p));
    }

    @Override
    public int getItemCount() {
        return listaProgramaciones != null ? listaProgramaciones.size() : 0;
    }

    // Método para formatear las fechas
    private String formatearFecha(OffsetDateTime fecha) {
        if (fecha == null) {
            return "-";
        }
        return fecha.format(OUTPUT_FORMATTER);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaActivacion, tvFechaDesactivacion, tvDescripcion, tvTipoRiego;
        Button btnDetener, btnActualizar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaActivacion = itemView.findViewById(R.id.tvFechaActivacionRiego);
            tvFechaDesactivacion = itemView.findViewById(R.id.tvFechaDesactivacionRiego);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProgramacionRiego);
            tvTipoRiego = itemView.findViewById(R.id.tvTipoRiegoProgramacionRiego);
            btnDetener = itemView.findViewById(R.id.btnDetenerRiego);
            btnActualizar = itemView.findViewById(R.id.btnActualizarRiego);
            btnEliminar = itemView.findViewById(R.id.btnEliminarRiego);
        }
    }
}
