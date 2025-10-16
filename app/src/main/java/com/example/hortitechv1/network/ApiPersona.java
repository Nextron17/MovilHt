package com.example.hortitechv1.network;

import com.example.hortitechv1.models.Persona;
import com.example.hortitechv1.models.Zona;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiPersona {
    @GET("api/persona")
    Call<List<Persona>> getPersona();
    @GET("api/persona/operarios")
    Call<List<Persona>> getOperarios();
    @GET("api/persona/activos")
    Call<List<Persona>> getActivos();
}
