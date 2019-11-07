package com.experta.pushNotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.experta.R;
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

        if (remoteMessage.getNotification() != null) {

            String titulo = remoteMessage.getNotification().getTitle();
            String texto = remoteMessage.getNotification().getBody();
            String sound = remoteMessage.getNotification().getSound();

            Log.i(LOGTAG, "NOTIFICACION RECIBIDA");
            Log.i(LOGTAG, "Título: " + titulo);
            Log.i(LOGTAG, "Texto: " + texto);
            Log.i(LOGTAG, "Sonido: " + sound);

            //Opcional: mostramos la notificación en la barra de estado
            showNotification(titulo, texto, sound);
        }
    }

    private void showNotification(String title, String text, String sound) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.icon)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setTicker("Product Notification received");


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Vibracion custom
        long[] pattern = new long[]{0, 100, 200, 100};
        mBuilder.setVibrate(pattern);

        Notification notification = mBuilder.build();
//        notification.defaults = Notification.DEFAULT_ALL;

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
