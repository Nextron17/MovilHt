package com.example.hortitechv1.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.hortitechv1.models.LoginResponse;
import com.example.hortitechv1.models.Persona;
import com.example.hortitechv1.view.MainActivity;

public class SessionManager {
    private static final String PREF_NAME = "HORTITECH_PREFS";
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;

    private static final String KEY_USER_TOKEN = "USER_TOKEN";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_USER_NAME = "USER_NAME";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";
    private static final String KEY_USER_ROL = "USER_ROL";
    private static final String KEY_USER_FOTO_URL = "USER_FOTO_URL";

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUserSession(LoginResponse loginResponse) {
        String token = loginResponse.getToken();
        Persona user = loginResponse.getUser();

        Log.d("PerfilDebug", "Iniciando guardado de sesión...");
        editor.putString(KEY_USER_TOKEN, "Bearer " + token);

        if (user != null) {
            editor.putInt(KEY_USER_ID, user.getId_persona());
            editor.putString(KEY_USER_NAME, user.getNombre_usuario());
            editor.putString(KEY_USER_EMAIL, user.getCorreo());
            if(user.getRol() != null) {
                editor.putString(KEY_USER_ROL, user.getRol().name());
            }

            if (user.getPerfil() != null) {
                Log.d("PerfilDebug", "El objeto Perfil EXISTE.");
                String fotoUrl = user.getPerfil().getFoto_url();
                if (fotoUrl != null && !fotoUrl.isEmpty()) {
                    Log.d("PerfilDebug", "Guardando URL de foto: " + fotoUrl);
                    editor.putString(KEY_USER_FOTO_URL, fotoUrl);
                } else {
                    Log.d("PerfilDebug", "El objeto Perfil existe, PERO la foto_url es nula o vacía.");
                }
            } else {
                Log.d("PerfilDebug", "¡AVISO! El objeto user.getPerfil() es NULO en la respuesta del login.");
            }
        } else {
            Log.d("PerfilDebug", "¡ERROR! El objeto 'user' en la respuesta del login es NULO.");
        }
        editor.apply();
        Log.d("PerfilDebug", "Guardado de sesión finalizado.");
    }

    // Método para actualizar el nombre y correo del usuario
    public void updateUser(String name, String email) {
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public void updateUserFotoUrl(String fotoUrl) {
        editor.putString(KEY_USER_FOTO_URL, fotoUrl);
        editor.apply();
    }

    public String getAuthToken() { return prefs.getString(KEY_USER_TOKEN, null); }
    public int getUserId() { return prefs.getInt(KEY_USER_ID, -1); }
    public String getUserName() { return prefs.getString(KEY_USER_NAME, null); }
    public String getUserEmail() { return prefs.getString(KEY_USER_EMAIL, null); }
    public String getUserRol() { return prefs.getString(KEY_USER_ROL, null); }
    public String getUserFotoUrl() { return prefs.getString(KEY_USER_FOTO_URL, null); }

    public void logoutUser() {
        editor.clear();
        editor.apply();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}