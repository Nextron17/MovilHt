package com.example.hortitechv1.network;

import com.example.hortitechv1.models.ProgramacionIluminacion;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiProIluminacion {

        @GET("api/programacioniluminacion/zona/{idZona}/futuras")
        Call<List<ProgramacionIluminacion>> getProgramacionesFuturas(@Path("idZona") int idZona);

        @POST("api/programacioniluminacion")
        Call<ProgramacionIluminacion> crearProgramacion(@Body ProgramacionIluminacion programacion);

        @PUT("api/programacioniluminacion/{id}")
        Call<ProgramacionIluminacion> actualizarProgramacion(@Path("id") int id, @Body ProgramacionIluminacion programacion);

        @PATCH("api/Programacioniluminacion/{id}/estado")
        Call<ProgramacionIluminacion> cambiarEstadoProgramacion(@Path("id") int id, @Body ProgramacionIluminacion programacion);

        @DELETE("api/programacioniluminacion/{id}")
        Call<Void> eliminarProgramacion(@Path("id") int id);
}