package com.example.hortitechv1.models;

// Se quitan las importaciones de java.time.LocalDateTime/OffsetDateTime

public class ProgramacionIluminacion {
    private Integer id_iluminacion;
    private int id_zona;
    private String descripcion;
    private String fecha_inicio; // [MODIFICADO]
    private String fecha_finalizacion; // [MODIFICADO]
    private String created_at; // [MODIFICADO]
    private String updated_at; // [MODIFICADO]
    private boolean estado;

    //Constructor vacio
    public ProgramacionIluminacion(){
    }

    public ProgramacionIluminacion(Integer id_iluminacion, int id_zona, String descripcion, String fecha_inicio, String fecha_finalizacion, String created_at, String updated_at, boolean estado){
        this.id_iluminacion = id_iluminacion;
        this.id_zona = id_zona;
        this.descripcion = descripcion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_finalizacion= fecha_finalizacion;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.estado = estado;
    }

    public Integer getId_iluminacion() {
        return id_iluminacion;
    }

    public void setId_iluminacion(Integer id_iluminacion) {
        this.id_iluminacion = id_iluminacion;
    }

    public int getId_zona() {
        return id_zona;
    }

    public void setId_zona(int id_zona) {
        this.id_zona = id_zona;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha_inicio() { // [MODIFICADO]
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) { // [MODIFICADO]
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_finalizacion() { // [MODIFICADO]
        return fecha_finalizacion;
    }

    public void setFecha_finalizacion(String fecha_finalizacion) { // [MODIFICADO]
        this.fecha_finalizacion = fecha_finalizacion;
    }

    public String getCreated_at() { // [MODIFICADO]
        return created_at;
    }

    public void setCreated_at(String created_at) { // [MODIFICADO]
        this.created_at = created_at;
    }

    public String getUpdated_at() { // [MODIFICADO]
        return updated_at;
    }

    public void setUpdated_at(String updated_at) { // [MODIFICADO]
        this.updated_at = updated_at;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}