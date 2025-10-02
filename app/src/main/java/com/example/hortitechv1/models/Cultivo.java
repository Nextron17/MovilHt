package com.example.hortitechv1.models;

public class Cultivo {

    private int id_cultivo;
    private String nombre_cultivo;
    private String descripcion;
    private double temp_min;
    private double temp_max;
    private double humedad_min;
    private double humedad_max;
    private String fecha_inicio;
    private String fecha_fin;
    private String estado;
    private String imagenes;
    private String unidad_medida;
    private String cantidad_cosechada;
    private String cantidad_disponible;
    private String cantidad_reservada;
    private String createdAt;
    private String updatedAt;
    private int id_zona;
    private int responsable_id;

    public Cultivo() {
    }

    public Cultivo(int id_cultivo, String nombre_cultivo, String descripcion, double temp_min, double temp_max, double humedad_min, double humedad_max, String fecha_inicio, String fecha_fin, String estado, String imagenes, String unidad_medida, String cantidad_cosechada, String cantidad_disponible, String cantidad_reservada, String createdAt, String updatedAt, int id_zona, int responsable_id) {
        this.id_cultivo = id_cultivo;
        this.nombre_cultivo = nombre_cultivo;
        this.descripcion = descripcion;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.humedad_min = humedad_min;
        this.humedad_max = humedad_max;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.estado = estado;
        this.imagenes = imagenes;
        this.unidad_medida = unidad_medida;
        this.cantidad_cosechada = cantidad_cosechada;
        this.cantidad_disponible = cantidad_disponible;
        this.cantidad_reservada = cantidad_reservada;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id_zona = id_zona;
        this.responsable_id = responsable_id;
    }

    public int getId_cultivo() { return id_cultivo; }
    public void setId_cultivo(int id_cultivo) { this.id_cultivo = id_cultivo; }
    public String getNombre_cultivo() { return nombre_cultivo; }
    public void setNombre_cultivo(String nombre_cultivo) { this.nombre_cultivo = nombre_cultivo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getTemp_min() { return temp_min; }
    public void setTemp_min(double temp_min) { this.temp_min = temp_min; }
    public double getTemp_max() { return temp_max; }
    public void setTemp_max(double temp_max) { this.temp_max = temp_max; }
    public double getHumedad_min() { return humedad_min; }
    public void setHumedad_min(double humedad_min) { this.humedad_min = humedad_min; }
    public double getHumedad_max() { return humedad_max; }
    public void setHumedad_max(double humedad_max) { this.humedad_max = humedad_max; }
    public String getFecha_inicio() { return fecha_inicio; }
    public void setFecha_inicio(String fecha_inicio) { this.fecha_inicio = fecha_inicio; }
    public String getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(String fecha_fin) { this.fecha_fin = fecha_fin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getImagenes() { return imagenes; }
    public void setImagenes(String imagenes) { this.imagenes = imagenes; }
    public String getCantidad_cosechada() { return cantidad_cosechada; }
    public void setCantidad_cosechada(String cantidad_cosechada) { this.cantidad_cosechada = cantidad_cosechada; }
    public String getCantidad_disponible() { return cantidad_disponible; }
    public void setCantidad_disponible(String cantidad_disponible) { this.cantidad_disponible = cantidad_disponible; }
    public String getCantidad_reservada() { return cantidad_reservada; }
    public void setCantidad_reservada(String cantidad_reservada) { this.cantidad_reservada = cantidad_reservada; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public int getId_zona() { return id_zona; }
    public void setId_zona(int id_zona) { this.id_zona = id_zona; }

    // Nuevos Getters y Setters
    public String getUnidad_medida() { return unidad_medida; }
    public void setUnidad_medida(String unidad_medida) { this.unidad_medida = unidad_medida; }
    public int getResponsable_id() { return responsable_id; }
    public void setResponsable_id(int responsable_id) { this.responsable_id = responsable_id; }
}