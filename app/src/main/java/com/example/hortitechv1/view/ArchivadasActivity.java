package com.example.hortitechv1.view;

import android.app.Activity;
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
import com.example.hortitechv1.controllers.BitacoraAdapter;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.models.Bitacora;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiBitacora;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArchivadasActivity extends AppCompatActivity implements BitacoraAdapter.BitacoraAdapterListener, NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvArchivadas;
    private BitacoraAdapter adapter;
    private List<Bitacora> bitacorasArchivadas = new ArrayList<>();
    private ApiBitacora apiService;
    private SessionManager sessionManager;

    private DrawerLayout drawerLayout;
    private LinearLayout mainContentContainer;
    private static final float END_SCALE = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivadas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        mainContentContainer = findViewById(R.id.main_content_container);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        setupDrawerAnimation(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        styleLogoutMenuItem(navigationView.getMenu());

        sessionManager = new SessionManager(this);

        apiService = ApiClient.getClient().create(ApiBitacora.class);
        rvArchivadas = findViewById(R.id.rvArchivadas);
        rvArchivadas.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BitacoraAdapter(this, bitacorasArchivadas, this);
        rvArchivadas.setAdapter(adapter);

        cargarArchivadas();
    }

    private void cargarArchivadas() {
        apiService.getBitacoras(true).enqueue(new Callback<List<Bitacora>>() {
            @Override
            public void onResponse(Call<List<Bitacora>> call, Response<List<Bitacora>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bitacorasArchivadas.clear();
                    bitacorasArchivadas.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ArchivadasActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Bitacora>> call, Throwable t) {
                Toast.makeText(ArchivadasActivity.this, "Fallo de conexión.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onArchiveClicked(Bitacora bitacora, int position) {
        apiService.desarchivarBitacora(bitacora.getId_publicacion()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    bitacorasArchivadas.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, bitacorasArchivadas.size());
                    Toast.makeText(ArchivadasActivity.this, "Bitácora restaurada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ArchivadasActivity.this, "Error al restaurar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    @Override public void onEditClicked(Bitacora bitacora) { }
    @Override public void onDeleteClicked(Bitacora bitacora, int position) { }

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
                startActivity(new Intent(ArchivadasActivity.this, HomeActivity.class));
            } else if (itemId == R.id.nav_greenhouses) {
                startActivity(new Intent(ArchivadasActivity.this, InvernaderoActivity.class));
            } else if (itemId == R.id.nav_crops) {
                startActivity(new Intent(ArchivadasActivity.this, CultivosActivity.class));
            } else if (itemId == R.id.nav_log) {
                startActivity(new Intent(ArchivadasActivity.this, BitacoraActivity.class));
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(ArchivadasActivity.this, PerfilActivity.class));
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
            setResult(Activity.RESULT_OK);
            super.onBackPressed();
        }
    }
}