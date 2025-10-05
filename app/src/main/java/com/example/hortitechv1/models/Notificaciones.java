package com.example.hortitechv1.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Notificaciones implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("leida")
    private boolean leido;

    @SerializedName("timestamp")
    private String timestamp;

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getMensaje() { return mensaje; }
    public boolean isLeido() { return leido; }
    public String getTimestamp() { return timestamp; }
}