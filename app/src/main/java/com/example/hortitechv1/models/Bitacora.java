package com.example.hortitechv1.models;

import java.io.Serializable;

public class Bitacora implements Serializable {

    private Integer id_publicacion;
    private String titulo;
    private String contenido;
    private String importancia;
    private String tipo_evento;
    private int id_invernadero;
    private Integer id_zona;
    private int autor_id;
    private boolean archivada;
    private String timestamp_publicacion;
    private String createdAt;
    private String updatedAt;

    private Invernadero invernadero;
    private Zona zona;
    private Autor autor;

    public Bitacora() {}

    public static class Autor implements Serializable {
        private int id_persona;
        private String nombre_usuario;
        public int getId_persona() { return id_persona; }
        public String getNombre_usuario() { return nombre_usuario; }
        @Override public String toString() { return nombre_usuario; }
    }

    public static class Invernadero implements Serializable {
        private int id_invernadero;
        private String nombre;
        private int responsable_id; // CAMPO AÑADIDO
        public int getId_invernadero() { return id_invernadero; }
        public String getNombre() { return nombre; }
        public int getResponsable_id() { return responsable_id; } // GETTER AÑADIDO
        @Override public String toString() { return nombre; }
    }

    public static class Zona implements Serializable {
        private int id_zona;
        private String nombre;
        public int getId_zona() { return id_zona; }
        public String getNombre() { return nombre; }
        @Override public String toString() { return nombre; }
    }

    // Getters y Setters...
    public Integer getId_publicacion() { return id_publicacion; }
    public void setId_publicacion(Integer id_publicacion) { this.id_publicacion = id_publicacion; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getImportancia() { return importancia; }
    public void setImportancia(String importancia) { this.importancia = importancia; }
    public String getTipo_evento() { return tipo_evento; }
    public void setTipo_evento(String tipo_evento) { this.tipo_evento = tipo_evento; }
    public int getId_invernadero() { return id_invernadero; }
    public void setId_invernadero(int id_invernadero) { this.id_invernadero = id_invernadero; }
    public Integer getId_zona() { return id_zona; }
    public void setId_zona(Integer id_zona) { this.id_zona = id_zona; }
    public int getAutor_id() { return autor_id; }
    public void setAutor_id(int autor_id) { this.autor_id = autor_id; }
    public boolean isArchivada() { return archivada; }
    public void setArchivada(boolean archivada) { this.archivada = archivada; }
    public String getTimestamp_publicacion() { return timestamp_publicacion; }
    public void setTimestamp_publicacion(String timestamp_publicacion) { this.timestamp_publicacion = timestamp_publicacion; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public Invernadero getInvernadero() { return invernadero; }
    public void setInvernadero(Invernadero invernadero) { this.invernadero = invernadero; }
    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }
}