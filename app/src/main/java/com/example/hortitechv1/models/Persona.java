package com.example.hortitechv1.models;

import java.io.Serializable;

public class Persona implements Serializable {
    private int id_persona;
    private String nombre_usuario;
    private String correo;
    private String contrasena;
    private Rol rol;
    private Estado estado;
    private boolean isVerified;
    private String verificationCode;
    private int intentos;
    private Perfil perfil;

    private String contrasena_actual;

    public Persona() {
    }

    @Override
    public String toString() {
        return nombre_usuario;
    }

    // Puedes mantener o remover este constructor si no lo usas
    public Persona(int id_persona,String nombre_usuario, String correo, String contrasena, Rol rol, Estado estado,boolean isVerified,String verificationCode, int intentos ) {
        this.id_persona = id_persona;
        this.nombre_usuario = nombre_usuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
        this.isVerified = isVerified;
        this.verificationCode = verificationCode;
        this.intentos = intentos;
    }


    public int getId_persona() {
        return id_persona;
    }

    public void setId_persona(int id_persona) {
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // --- NUEVOS GETTER Y SETTER ---
    public String getContrasena_actual() {
        return contrasena_actual;
    }

    public void setContrasena_actual(String contrasena_actual) {
        this.contrasena_actual = contrasena_actual;
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

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
}