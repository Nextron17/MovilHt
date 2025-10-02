package com.example.hortitechv1.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.example.hortitechv1.R;
import com.example.hortitechv1.models.EmailRequest;
import com.example.hortitechv1.models.ResetPasswordRequest;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiUsuario;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etCode, etNewPassword;
    private Button btnSendCode, btnResetPassword;
    private LinearLayout layoutCodePassword;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        etCode = findViewById(R.id.etCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        layoutCodePassword = findViewById(R.id.layoutCodePassword);

        btnSendCode.setOnClickListener(v -> sendCode());
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void sendCode() {
        userEmail = etEmail.getText().toString().trim();
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu correo.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiUsuario api = ApiClient.getClient().create(ApiUsuario.class);
        api.sendPasswordResetCode(new EmailRequest(userEmail)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Código enviado a tu correo.", Toast.LENGTH_SHORT).show();
                    layoutCodePassword.setVisibility(View.VISIBLE);
                    etEmail.setEnabled(false);
                    btnSendCode.setEnabled(false);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error: El correo no está registrado.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Fallo de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword() {
        String code = etCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (code.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa el código y la nueva contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiUsuario api = ApiClient.getClient().create(ApiUsuario.class);
        ResetPasswordRequest request = new ResetPasswordRequest(userEmail, code, newPassword);
        api.resetPassword(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Contraseña actualizada con éxito.", Toast.LENGTH_LONG).show();
                    finish(); // Vuelve a la pantalla de login
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Error: Código incorrecto o expirado.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Fallo de conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}