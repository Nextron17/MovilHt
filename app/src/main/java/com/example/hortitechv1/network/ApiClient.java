package com.example.hortitechv1.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Base URL
    public static final String BASE_URL = "https://backendhortitech.onrender.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {

            // Serializer & Deserializer para OffsetDateTime
            JsonDeserializer<OffsetDateTime> deserializer = (json, type, ctx) ->
                    OffsetDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            JsonSerializer<OffsetDateTime> serializer = (odt, type, ctx) ->
                    ctx.serialize(odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(OffsetDateTime.class, deserializer)
                    .registerTypeAdapter(OffsetDateTime.class, serializer)
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