package com.example.hortitechv1.network;

import com.example.hortitechv1.models.EmailRequest;
import com.example.hortitechv1.models.LoginRequest;
import com.example.hortitechv1.models.LoginResponse;
import com.example.hortitechv1.models.Persona;
import com.example.hortitechv1.models.ResetPasswordRequest;
import com.example.hortitechv1.models.VerifyCodeRequest;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface ApiUsuario {
    @POST("api/auth/login")
    Call<LoginResponse> loginUsuario(@Body LoginRequest loginRequest);

    @GET("api/user/profile")
    Call<Persona> getAuthenticatedUserProfile(@Header("Authorization") String authToken);

    @PUT("api/user/profile")
    Call<ResponseBody> updateAuthenticatedUserProfile(@Header("Authorization") String authToken, @Body Persona persona);

    @POST("api/auth/send-reset-code")
    Call<ResponseBody> sendPasswordResetCode(@Body EmailRequest emailRequest);

    @POST("api/auth/verify-reset-code")
    Call<ResponseBody> verifyPasswordResetCode(@Body VerifyCodeRequest verifyCodeRequest);

    @POST("api/auth/reset-password")
    Call<ResponseBody> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @Multipart
    @POST("api/user/profile/photo") // Asegúrate que esta ruta coincida con tu backend
    Call<Persona> uploadProfilePicture(
            @Header("Authorization") String authToken,
            @Part MultipartBody.Part profile_picture
    );
}