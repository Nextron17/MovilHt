package com.example.hortitechv1.view.fragments; // O tu paquete

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.hortitechv1.R;
import com.example.hortitechv1.view.BitacoraActivity;
import com.example.hortitechv1.view.CultivosActivity;
import com.example.hortitechv1.view.InvernaderoActivity;
import com.example.hortitechv1.view.NotificacionesActivity;
import com.example.hortitechv1.view.PerfilActivity;

public class HomeFragment extends Fragment {

    private static class DashboardItem {
        final String title;
        final String description;
        final int iconResId;
        final Class<?> activityToOpen; // Clase de la Activity a abrir

        DashboardItem(String title, String description, int iconResId, Class<?> activityToOpen) {
            this.title = title;
            this.description = description;
            this.iconResId = iconResId;
            this.activityToOpen = activityToOpen;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        setWelcomeMessage(view.findViewById(R.id.tv_welcome_message), "Operario");

        GridLayout gridLayout = view.findViewById(R.id.dashboard_grid);

        // Definimos las 6 opciones que se mostrarán en el panel
        DashboardItem[] items = {
                new DashboardItem("Invernaderos", "Gestiona los invernaderos.", R.drawable.ic_sprout, InvernaderoActivity.class),
                new DashboardItem("Cultivos", "Revisa tus cultivos.", R.drawable.ic_package, CultivosActivity.class),
                new DashboardItem("Bitácora", "Revisa eventos.", R.drawable.ic_book_text, BitacoraActivity.class),
                new DashboardItem("Perfil", "Configura tu perfil.", R.drawable.ic_settings, PerfilActivity.class),
                new DashboardItem("Notificaciones", "Alertas y avisos.", R.drawable.ic_notifications, NotificacionesActivity.class)



        };

        // Poblar el GridLayout dinámicamente
        int cardIndex = 0;
        for (DashboardItem item : items) {
            addCardToGrid(gridLayout, item, cardIndex * 75L); // 75ms de retraso entre tarjetas
            cardIndex++;
        }

        return view;
    }

    private void addCardToGrid(GridLayout gridLayout, DashboardItem item, long startDelay) {
        if (getContext() == null) return;

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cardView = inflater.inflate(R.layout.card_dashboard_item, gridLayout, false);

        ImageView icon = cardView.findViewById(R.id.card_icon);
        TextView title = cardView.findViewById(R.id.card_title);
        TextView description = cardView.findViewById(R.id.card_description);

        icon.setImageResource(item.iconResId);
        title.setText(item.title);
        description.setText(item.description);

        // Lógica de clic para navegar a la Activity correspondiente
        cardView.setOnClickListener(v -> {
            if (item.activityToOpen != null) {
                Intent intent = new Intent(getActivity(), item.activityToOpen);
                startActivity(intent);
            }
        });

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(16, 16, 16, 16);
        cardView.setLayoutParams(params);

        gridLayout.addView(cardView);

        // Animación de entrada
        cardView.setAlpha(0f);
        cardView.setTranslationY(50f);
        cardView.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(startDelay)
                .setDuration(400)
                .start();
    }

    private void setWelcomeMessage(TextView textView, String userName) {
        Context context = getContext();
        if (context == null) return;

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append("Bienvenido de nuevo, ");
        int start = builder.length();
        builder.append(userName);
        int end = builder.length();
        builder.append(".");

        builder.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccentIcon)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        textView.setText(builder);
    }
}