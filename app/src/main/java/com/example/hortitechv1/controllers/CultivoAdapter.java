package com.example.hortitechv1.controllers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hortitechv1.R;
import com.example.hortitechv1.models.Cultivo;

import java.util.List;

public class CultivoAdapter extends RecyclerView.Adapter<CultivoAdapter.ViewHolder> {

    private final List<Cultivo> cultivos;
    private final Context context;

    public CultivoAdapter(Context context, List<Cultivo> cultivos) {
        this.context = context;
        this.cultivos = cultivos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cultivo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cultivo cultivo = cultivos.get(position);

        Glide.with(context)
                .load(cultivo.getImagenes())
                .placeholder(R.drawable.sprout) // Imagen de placeholder
                .into(holder.ivCultivoImage);

        holder.tvNombre.setText(cultivo.getNombre_cultivo());
        holder.tvDescripcion.setText(cultivo.getDescripcion());
        holder.tvEstado.setText(cultivo.getEstado());

        if ("activo".equalsIgnoreCase(cultivo.getEstado())) {
            holder.tvEstado.setBackgroundResource(R.drawable.badge_activo_background);
        } else {
            holder.tvEstado.setBackgroundResource(R.drawable.badge_finalizado_background);
        }

        holder.tvZonaId.setText("📍 Zona ID: " + cultivo.getId_zona());
        holder.tvTemperatura.setText("🌡️ Temp: " + cultivo.getTemp_min() + "°C - " + cultivo.getTemp_max() + "°C");
        holder.tvHumedad.setText("💧 Humedad: " + cultivo.getHumedad_min() + "% - " + cultivo.getHumedad_max() + "%");

        String fechaFin = (cultivo.getFecha_fin() != null && !cultivo.getFecha_fin().equals("null") && !cultivo.getFecha_fin().isEmpty()) ? cultivo.getFecha_fin().split("T")[0] : "Presente";
        String fechaInicio = (cultivo.getFecha_inicio() != null) ? cultivo.getFecha_inicio().split("T")[0] : "N/A";
        holder.tvFechas.setText("📅 " + fechaInicio + " - " + fechaFin);

        // Lógica para unidad de medida
        String unidad = cultivo.getUnidad_medida() != null ? " " + cultivo.getUnidad_medida() : "";
        holder.tvCosechada.setText("Cosechado: " + cultivo.getCantidad_cosechada() + unidad);
        holder.tvDisponible.setText("Disponible: " + cultivo.getCantidad_disponible() + unidad);
        holder.tvReservada.setText("Reservado: " + cultivo.getCantidad_reservada() + unidad);

        String fechaCreacion = (cultivo.getCreatedAt() != null) ? cultivo.getCreatedAt().split("T")[0] : "N/A";
        String fechaActualizacion = (cultivo.getUpdatedAt() != null) ? cultivo.getUpdatedAt().split("T")[0] : "N/A";
        holder.tvFechaCreacion.setText("Creado: " + fechaCreacion);
        holder.tvFechaActualizacion.setText("Actualizado: " + fechaActualizacion);
    }

    @Override
    public int getItemCount() {
        return cultivos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEstado, tvDescripcion, tvTemperatura, tvHumedad, tvFechas;
        TextView tvZonaId, tvCosechada, tvDisponible, tvReservada, tvFechaCreacion, tvFechaActualizacion;
        ImageView ivCultivoImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivCultivoImage = itemView.findViewById(R.id.ivCultivoImage);
            tvNombre = itemView.findViewById(R.id.tvNombreCultivo);
            tvEstado = itemView.findViewById(R.id.tvEstadoCultivo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionCultivo);
            tvZonaId = itemView.findViewById(R.id.tvZonaId);
            tvTemperatura = itemView.findViewById(R.id.tvTemperatura);
            tvHumedad = itemView.findViewById(R.id.tvHumedad);
            tvFechas = itemView.findViewById(R.id.tvFechas);
            tvCosechada = itemView.findViewById(R.id.tvCosechada);
            tvDisponible = itemView.findViewById(R.id.tvDisponible);
            tvReservada = itemView.findViewById(R.id.tvReservada);
            tvFechaCreacion = itemView.findViewById(R.id.tvFechaCreacion);
            tvFechaActualizacion = itemView.findViewById(R.id.tvFechaActualizacion);
        }
    }
}