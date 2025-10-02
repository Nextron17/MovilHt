package com.example.hortitechv1.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.hortitechv1.R;
import com.example.hortitechv1.controllers.SessionManager;
import com.example.hortitechv1.models.TokenRequest;
import com.example.hortitechv1.network.ApiClient;
import com.example.hortitechv1.network.ApiNotificaciones; // O ApiNotificaciones, según lo tengas nombrado
import com.example.hortitechv1.view.NotificacionesActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicioDeMensajeria extends FirebaseMessagingService {

    private static final String TAG = "FCM_Service";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Nuevo token generado: " + token);
        enviarToken(this, token);
    }

    // --- MÉTODO MODIFICADO ---
    // Ahora lee desde "getData()" para funcionar siempre.
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "Mensaje recibido desde: " + remoteMessage.getFrom());

        // Verificamos si el mensaje contiene un payload de "data"
        if (remoteMessage.getData().size() > 0) {
            // Obtenemos el título y el cuerpo desde el mapa de datos
            String titulo = remoteMessage.getData().get("title");
            String cuerpo = remoteMessage.getData().get("body");

            // Nos aseguramos de que no sean nulos antes de mostrar la notificación
            if (titulo != null && cuerpo != null) {
                mostrarNotificacion(titulo, cuerpo);
            }
        }
    }

    public static void enviarTokenManualmente(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Obtener el token actual
                    String token = task.getResult();
                    Log.d(TAG, "Token obtenido manualmente: " + token);
                    enviarToken(context, token);
                });
    }

    private static void enviarToken(Context context, String token) {
        SessionManager sessionManager = new SessionManager(context);
        int userIdInt = sessionManager.getUserId();

        if (userIdInt == -1) {
            Log.w(TAG, "Usuario no logueado. El token no se enviará.");
            return;
        }

        String userIdString = String.valueOf(userIdInt);
        ApiNotificaciones api = ApiClient.getClient().create(ApiNotificaciones.class); // O ApiNotificaciones
        TokenRequest requestBody = new TokenRequest(userIdString, token);
        Call<ResponseBody> call = api.registrarToken(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token registrado en el backend exitosamente.");
                } else {
                    Log.e(TAG, "Error al registrar el token. Código: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo de conexión al registrar el token.", t);
            }
        });
    }

    private void mostrarNotificacion(String titulo, String cuerpo) {
        Intent intent = new Intent(this, NotificacionesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "canal_alertas_hortitech";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notifications) // ¡Asegúrate que este ícono sea blanco y transparente!
                .setContentTitle(titulo)
                .setContentText(cuerpo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Alertas de Cultivo", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}