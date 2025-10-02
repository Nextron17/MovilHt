package com.example.hortitechv1.models;

import java.io.Serializable;

public class Zona implements Serializable {
    public enum Estado_iluminacion{
        activo, inactivo
    }

    private int id_zona;
    private String nombre;
    private String descripciones_add;
    private Estado estado;
    private int id_invernadero;

    // private GestionCultivos id_cultivo;
    private Estado_iluminacion estado_iluminacion;

    public Zona(){

    }
    public Zona(int id_zona, String nombre, String descripciones_add, Estado estado, int id_invernadero, Estado_iluminacion estado_iluminacion) {
        this.id_zona = id_zona;
        this.nombre = nombre;
        this.descripciones_add = descripciones_add;
        this.estado = estado;
        this.id_invernadero = id_invernadero;
        this.estado_iluminacion = estado_iluminacion;
    }

    public int getId_zona() {
        return id_zona;
    }

    public void setId_zona(int id_zona) {
        this.id_zona = id_zona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripciones_add() {
        return descripciones_add;
    }

    public void setDescripciones_add(String descripciones_add) {
        this.descripciones_add = descripciones_add;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public int getId_invernadero() {
        return id_invernadero;
    }

    public void setId_invernadero(int id_invernadero) {
        this.id_invernadero = id_invernadero;
    }

    public Estado_iluminacion getEstado_iluminacion() {
        return estado_iluminacion;
    }

    public void setEstado_iluminacion(Estado_iluminacion estado_iluminacion) {
        this.estado_iluminacion = estado_iluminacion;
    }
}
