package com.example.hortitechv1.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hortitechv1.ItemSpacingDecoration;
import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.controllers.ZonaAdapter;
import com.example.hortitechv1.models.Invernadero;
import com.example.hortitechv1.models.Zona;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiInvernaderos;
import com.example.hortitechv1.network.ApiZona;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ZonaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvZonas;
    private ZonaAdapter adapter;
    private List<Invernadero> listaInvernaderos = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private LinearLayout mainContentContainer;
    private SessionManager sessionManager;
    private static final float END_SCALE = 0.8f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zona);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        mainContentContainer = findViewById(R.id.main_content_container);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        sessionManager = new SessionManager(this);

        setupDrawerAnimation(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        styleLogoutMenuItem(navigationView.getMenu());

        rvZonas = findViewById(R.id.rvZonas);
        rvZonas.setLayoutManager(new LinearLayoutManager(this));
        rvZonas.addItemDecoration(new ItemSpacingDecoration(20));

        int id_invernadero = getIntent().getIntExtra("invernadero_id", -1);

        if (id_invernadero != -1) {
            obtenerInvernaderos(() -> obtenerZonasPorInvernadero(id_invernadero));
        } else {
            Toast.makeText(this, "Error: no se recibió ID de invernadero", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void obtenerInvernaderos(Runnable onComplete) {
        ApiInvernaderos api = ApiClient.getClient().create(ApiInvernaderos.class);
        Call<List<Invernadero>> call = api.getInvernaderos();

        call.enqueue(new Callback<List<Invernadero>>() {
            @Override
            public void onResponse(Call<List<Invernadero>> call, Response<List<Invernadero>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaInvernaderos = response.body();
                    onComplete.run();
                } else {
                    Toast.makeText(ZonaActivity.this, "Error al obtener invernaderos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Invernadero>> call, Throwable t) {
                Toast.makeText(ZonaActivity.this, "Error de red al obtener invernaderos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerZonasPorInvernadero(int invernaderoId) {
        ApiZona api = ApiClient.getClient().create(ApiZona.class);
        Call<List<Zona>> call = api.getZonasPorInvernadero(invernaderoId);
        call.enqueue(new Callback<List<Zona>>() {
            @Override
            public void onResponse(Call<List<Zona>> call, Response<List<Zona>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Zona> zonas = response.body();
                    adapter = new ZonaAdapter(ZonaActivity.this, zonas, listaInvernaderos);
                    rvZonas.setAdapter(adapter);
                } else {
                    Toast.makeText(ZonaActivity.this, "Error al obtener zonas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Zona>> call, Throwable t) {
                Toast.makeText(ZonaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                Log.e("ZonaActivity", "onFailure: ", t);
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
                startActivity(new Intent(ZonaActivity.this, HomeActivity.class));
            } else if (itemId == R.id.nav_crops) {
                startActivity(new Intent(ZonaActivity.this, CultivosActivity.class));
            } else if (itemId == R.id.nav_log) {
                startActivity(new Intent(ZonaActivity.this, BitacoraActivity.class));
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(ZonaActivity.this, PerfilActivity.class));
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