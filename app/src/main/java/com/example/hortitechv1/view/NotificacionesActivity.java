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
import com.example.hortitechv1.models.Notificaciones; // O Notificaciones, según lo tengas nombrado
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiNotificaciones; // O ApiNotificaciones
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvNotificaciones;
    private NotificacionAdapter adapter;
    private List<Notificaciones> listaDeNotificaciones = new ArrayList<>(); // Asegúrate que el tipo sea el correcto
    private TextView tvSinNotificaciones;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private LinearLayout mainContentContainer;
    private static final float END_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        mainContentContainer = findViewById(R.id.main_content_container);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupDrawerAnimation(toolbar);
        styleLogoutMenuItem(navigationView.getMenu());
        navigationView.setCheckedItem(R.id.nav_settings); // Puede que quieras cambiar esto dependiendo de la lógica

        rvNotificaciones = findViewById(R.id.rvNotificaciones);
        tvSinNotificaciones = findViewById(R.id.tvSinNotificaciones);

        rvNotificaciones.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificacionAdapter(this, listaDeNotificaciones); // Asegúrate que el adapter acepte el tipo de lista correcto
        rvNotificaciones.setAdapter(adapter);

        // --- CAMBIO ---
        // La llamada a la API ya no se hace aquí para poder refrescar la lista.
        // cargarNotificacionesDesdeApi();
    }

    // +++ MÉTODO AÑADIDO +++
    // onResume se ejecuta cada vez que la actividad se muestra en pantalla.
    // Es el lugar ideal para asegurar que los datos estén siempre actualizados.
    @Override
    protected void onResume() {
        super.onResume();
        cargarNotificacionesDesdeApi();
    }

    private void cargarNotificacionesDesdeApi() {
        int userIdInt = sessionManager.getUserId();

        if (userIdInt == -1) {
            Toast.makeText(this, "Error: No se pudo identificar al usuario.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userIdString = String.valueOf(userIdInt);

        ApiNotificaciones api = ApiClient.getClient().create(ApiNotificaciones.class);
        Call<List<Notificaciones>> call = api.getNotificaciones(sessionManager.getAuthToken(), userIdString);
        call.enqueue(new Callback<List<Notificaciones>>() {
            @Override
            public void onResponse(Call<List<Notificaciones>> call, Response<List<Notificaciones>> response) {
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
            public void onFailure(Call<List<Notificaciones>> call, Throwable t) {
                tvSinNotificaciones.setVisibility(View.VISIBLE);
                rvNotificaciones.setVisibility(View.GONE);
                Toast.makeText(NotificacionesActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- El resto de tus métodos no cambian ---

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
        new android.os.Handler().postDelayed(() -> {
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(NotificacionesActivity.this, HomeActivity.class));
            } else if (itemId == R.id.nav_greenhouses) {
                startActivity(new Intent(NotificacionesActivity.this, InvernaderoActivity.class));
            } else if (itemId == R.id.nav_crops) {
                startActivity(new Intent(NotificacionesActivity.this, CultivosActivity.class));
            } else if (itemId == R.id.nav_log) {
                startActivity(new Intent(NotificacionesActivity.this, BitacoraActivity.class));
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