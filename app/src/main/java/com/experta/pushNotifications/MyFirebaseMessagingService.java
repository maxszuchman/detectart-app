package com.experta.pushNotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.experta.R;
import com.experta.ui.COActivity;
import com.experta.ui.GASActivity;
import com.experta.ui.RecommendationActivity;
import com.experta.ui.SMOKEActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOGTAG = "android-fcm";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData() != null) {

            String click_action = remoteMessage.getData().get("click_action");
            String titulo = remoteMessage.getData().get("title");
            String texto = remoteMessage.getData().get("body");
            String sound = remoteMessage.getData().get("sound");
            String icon = remoteMessage.getData().get("icon");

            Log.i(LOGTAG, "NOTIFICACION RECIBIDA");
            Log.i(LOGTAG, "Click_action: " + click_action);
            Log.i(LOGTAG, "Título: " + titulo);
            Log.i(LOGTAG, "Texto: " + texto);
            Log.i(LOGTAG, "Sonido: " + sound);
            Log.i(LOGTAG, "Ícono: " + icon);

            showNotification(titulo, texto, sound, click_action);
        }
    }

    private void showNotification(String title, String text, String sound, String click_action) {
        Class<?> classToLaunch;
        switch (click_action) {
            case "com.experta.ui.COActivity":
                classToLaunch = COActivity.class;
                break;
            case "com.experta.ui.GASActivity":
                classToLaunch = GASActivity.class;
                break;
            case "com.experta.ui.SMOKEActivity":
                classToLaunch = SMOKEActivity.class;
                break;
            default:
                classToLaunch = RecommendationActivity.class;
        }

        Log.i(LOGTAG, "classToLaunch: " + classToLaunch.getSimpleName());
        Intent intent = new Intent(getApplicationContext(), classToLaunch);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(pendingIntent)
                        .setTicker("Product Notification received");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Vibracion custom
        long[] pattern = new long[]{0, 100, 200, 100};
        mBuilder.setVibrate(pattern);

        Notification notification = mBuilder.build();

        // Sonido custom
        int soundToPlay;
        switch (sound) {
            case "alarma":
                soundToPlay = R.raw.alarma;
                break;
            case "ding":
                soundToPlay = R.raw.ding;
                break;
            default:
                soundToPlay = R.raw.alarma;
        }

        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" +  soundToPlay);
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify(0, notification);
    }
}
