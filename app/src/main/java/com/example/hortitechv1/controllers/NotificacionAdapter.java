package com.example.hortitechv1.controllers;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hortitechv1.R;
import com.example.hortitechv1.models.Notificaciones;
import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.ViewHolder> {

    private final List<Notificaciones> notificaciones;

    public NotificacionAdapter(List<Notificaciones> notificaciones) {
        this.notificaciones = notificaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificaciones notificacion = notificaciones.get(position);

        holder.tvTitulo.setText(notificacion.getTitulo());
        holder.tvMensaje.setText(notificacion.getMensaje());
        holder.tvTimestamp.setText(notificacion.getTimestamp()); // Usar el campo correcto

        if (notificacion.isLeido()) {
            holder.puntoNoLeido.setVisibility(View.GONE);
            holder.tvTitulo.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.puntoNoLeido.setVisibility(View.VISIBLE);
            holder.tvTitulo.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcono;
        TextView tvTitulo, tvMensaje, tvTimestamp;
        View puntoNoLeido;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcono = itemView.findViewById(R.id.ivIconoNotificacion);
            tvTitulo = itemView.findViewById(R.id.tvTituloNotificacion);
            tvMensaje = itemView.findViewById(R.id.tvMensajeNotificacion);
            tvTimestamp = itemView.findViewById(R.id.tvTimestampNotificacion);
            puntoNoLeido = itemView.findViewById(R.id.viewPuntoNoLeido);
        }
    }
}