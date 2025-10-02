package com.example.hortitechv1.network;

import com.example.hortitechv1.models.Notificaciones;
import com.example.hortitechv1.models.TokenRequest;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiNotificaciones {

    @POST("api/notificaciones/register")
    Call<ResponseBody> registrarToken(@Body TokenRequest tokenRequest);

    @GET("api/notificaciones/user/{id_persona}")
    Call<List<Notificaciones>> getNotificaciones(@Header("Authorization") String authToken, @Path("id_persona") String idPersona);
}