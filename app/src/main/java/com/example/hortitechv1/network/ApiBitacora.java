package com.example.hortitechv1.network;

import com.example.hortitechv1.models.Bitacora;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiBitacora {
    @GET("api/bitacora")
    Call<List<Bitacora>> getBitacoras(@Query("archivadas") boolean archivadas);

    @POST("api/bitacora")
    Call<ResponseBody> crearBitacora(@Body Bitacora bitacora);

    @PUT("api/bitacora/{id}")
    Call<ResponseBody> actualizarBitacora(@Path("id") int id, @Body Bitacora bitacora);

    @DELETE("api/bitacora/{id}")
    Call<ResponseBody> eliminarBitacora(@Path("id") int id);

    @PATCH("api/bitacora/{id}/archivar")
    Call<ResponseBody> archivarBitacora(@Path("id") int id);

    @PATCH("api/bitacora/{id}/desarchivar")
    Call<ResponseBody> desarchivarBitacora(@Path("id") int id);

    // Endpoints actualizados para referenciar a las clases internas
    @GET("api/persona")
    Call<List<Bitacora.Autor>> getAutores();

    @GET("api/invernadero")
    Call<List<Bitacora.Invernadero>> getInvernaderos();

    @GET("api/zona/invernadero/{id}")
    Call<List<Bitacora.Zona>> getZonasPorInvernadero(@Path("id") int idInvernadero);
}