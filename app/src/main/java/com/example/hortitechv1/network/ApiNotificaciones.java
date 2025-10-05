package com.example.hortitechv1.network;

import com.example.hortitechv1.models.FcmTokenRequest;
import com.example.hortitechv1.models.Notificaciones;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiNotificaciones {

    @POST("api/users/fcm-token")
    Call<Void> sendFcmToken(@Header("Authorization") String authToken, @Body FcmTokenRequest request);

    @GET("api/notificaciones/operario")
    Call<List<Notificaciones>> getNotificacionesOperario(@Header("Authorization") String authToken);
}