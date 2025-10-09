package com.example.hortitechv1.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.models.LoginRequest;
import com.example.hortitechv1.models.LoginResponse;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiUsuario;
import com.example.hortitechv1.services.ServicioDeMensajeria;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN_APP_HORTITECH"; // Etiqueta para depuración
    private SessionManager sessionManager;
    private Button btnLogin;
    private ProgressBar loadingProgressBar;
    private TextInputEditText etCorreo, etContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        if (sessionManager.getAuthToken() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Aseguramos que el ProgressBar esté oculto inicialmente para no bloquear clics
        loadingProgressBar.setVisibility(View.GONE);

        btnLogin.setOnClickListener(v -> {
            // ⭐ LOG DE PRUEBA CRÍTICO: ¿El clic llega al código?
            Log.d(TAG, "LOGIN_TEST: El botón de Iniciar Sesión fue presionado.");

            String correo = etCorreo.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Log.w(TAG, "Validación fallida: campos vacíos.");
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            iniciarSesion(correo, contrasena);
        });
    }

    private void iniciarSesion(String correo, String contrasena) {
        Log.d(TAG, "Iniciando sesión para: " + correo);
        btnLogin.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        ApiUsuario api = ApiClient.getClient().create(ApiUsuario.class);
        api.loginUsuario(new LoginRequest(correo, contrasena)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                btnLogin.setVisibility(View.VISIBLE);
                loadingProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // ⭐ LOG DE ÉXITO
                    Log.i(TAG, "LOGIN_API: Respuesta Exitosa. Código HTTP: " + response.code());
                    sessionManager.saveUserSession(response.body());

                    ServicioDeMensajeria.enviarTokenManualmente(MainActivity.this);

                    Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                } else {
                    // ⭐ LOG DE ERROR DE RESPUESTA (Credenciales o Error del Servidor)
                    Log.e(TAG, "LOGIN_API: Respuesta Fallida. Código HTTP: " + response.code());
                    Toast.makeText(getApplicationContext(), "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                btnLogin.setVisibility(View.VISIBLE);
                loadingProgressBar.setVisibility(View.GONE);
                // ⭐ LOG DE ERROR DE CONEXIÓN
                Log.e(TAG, "LOGIN_API: Fallo de Conexión. Mensaje: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Fallo de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }
}