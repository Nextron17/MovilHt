package com.example.hortitechv1.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.InvernaderoAdapter;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.models.Invernadero;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiInvernaderos;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvernaderoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SessionManager sessionManager;
    private RecyclerView rvInvernaderos;
    private DrawerLayout drawerLayout;
    private LinearLayout mainContentContainer;
    private static final float END_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invernadero);
        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        mainContentContainer = findViewById(R.id.main_content_container);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupDrawerAnimation(toolbar);
        navigationView.setCheckedItem(R.id.nav_greenhouses);

        rvInvernaderos = findViewById(R.id.rvInvernaderos);
        rvInvernaderos.setLayoutManager(new LinearLayoutManager(this));

        fetchInvernaderosData();
    }

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

    private void fetchInvernaderosData() {
        int responsableId = sessionManager.getUserId();
        if (responsableId == -1) {
            Toast.makeText(this, "Error: Sesión no válida.", Toast.LENGTH_LONG).show();
            return;
        }

        ApiInvernaderos api = ApiClient.getClient().create(ApiInvernaderos.class);
        api.getInvernaderos().enqueue(new Callback<List<Invernadero>>() {
            @Override
            public void onResponse(@NonNull Call<List<Invernadero>> call, @NonNull Response<List<Invernadero>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Invernadero> listaCompleta = response.body();
                    List<Invernadero> invernaderosFiltrados = new ArrayList<>();

                    for (Invernadero inv : listaCompleta) {
                        if (inv.getResponsable_id() == responsableId) {
                            invernaderosFiltrados.add(inv);
                        }
                    }

                    if (invernaderosFiltrados.isEmpty()) {
                        Toast.makeText(InvernaderoActivity.this, "No tienes invernaderos asignados.", Toast.LENGTH_LONG).show();
                    }

                    InvernaderoAdapter adapter = new InvernaderoAdapter(InvernaderoActivity.this, invernaderosFiltrados);
                    rvInvernaderos.setAdapter(adapter);
                } else {
                    Toast.makeText(InvernaderoActivity.this, "Error al obtener datos: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Invernadero>> call, @NonNull Throwable t) {
                Toast.makeText(InvernaderoActivity.this, "Fallo de conexión.", Toast.LENGTH_SHORT).show();
                Log.e("API_FAIL", "Error en fetchInvernaderosData: ", t);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        new android.os.Handler().postDelayed(() -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(InvernaderoActivity.this, HomeActivity.class));
            } else if (itemId == R.id.nav_crops) {
                startActivity(new Intent(InvernaderoActivity.this, CultivosActivity.class));
            } else if (itemId == R.id.nav_log) {
                startActivity(new Intent(InvernaderoActivity.this, BitacoraActivity.class));
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(InvernaderoActivity.this, PerfilActivity.class));
            } else if (itemId == R.id.nav_logout) {
                sessionManager.logoutUser();
            }
        }, 250);

        return true;
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