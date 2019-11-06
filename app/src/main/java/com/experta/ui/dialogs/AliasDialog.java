package com.experta.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.experta.qrScanner.WifiChooser;
import com.experta.R;
import com.experta.services.ToastService;

public class AliasDialog extends DialogFragment {

    WifiChooser wifiChooser;

    public AliasDialog(WifiChooser wifiChooser) {
        super();
        this.wifiChooser = wifiChooser;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_alias, null);
        final EditText aliasET = dialogView.findViewById(R.id.aliasET);

        builder.setView(dialogView)
               .setNeutralButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {

                   public void onClick(DialogInterface dialog, int id) {

                       if (aliasET.getText().toString().isEmpty()) {

                           ToastService.toast(getContext(), getString(R.string.ingrese_alias), Toast.LENGTH_SHORT);

                           FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                           new AliasDialog(wifiChooser).show(fragmentManager, "tagAlerta");

                       } else {

                           wifiChooser.setDeviceAlias(aliasET.getText().toString());
                           wifiChooser.sendConnectionDataToDevice();
                           dialog.cancel();
                       }
                    }
                });

        return builder.create();
    }
}