package com.experta.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.experta.R;
import com.experta.com.experta.model.Device;
import com.experta.com.experta.model.Status;

public class DeviceAdapter extends ArrayAdapter<Device> {

    private Device[] devices;

    public DeviceAdapter(Context context, Device[] data) {
        super(context, R.layout.lstitem_device, data);
        this.devices = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.lstitem_device, null);

        TextView lblAlias = (TextView)item.findViewById(R.id.lblAlias);
        lblAlias.setText(devices[position].getAlias());

        TextView lblStatus = (TextView)item.findViewById(R.id.lblStatus);
        lblStatus.setText(devices[position].getGeneralStatus().name());

        if (devices[position].getGeneralStatus() == Status.ALARM) {
            lblStatus.setTextColor(Color.RED);
        } else if (devices[position].getGeneralStatus() == Status.NORMAL){
            lblStatus.setTextColor(Color.BLUE);
        } else if (devices[position].getGeneralStatus() == Status.INACTIVE){
            lblStatus.setTextColor(Color.parseColor("#b7b814"));
        }

        return(item);
    }
}
