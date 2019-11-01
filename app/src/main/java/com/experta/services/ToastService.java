package com.experta.services;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastService {

    public static void toast(Context context, String message, int length, int gravity, int yOffset) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(gravity, 0, yOffset);
        toast.show();
    }

    public static void toast(Context context, String message, int length, int gravity) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    public static void toast(Context context, String message, int length) {
        toastCenter(context, message, length);
    }

    public static void toastCenter(Context context, String message, int length) {
        toast(context, message, length, Gravity.CENTER);
    }

    public static void toastTop(Context context, String message, int length) {
        toast(context, message, length, Gravity.TOP, -150);
    }

    public static void toastBottom(Context context, String message, int length) {
        toast(context, message, length, Gravity.BOTTOM, 150);
    }

}
