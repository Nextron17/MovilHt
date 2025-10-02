package com.example.hortitechv1.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // construye la instancia para llamar a los métodos.
    public static final String BASE_URL = "https://backendhortitech.onrender.com/"; // AL final siempre lleva otra /
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit == null) {

            // Conversor
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                            LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME))
                    .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext) ->
                            jsonSerializationContext.serialize(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME)))
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}