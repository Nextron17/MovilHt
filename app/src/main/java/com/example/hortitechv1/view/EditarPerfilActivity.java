package com.example.hortitechv1.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.models.Persona;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiUsuario;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextInputEditText etNombreUsuario, etCorreo, etContrasena, etContrasenaActual;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private LinearLayout mainContentContainer;
    private static final float END_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        mainContentContainer = findViewById(R.id.main_content_container);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        setupDrawerAnimation(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        styleLogoutMenuItem(navigationView.getMenu());
        navigationView.setCheckedItem(R.id.nav_settings);

        sessionManager = new SessionManager(this);

        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etContrasenaActual = findViewById(R.id.etContrasenaActual);
        Button btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        etNombreUsuario.setText(sessionManager.getUserName());
        etCorreo.setText(sessionManager.getUserEmail());

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());
    }

    private void guardarCambios() {
        String nombre = etNombreUsuario.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String contrasenaActual = etContrasenaActual.getText().toString().trim();
        String contrasenaNueva = etContrasena.getText().toString().trim();

        boolean quiereCambiarPassword = !contrasenaActual.isEmpty() || !contrasenaNueva.isEmpty();

        if (quiereCambiarPassword) {
            if (contrasenaActual.isEmpty()) {
                etContrasenaActual.setError("Ingresa tu contraseña actual para cambiarla.");
                etContrasenaActual.requestFocus();
                return;
            }
            if (contrasenaNueva.isEmpty()) {
                etContrasena.setError("Ingresa la nueva contraseña.");
                etContrasena.requestFocus();
                return;
            }
            if (contrasenaNueva.length() < 6) {
                etContrasena.setError("La nueva contraseña debe tener al menos 6 caracteres.");
                etContrasena.requestFocus();
                return;
            }
        }

        Persona personaActualizada = new Persona();
        personaActualizada.setNombre_usuario(nombre);
        personaActualizada.setCorreo(correo);

        if (quiereCambiarPassword) {
            personaActualizada.setContrasena_actual(contrasenaActual);
            personaActualizada.setContrasena(contrasenaNueva);
        }

        ApiUsuario api = ApiClient.getClient().create(ApiUsuario.class);
        api.updateAuthenticatedUserProfile(sessionManager.getAuthToken(), personaActualizada).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    sessionManager.updateUser(nombre, correo);
                    Toast.makeText(EditarPerfilActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMessage = "Error al actualizar el perfil";
                    if (response.code() == 400 || response.code() == 401) {
                        errorMessage = "La contraseña actual es incorrecta.";
                    }
                    Toast.makeText(EditarPerfilActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditarPerfilActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDrawerAnimation(Toolbar toolbar) {
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                final float scale = 1 - (1 - END_SCALE) * slideOffset;
                mainContentContainer.setScaleX(scale);
                mainContentContainer.setScaleY(scale);
                final float xOffset = drawerView.getWidth() * slideOffset;
                mainContentContainer.setTranslationX(xOffset);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        new android.os.Handler().postDelayed(() -> {
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(EditarPerfilActivity.this, HomeActivity.class));
            } else if (itemId == R.id.nav_greenhouses) {
                startActivity(new Intent(EditarPerfilActivity.this, InvernaderoActivity.class));
            } else if (itemId == R.id.nav_crops) {
                startActivity(new Intent(EditarPerfilActivity.this, CultivosActivity.class));
            } else if (itemId == R.id.nav_log) {
                startActivity(new Intent(EditarPerfilActivity.this, BitacoraActivity.class));
            }
        }, 250);

        if (itemId == R.id.nav_logout) {
            sessionManager.logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void styleLogoutMenuItem(Menu menu) {
        MenuItem logoutItem = menu.findItem(R.id.nav_logout);
        if (logoutItem != null) {
            SpannableString s = new SpannableString(logoutItem.getTitle());
            s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorError)), 0, s.length(), 0);
            logoutItem.setTitle(s);
            Drawable icon = logoutItem.getIcon();
            if (icon != null) {
                Drawable wrappedIcon = DrawableCompat.wrap(icon);
                DrawableCompat.setTint(wrappedIcon, ContextCompat.getColor(this, R.color.colorError));
                logoutItem.setIcon(wrappedIcon);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}