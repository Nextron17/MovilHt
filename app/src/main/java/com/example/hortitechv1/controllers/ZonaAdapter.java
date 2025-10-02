package com.example.hortitechv1.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hortitechv1.R;
import com.example.hortitechv1.models.Invernadero;
import com.example.hortitechv1.models.Zona;
import com.example.hortitechv1.view.ProgramacionIluminacionActivity;
import com.example.hortitechv1.view.ProgramacionRiegoActivity;


import java.util.List;

public class ZonaAdapter extends RecyclerView.Adapter<ZonaAdapter.ViewHolder> {

    private List<Zona> zonas;
    private List<Invernadero> listaInvernaderos;
    private Context context;

    public ZonaAdapter(Context context, List<Zona> zonas, List<Invernadero> invernaderos) {
        this.context = context;
        this.zonas = zonas;
        this.listaInvernaderos = invernaderos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_zona, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Zona zona = zonas.get(position);
        holder.tvNombre.setText(zona.getNombre());
        holder.tvDescripcion.setText(zona.getDescripciones_add());
        holder.tvEstado.setText("Estado: " + zona.getEstado().name());

        // Obtener nombre del invernadero usando su ID
        int idInvernadero = zona.getId_invernadero();
        String nombreInvernadero = buscarNombreInvernadero(idInvernadero);
        holder.tvIdInvernadero.setText("Invernadero: " + nombreInvernadero);


        holder.btnRiego.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProgramacionRiegoActivity.class);
            intent.putExtra("zona_id", zona.getId_zona());
            context.startActivity(intent);
        });

        // Botón para ir a programación de iluminación
        holder.btnIluminacion.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProgramacionIluminacionActivity.class);
            intent.putExtra("zona_id", zona.getId_zona()); // pasamos el id_zona
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return zonas.size();
    }


    private String buscarNombreInvernadero(int id) {
        for (Invernadero inv : listaInvernaderos) {
            if (inv.getId_invernadero() == id) {
                return inv.getNombre();
            }
        }
        return "Desconocido";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvEstado, tvIdInvernadero;
        Button btnRiego, btnIluminacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreZona);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionesZona);
            tvEstado = itemView.findViewById(R.id.tvEstadoZona);
            tvIdInvernadero = itemView.findViewById(R.id.tvIdInvernaderoZona);
            btnRiego = itemView.findViewById(R.id.btnIrProRiego);
            btnIluminacion = itemView.findViewById(R.id.btnIrProIlu);
        }
    }
}
