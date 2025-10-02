package com.example.hortitechv1.network;

import com.example.hortitechv1.models.Invernadero;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiInvernaderos {
    //Solo utilizaremos el metodo get porque solo es vista
    @GET("api/invernadero")
        Call<List<Invernadero>> getInvernaderos();

}
