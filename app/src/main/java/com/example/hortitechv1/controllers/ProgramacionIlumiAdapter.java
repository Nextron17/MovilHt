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
import com.example.hortitechv1.models.ProgramacionIluminacion;

import java.util.List;

public class ProgramacionIlumiAdapter extends RecyclerView.Adapter<ProgramacionIlumiAdapter.ViewHolder> {

    private Context context;
    private List<ProgramacionIluminacion> listaProgramaciones;
    private OnItemClickListener listener;

    // Se eliminó la declaración de DateTimeFormatter

    public interface OnItemClickListener {
        void onActualizarClick(ProgramacionIluminacion programacion);
        void onDetenerClick(ProgramacionIluminacion programacion);
        void onEliminarClick(ProgramacionIluminacion programacion);
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

        // [CORREGIDO] Acceso directo al String
        if (p.getFecha_inicio() != null) {
            holder.tvFechaActivacion.setText("Inicio: " + p.getFecha_inicio());
        } else {
            holder.tvFechaActivacion.setText("Inicio: -");
        }

        // [CORREGIDO] Acceso directo al String
        if (p.getFecha_finalizacion() != null) {
            holder.tvFechaDesactivacion.setText("Fin: " + p.getFecha_finalizacion());
        } else {
            holder.tvFechaDesactivacion.setText("Fin: -");
        }

        holder.btnActualizar.setOnClickListener(v -> listener.onActualizarClick(p));
        holder.btnDetener.setOnClickListener(v -> listener.onDetenerClick(p));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(p));
    }

    @Override
    public int getItemCount() {
        return listaProgramaciones != null ? listaProgramaciones.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaActivacion, tvFechaDesactivacion, tvDescripcion;
        Button btnDetener, btnActualizar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaActivacion = itemView.findViewById(R.id.tvFechaActivacionilumi);
            tvFechaDesactivacion = itemView.findViewById(R.id.tvFechaDesactivacionilumi);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProgramacionIlu);
            btnDetener = itemView.findViewById(R.id.btnDetenerIluminacion);
            btnActualizar = itemView.findViewById(R.id.btnActualizarIluminacion);
            btnEliminar = itemView.findViewById(R.id.btnEliminarIluminacion);
        }
    }
}