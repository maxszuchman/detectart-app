package com.experta.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.experta.R;
import com.experta.com.experta.model.Contact;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private Contact[] contacts;

    public ContactAdapter(Context context, Contact[] data) {
        super(context, R.layout.lstitem_contact, data);
        this.contacts = data;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View item = inflater.inflate(R.layout.lstitem_contact, null);

        TextView lblFullName = (TextView)item.findViewById(R.id.lblFullName);
        lblFullName.setText(contacts[position].getFullName());

        TextView lblPhone = (TextView)item.findViewById(R.id.lblPhone);
        lblPhone.setText(contacts[position].getPhone());

        return item;
    }
}
