package com.experta.services;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastService {

    public static void toast(Context context, String message, int length) {
        Toast toast = Toast.makeText(context, message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
