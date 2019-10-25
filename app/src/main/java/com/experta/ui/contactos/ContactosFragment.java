package com.experta.ui.contactos;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.experta.R;
import com.experta.com.experta.model.Contact;
import com.experta.com.experta.model.Device;
import com.experta.services.ToastService;
import com.experta.ui.BottomNavActivity;
import com.experta.ui.adapters.ContactAdapter;
import com.experta.utilities.NetworkUtils;

import java.io.IOException;

public class ContactosFragment extends Fragment {

    public static final String LOGTAG = ContactosFragment.class.getSimpleName();

    private Contact[] contacts = new Contact[] {};
    private ContactAdapter adapter;
    private ListView lstContacts;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contactos, container, false);

        GetContactsTask getContactsTask = new GetContactsTask();
        getContactsTask.execute(BottomNavActivity.userEmail);

        adapter = new ContactAdapter(getContext(), contacts);
        lstContacts = root.findViewById(R.id.LstDevices);
        lstContacts.setAdapter(adapter);
        lstContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                String selectedOption = ((Contact) a.getItemAtPosition(position)).getFullName();
                ToastService.toast(getContext(), selectedOption, Toast.LENGTH_SHORT);
            }
        });

        return root;
    }

    public class GetContactsTask extends AsyncTask<String, Void, Contact[]> {

        @Override
        protected Contact[] doInBackground(String... params) {

            Log.i(LOGTAG, "doInBackground");

            Contact[] returnedContacts = null;

            try {
                returnedContacts = NetworkUtils.getContactListFromServer(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return returnedContacts;
        }

        @Override
        protected void onPostExecute(Contact[] contacts) {
            Log.i(LOGTAG, "onPostExecute");

            setContacts(contacts);
        }
    }

    public void setContacts(Contact[] contacts) {
        this.contacts = contacts;
        Log.i(LOGTAG, "Contacts: ");
        for (Contact contact : contacts) {
            Log.i(LOGTAG, " " + contact.toString());
        }

        // Lo hacemos asi porque porque adapter.notifyDataSetChanged() no anda
        adapter = new ContactAdapter(getContext(), contacts);
        lstContacts.setAdapter(adapter);
    }

}