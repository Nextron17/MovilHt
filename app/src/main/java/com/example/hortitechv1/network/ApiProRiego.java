package com.example.hortitechv1.network;

import com.example.hortitechv1.models.ProgramacionRiego;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiProRiego {

    @GET("api/programacionRiego/zona/{idZona}/futuras")
    Call<List<ProgramacionRiego>> getProgramacionesFuturas(@Path("idZona") int idZona);

    @POST("api/programacionRiego")
    Call<ProgramacionRiego> crearProgramacion(@Body ProgramacionRiego programacion);

    @PUT("api/programacionRiego/{id}")
    Call<ProgramacionRiego> actualizarProgramacion(@Path("id") int id, @Body ProgramacionRiego programacion);

    // Endpoint para cambiar el estado (detener)
    @PATCH("api/programacionRiego/{id}/estado")
    Call<ProgramacionRiego> cambiarEstadoProgramacion(@Path("id") int id, @Body ProgramacionRiego programacion);

    @DELETE("api/programacionRiego/{id}")
    Call<Void> eliminarProgramacion(@Path("id") int id);
}