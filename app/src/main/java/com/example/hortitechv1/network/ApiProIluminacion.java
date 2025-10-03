package com.example.hortitechv1.network;

import com.example.hortitechv1.models.ProgramacionIluminacion;
import com.example.hortitechv1.models.Zona;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApiProIluminacion {

        //Para historiales
        @GET("api/programacioniluminacion")
        Call<List<ProgramacionIluminacion>> getTodasLasProgramaciones();
        @GET("api/programacioniluminacion/{id}")
        Call<ProgramacionIluminacion> getProgramacionPorId(@Path("id") int id);

        //Listar por zonas
        @GET("api/programacioniluminacion/zona/{idZona}/futuras")
        Call<List<ProgramacionIluminacion>> getProgramacionesFuturas(@Path("idZona") int idZona);
        //Crear la Porgramacion
        @POST("api/programacioniluminacion")
        Call<ProgramacionIluminacion> crearProgramacion(@Body ProgramacionIluminacion programacion);
        //Cambiamos el estado de una programacion para detener la ejecucion
        @PATCH("api/Programacioniluminacion/{id}/estado")
        Call<ProgramacionIluminacion> cambiarEstadoProgramacion(
                @Path("id") int id,
                @Body ProgramacionIluminacion programacion
        );
        @PUT("api/programacioniluminacion/{id}")
        Call<ProgramacionIluminacion> actualizarProgramacion(
                @Path("id") int id,
                @Body ProgramacionIluminacion programacion
        );

        @DELETE("api/programacioniluminacion/{id}")
        Call<Void> eliminarProgramacion(@Path("id") int id);

}
