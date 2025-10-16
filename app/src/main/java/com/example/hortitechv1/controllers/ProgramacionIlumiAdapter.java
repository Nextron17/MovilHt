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
import com.example.hortitechv1.models.ProgramacionIluminacion;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProgramacionIlumiAdapter extends RecyclerView.Adapter<ProgramacionIlumiAdapter.ViewHolder> {

    private Context context;
    private List<ProgramacionIluminacion> listaProgramaciones;
    private OnItemClickListener listener;

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public interface OnItemClickListener {
        void onActualizarClick(ProgramacionIluminacion programacion);
        void onDetenerClick(ProgramacionIluminacion programacion);
        void onEliminarClick(ProgramacionIluminacion programacion);
        void onReanudarClick(ProgramacionIluminacion programacion);
    }

    public ProgramacionIlumiAdapter(Context context, List<ProgramacionIluminacion> listaProgramaciones, OnItemClickListener listener) {
        this.context = context;
        this.listaProgramaciones = listaProgramaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_programacion_iluminacion,
                parent,
                false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProgramacionIluminacion p = listaProgramaciones.get(position);

        holder.tvDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");
        holder.tvFechaActivacion.setText("Inicio: " + formatearFecha(p.getFecha_inicio()));
        holder.tvFechaDesactivacion.setText("Fin: " + formatearFecha(p.getFecha_finalizacion()));

        if (p.isEstado()) {
            holder.btnDetener.setVisibility(View.VISIBLE);
            holder.btnReanudar.setVisibility(View.GONE);

            holder.btnEditar.setEnabled(false);
            holder.btnEliminar.setEnabled(false);

            holder.btnEditar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gris_desactivado)));
            holder.btnEliminar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gris_desactivado)));

        } else {
            // Estado INACTIVO
            holder.btnDetener.setVisibility(View.GONE);
            holder.btnReanudar.setVisibility(View.VISIBLE);

            holder.btnEditar.setEnabled(true);
            holder.btnEliminar.setEnabled(true);

            holder.btnEditar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.azul_editar)));
            holder.btnEliminar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.rojo_eliminar)));
        }

        holder.btnEditar.setOnClickListener(v -> listener.onActualizarClick(p));
        holder.btnDetener.setOnClickListener(v -> listener.onDetenerClick(p));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(p));
        holder.btnReanudar.setOnClickListener(v -> listener.onReanudarClick(p));
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
        TextView tvFechaActivacion, tvFechaDesactivacion, tvDescripcion;
        Button btnDetener, btnEditar, btnEliminar, btnReanudar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaActivacion = itemView.findViewById(R.id.tvFechaActivacionilumi);
            tvFechaDesactivacion = itemView.findViewById(R.id.tvFechaDesactivacionilumi);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProgramacionIlu);

            btnDetener = itemView.findViewById(R.id.btnDetenerIluminacion);
            btnEditar = itemView.findViewById(R.id.btnEditarIluminacion);
            btnEliminar = itemView.findViewById(R.id.btnEliminarIluminacion);
            btnReanudar = itemView.findViewById(R.id.btnReanudarIluminacion);
        }
    }
}