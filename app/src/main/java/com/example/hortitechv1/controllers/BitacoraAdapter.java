package com.example.hortitechv1.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hortitechv1.R;
import com.example.hortitechv1.models.Bitacora;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class BitacoraAdapter extends RecyclerView.Adapter<BitacoraAdapter.ViewHolder> {

    private final List<Bitacora> bitacoras;
    private final Context context;
    private final BitacoraAdapterListener listener;

    public interface BitacoraAdapterListener {
        void onEditClicked(Bitacora bitacora);
        void onArchiveClicked(Bitacora bitacora, int position);
        void onDeleteClicked(Bitacora bitacora, int position);
    }

    public BitacoraAdapter(Context context, List<Bitacora> bitacoras, BitacoraAdapterListener listener) {
        this.context = context;
        this.bitacoras = bitacoras;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bitacora, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitacora pub = bitacoras.get(position);
        holder.bind(pub, listener);
    }

    @Override public int getItemCount() { return bitacoras.size(); }

    private String formatDate(String date) {
        if (date == null || date.isEmpty()) return "N/A";
        try {
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", new Locale("es", "ES"));
            return OffsetDateTime.parse(date).format(outputFormatter);
        } catch (Exception e) {
            return date.split("T")[0];
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View viewImportance;
        TextView tvTitulo, tvContenido, tvTipoEvento, tvTimestamp, tvUbicacion, tvAutor;
        ImageButton btnEdit, btnArchive, btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            viewImportance = itemView.findViewById(R.id.viewImportance);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvContenido = itemView.findViewById(R.id.tvContenido);
            tvTipoEvento = itemView.findViewById(R.id.tvTipoEvento);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvAutor = itemView.findViewById(R.id.tvAutor);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnArchive = itemView.findViewById(R.id.btnArchive);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(final Bitacora bitacora, final BitacoraAdapterListener listener) {
            tvTitulo.setText(bitacora.getTitulo());
            tvContenido.setText(bitacora.getContenido());
            tvTipoEvento.setText(bitacora.getTipo_evento());
            tvTimestamp.setText(formatDate(bitacora.getTimestamp_publicacion()));

            String autor = (bitacora.getAutor() != null && bitacora.getAutor().getNombre_usuario() != null) ? bitacora.getAutor().getNombre_usuario() : "N/A";
            tvAutor.setText("Autor: " + autor);

            String invernadero = (bitacora.getInvernadero() != null && bitacora.getInvernadero().getNombre() != null) ? bitacora.getInvernadero().getNombre() : "N/A";
            String zona = (bitacora.getZona() != null && bitacora.getZona().getNombre() != null) ? bitacora.getZona().getNombre() : "N/A";
            tvUbicacion.setText("Ubicación: " + invernadero + " - " + zona);

            if (bitacora.getImportancia() != null) {
                int colorRes;
                switch (bitacora.getImportancia().toLowerCase()) {
                    case "alta": colorRes = R.color.color_alta; break;
                    case "media": colorRes = R.color.color_media; break;
                    default: colorRes = R.color.color_baja; break;
                }
                viewImportance.setBackgroundColor(ContextCompat.getColor(context, colorRes));
            }

            boolean isArchivada = bitacora.isArchivada();
            btnDelete.setVisibility(isArchivada ? View.GONE : View.VISIBLE);
            btnEdit.setVisibility(isArchivada ? View.GONE : View.VISIBLE);
            btnArchive.setImageResource(isArchivada ? R.drawable.ic_unarchive : R.drawable.ic_archive);

            btnEdit.setOnClickListener(v -> listener.onEditClicked(bitacora));
            btnArchive.setOnClickListener(v -> listener.onArchiveClicked(bitacora, getAdapterPosition()));
            btnDelete.setOnClickListener(v -> listener.onDeleteClicked(bitacora, getAdapterPosition()));
        }
    }
}