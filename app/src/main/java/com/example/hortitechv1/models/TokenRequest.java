package com.example.hortitechv1.models;

import com.google.gson.annotations.SerializedName;

public class TokenRequest {
    @SerializedName("id_persona")
    private String id_persona;

    private String token;
    private String plataforma = "mobile";

    // El constructor ahora necesita el id_persona
    public TokenRequest(String id_persona, String token) {
        this.id_persona = id_persona;
        this.token = token;
    }
}