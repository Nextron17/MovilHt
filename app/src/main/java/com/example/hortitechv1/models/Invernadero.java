package com.example.hortitechv1.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Invernadero implements Serializable {
    @SerializedName("id_invernadero")
    private int id_invernadero;
    private String nombre;
    private String descripcion;
    private String estado;
    @SerializedName("zonas_totales")
    private int zonas_totales;
    @SerializedName("zonas_activas")
    private int zonas_activas;
    @SerializedName("responsable_id")
    private int responsable_id;
    private Persona encargado;

    // Getters
    public int getId_invernadero() { return id_invernadero; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public int getZonas_totales() { return zonas_totales; }
    public int getZonas_activas() { return zonas_activas; }
    public int getResponsable_id() { return responsable_id; }
    public Persona getEncargado() { return encargado; }
}