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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.NotificacionAdapter;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.models.Notificaciones;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiNotificaciones;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// PASO 1: Implementar la interfaz para el menú de navegación
public class NotificacionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvNotificaciones;
    private NotificacionAdapter adapter;
    private List<Notificaciones> listaDeNotificaciones = new ArrayList<>();
    private TextView tvSinNotificaciones;
    private SessionManager sessionManager;

    // PASO 2: Declarar las variables para el DrawerLayout
    private DrawerLayout drawerLayout;
    private LinearLayout mainContentContainer;
    private static final float END_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        sessionManager = new SessionManager(this);

        // --- INICIO: CÓDIGO AÑADIDO PARA EL SIDEBAR ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        mainContentContainer = findViewById(R.id.main_content_container); // Asegúrate de que el contenedor principal tenga este ID en tu XML
        NavigationView navigationView = findViewById(R.id.navigation_view);
        setupDrawerAnimation(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        styleLogoutMenuItem(navigationView.getMenu());
        // Opcional: marca un ítem del menú como seleccionado, por ejemplo el de bitácora/notificaciones
        // navigationView.setCheckedItem(R.id.nav_log);
        // --- FIN: CÓDIGO AÑADIDO PARA EL SIDEBAR ---


        rvNotificaciones = findViewById(R.id.rvNotificaciones);
        tvSinNotificaciones = findViewById(R.id.tvSinNotificaciones);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificacionAdapter(listaDeNotificaciones);
        rvNotificaciones.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarNotificacionesDesdeApi();
    }

    private void cargarNotificacionesDesdeApi() {
        String authToken = sessionManager.getAuthToken();
        if (authToken == null) {
            Toast.makeText(this, "Error: Sesión no válida.", Toast.LENGTH_SHORT).show();
            sessionManager.logoutUser(); // Redirigir al login si no hay token
            return;
        }

        ApiNotificaciones api = ApiClient.getClient().create(ApiNotificaciones.class);
        Call<List<Notificaciones>> call = api.getNotificacionesOperario("Bearer " + authToken);

        call.enqueue(new Callback<List<Notificaciones>>() {
            @Override
            public void onResponse(@NonNull Call<List<Notificaciones>> call, @NonNull Response<List<Notificaciones>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaDeNotificaciones.clear();
                    listaDeNotificaciones.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (listaDeNotificaciones.isEmpty()) {
                        tvSinNotificaciones.setVisibility(View.VISIBLE);
                        rvNotificaciones.setVisibility(View.GONE);
                    } else {
                        tvSinNotificaciones.setVisibility(View.GONE);
                        rvNotificaciones.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(NotificacionesActivity.this, "Error al cargar notificaciones: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Notificaciones>> call, @NonNull Throwable t) {
                tvSinNotificaciones.setVisibility(View.VISIBLE);
                rvNotificaciones.setVisibility(View.GONE);
                Toast.makeText(NotificacionesActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- PASO 3: AÑADIR TODOS LOS MÉTODOS DEL SIDEBAR DE PERFILACTIVITY ---

    private void setupDrawerAnimation(Toolbar toolbar) {
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
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
        // Usamos un delay para que la animación del drawer no se corte
        new android.os.Handler().postDelayed(() -> {
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(NotificacionesActivity.this, HomeActivity.class));
            } else if (itemId == R.id.nav_greenhouses) {
                startActivity(new Intent(NotificacionesActivity.this, InvernaderoActivity.class));
            } else if (itemId == R.id.nav_crops) {
                startActivity(new Intent(NotificacionesActivity.this, CultivosActivity.class));
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(NotificacionesActivity.this, PerfilActivity.class));
            } else if (itemId == R.id.nav_log) {
                // Si ya estamos aquí, solo cerramos el drawer
                // O si es una actividad diferente, iniciamos el intent
                // startActivity(new Intent(NotificacionesActivity.this, BitacoraActivity.class));
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