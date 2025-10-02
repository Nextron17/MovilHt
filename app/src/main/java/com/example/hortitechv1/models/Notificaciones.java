package com.example.hortitechv1.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Notificaciones implements Serializable {
    @SerializedName("id_notificacion")
    private int id_notificacion;
    private String titulo;
    private String mensaje;
    private boolean leido;
    @SerializedName("timestamp_envio")
    private String timestamp_envio;

    public Notificaciones(int id_notificacion, String titulo, String mensaje, boolean leido, String timestamp_envio) {
        this.id_notificacion = id_notificacion;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leido = leido;
        this.timestamp_envio = timestamp_envio;
    }

    public int getId_notificacion() { return id_notificacion; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public boolean isLeido() { return leido; }
    public String getTimestamp_envio() { return timestamp_envio; }
}