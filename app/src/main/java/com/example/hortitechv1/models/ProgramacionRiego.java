package com.example.hortitechv1.models;

// Se quitan las importaciones de java.time.LocalDateTime/OffsetDateTime

public class ProgramacionRiego {
    private Integer id_pg_riego;
    private String descripcion;
    private String tipo_riego;
    private String fecha_inicio; // [MODIFICADO]
    private String fecha_finalizacion; // [MODIFICADO]
    private int id_zona;
    private boolean estado;

    private String created_at; // [MODIFICADO]
    private String updated_at; // [MODIFICADO]

    public ProgramacionRiego() {}

    public ProgramacionRiego(Integer id_pg_riego, String descripcion, String tipo_riego,
                             String fecha_inicio, String fecha_finalizacion, // [MODIFICADO]
                             String created_at, String updated_at, // [MODIFICADO]
                             int id_zona, boolean estado) {
        this.id_pg_riego = id_pg_riego;
        this.descripcion = descripcion;
        setTipo_riego(tipo_riego);
        this.fecha_inicio = fecha_inicio;
        this.fecha_finalizacion = fecha_finalizacion;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.id_zona = id_zona;
        this.estado = estado;
    }

    public Integer getId_pg_riego() {
        return id_pg_riego;
    }

    public void setId_pg_riego(Integer id_pg_riego) {
        this.id_pg_riego = id_pg_riego;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo_riego() {
        return tipo_riego;
    }


    public void setTipo_riego(String tipo_riego) {
        if (tipo_riego == null) {
            this.tipo_riego = null;
        } else {
            String lower = tipo_riego.toLowerCase();
            if (lower.equals("goteo") || lower.equals("aspersión") || lower.equals("manual")) {
                this.tipo_riego = lower;
            } else {
                throw new IllegalArgumentException(
                        "Valor inválido para tipo_riego: " + tipo_riego +
                                ". Solo se permiten: goteo, aspersión, manual."
                );
            }
        }
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

    public int getId_zona() {
        return id_zona;
    }

    public void setId_zona(int id_zona) {
        this.id_zona = id_zona;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
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
}