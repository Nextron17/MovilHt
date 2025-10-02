package com.example.hortitechv1.models;

import java.io.Serializable;

public class Perfil implements Serializable {
    private int id_perfil;
    private Persona id_persona; //lave foranea Persona
    private String nombre_usuario;
    private String correo;
    private String foto_url;
    private Rol rol;
    private Estado estado;
    private boolean isVerified;

    public Perfil(){

    }
    public Perfil(int id_perfil, Persona id_persona,String nombre_usuario, String correo, String foto_url, Rol rol, Estado estado, boolean isVerified){
        this.id_perfil = id_perfil;
        this.id_persona = id_persona;
        this.nombre_usuario = nombre_usuario;
        this.correo = correo;
        this.foto_url = foto_url;
        this.rol = rol;
        this.estado = estado;
        this.isVerified = isVerified;
    }

    public int getId_perfil() {
        return id_perfil;
    }

    public void setId_perfil(int id_perfil) {
        this.id_perfil = id_perfil;
    }

    public Persona getId_persona() {
        return id_persona;
    }

    public void setId_persona(Persona id_persona) {
        this.id_persona = id_persona;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFoto_url() {
        return foto_url;
    }

    public void setFoto_url(String foto_url) {
        this.foto_url = foto_url;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}
