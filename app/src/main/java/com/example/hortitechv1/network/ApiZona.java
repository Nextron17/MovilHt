package com.example.hortitechv1.network;

import com.example.hortitechv1.models.Zona;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiZona {
    @GET("api/zona")
    Call<List<Zona>> getZonas();

    @GET("api/zona/invernadero/{id}")
    Call<List<Zona>> getZonasPorInvernadero(@Path("id") int id);
}

