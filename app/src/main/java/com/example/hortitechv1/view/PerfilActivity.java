package com.example.hortitechv1.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.models.Persona;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiUsuario;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvNombreUsuario, tvCorreo, tvRol;
    private ImageView ivFotoPerfil;
    private SessionManager sessionManager;
    private DrawerLayout drawerLayout;
    private LinearLayout mainContentContainer;
    private static final float END_SCALE = 0.8f;
    private Uri cameraImageUri;

    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    mostrarDatosGuardados();
                }
            });
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    ivFotoPerfil.setImageURI(uri);
                    uploadImage(uri);
                }
            });
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if (result) {
                    ivFotoPerfil.setImageURI(cameraImageUri);
                    uploadImage(cameraImageUri);
                }
            });
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePicture();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

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
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvCorreo = findViewById(R.id.tvCorreo);
        tvRol = findViewById(R.id.tvRol);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        Button btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        mostrarDatosGuardados();
        btnCerrarSesion.setOnClickListener(v -> sessionManager.logoutUser());
        btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditarPerfilActivity.class);
            editProfileLauncher.launch(intent);
        });
        ivFotoPerfil.setOnClickListener(v -> showImagePickerDialog());
    }

    private void mostrarDatosGuardados() {
        String nombre = sessionManager.getUserName();
        String correo = sessionManager.getUserEmail();
        String rol = sessionManager.getUserRol();
        String fotoUrl = sessionManager.getUserFotoUrl();

        if (nombre != null) {
            tvNombreUsuario.setText(nombre);
            tvCorreo.setText(correo);
            tvRol.setText("Rol: " + rol);

            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                Glide.with(this).load(fotoUrl).circleCrop().placeholder(R.drawable.ic_profile_placeholder).into(ivFotoPerfil);
            } else {
                cargarPerfilCompleto();
            }
        } else {
            Toast.makeText(this, "No se pudo cargar la sesión. Por favor, inicia sesión de nuevo.", Toast.LENGTH_LONG).show();
            sessionManager.logoutUser();
        }
    }

    private void cargarPerfilCompleto() {
        ApiUsuario api = ApiClient.getClient().create(ApiUsuario.class);
        api.getAuthenticatedUserProfile(sessionManager.getAuthToken()).enqueue(new Callback<Persona>() {
            @Override
            public void onResponse(Call<Persona> call, Response<Persona> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Persona user = response.body();
                    if (user.getPerfil() != null && user.getPerfil().getFoto_url() != null) {
                        String fotoUrl = user.getPerfil().getFoto_url();
                        sessionManager.updateUserFotoUrl(fotoUrl);
                        Glide.with(PerfilActivity.this)
                                .load(fotoUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(ivFotoPerfil);
                    }
                } else {
                    Log.e("PerfilDebug", "Error al solicitar el perfil completo: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<Persona> call, Throwable t) {
                Log.e("PerfilDebug", "Fallo de red al solicitar perfil completo", t);
            }
        });
    }

    private void uploadImage(Uri imageUri) {
        File file = uriToFile(imageUri);
        if (file == null) {
            Toast.makeText(this, "No se pudo obtener el archivo de la imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_picture", file.getName(), requestFile);
        ApiUsuario api = ApiClient.getClient().create(ApiUsuario.class);
        Call<Persona> call = api.uploadProfilePicture(sessionManager.getAuthToken(), body);

        call.enqueue(new Callback<Persona>() {
            @Override
            public void onResponse(Call<Persona> call, Response<Persona> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Persona updatedUser = response.body();
                    String newFotoUrl = updatedUser.getPerfil().getFoto_url();
                    sessionManager.updateUserFotoUrl(newFotoUrl);
                    Toast.makeText(PerfilActivity.this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                    Glide.with(PerfilActivity.this).load(newFotoUrl).circleCrop().placeholder(R.drawable.ic_profile_placeholder).into(ivFotoPerfil);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Respuesta de error vacía";
                        Log.e("UploadError", "Error en onResponse: " + response.code() + " - " + errorBody);
                        Toast.makeText(PerfilActivity.this, "Error del servidor: " + response.code(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("UploadError", "Error al leer el errorBody: ", e);
                    }
                    mostrarDatosGuardados();
                }
            }
            @Override
            public void onFailure(Call<Persona> call, Throwable t) {
                Log.e("UploadError", "Error en onFailure: " + t.getMessage(), t);
                Toast.makeText(PerfilActivity.this, "Fallo de conexión. Revisa el Logcat.", Toast.LENGTH_LONG).show();
                mostrarDatosGuardados();
            }
        });
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar foto de perfil");
        builder.setItems(new CharSequence[]{"Tomar foto", "Elegir de la galería"}, (dialog, which) -> {
            if (which == 0) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                galleryLauncher.launch("image/*");
            }
        });
        builder.show();
    }
    private void takePicture() {
        try {
            File photoFile = createImageFile();
            cameraImageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
            cameraLauncher.launch(cameraImageUri);
        } catch (IOException ex) {
            Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show();
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalCacheDir();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    private File uriToFile(final Uri uri) {
        File file = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp;
            file = File.createTempFile(fileName, "." + getFileExtension(uri), getExternalCacheDir());
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 OutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            Log.e("FileUtil", "Error creating file from URI", e);
        }
        return file;
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        new android.os.Handler().postDelayed(() -> {
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(PerfilActivity.this, HomeActivity.class));
            } else if (itemId == R.id.nav_greenhouses) {
                startActivity(new Intent(PerfilActivity.this, InvernaderoActivity.class));
            } else if (itemId == R.id.nav_crops) {
                startActivity(new Intent(PerfilActivity.this, CultivosActivity.class));
            } else if (itemId == R.id.nav_log) {
                startActivity(new Intent(PerfilActivity.this, BitacoraActivity.class));
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