package com.example.hortitechv1.network;

import com.example.hortitechv1.models.Cultivo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiCultivos {

    @GET("api/cultivos")
    Call<List<Cultivo>> getCultivos();
}