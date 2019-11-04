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

            Log.d(LOGTAG, "NOTIFICACION RECIBIDA");
            Log.d(LOGTAG, "Título: " + titulo);
            Log.d(LOGTAG, "Texto: " + texto);

            //Opcional: mostramos la notificación en la barra de estado
            showNotification(titulo, texto);
        }
    }

    private void showNotification(String title, String text) {

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
        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" +  R.raw.alarma);
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify(0, notification);
    }
}
