package com.experta.ui.contactos;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Trace;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.experta.R;
import com.experta.com.experta.model.Contact;
import com.experta.com.experta.model.User;
import com.experta.services.NamesService;
import com.experta.services.ToastService;
import com.experta.ui.BottomNavActivity;
import com.experta.ui.SignInActivity;
import com.experta.ui.adapters.ContactAdapter;
import com.experta.utilities.NetworkUtils;

import java.io.IOException;

public class ContactosFragment extends Fragment {

    public static final String LOGTAG = ContactosFragment.class.getSimpleName();

    private static final int REQUEST_CONTACT = 100;
    private static final int READ_CONTACTS_REQUEST = 200;

    private NamesService namesService;

    private Contact[] contacts = new Contact[] {};
    private ContactAdapter adapter;
    private ListView lstContacts;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contactos, container, false);
        setHasOptionsMenu(true);
;
        namesService = new NamesService();

        GetContactsTask getContactsTask = new GetContactsTask();
        getContactsTask.execute(BottomNavActivity.user.getId());

        adapter = new ContactAdapter(getContext(), contacts);
        lstContacts = root.findViewById(R.id.LstContacts);
        lstContacts.setAdapter(adapter);
        lstContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                Contact contact = (Contact) a.getItemAtPosition(position);
                showDeleteContactDialog(contact);
            }
        });

        return root;
    }

    private void showDeleteContactDialog(final Contact contact) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.borrar_contacto))
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        new DeleteContactTask().execute(contact);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_contact, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_contact) {

            // Check for contacts read permission
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted, requesting
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS},
                                                  READ_CONTACTS_REQUEST);
            } else {

                startContactPickerActivity();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == READ_CONTACTS_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startContactPickerActivity();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            return;
        }
    }

    private void startContactPickerActivity() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, REQUEST_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK) {

            // we got a result from the contact picker
            Uri contactUri = data.getData();
            Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();

            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
            String phoneNumber = cursor.getString(column);

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                phoneNumber = cursor.getString(column);

                if (phoneNumber == null || phoneNumber.isEmpty()) {
                    return;
                }
            }

            column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME);
            String displayName = cursor.getString(column);
            String firstName = namesService.getFirstNameFromDisplayName(displayName);
            String lastName =  namesService.getLastNameFromDisplayName(displayName);

            Log.i(LOGTAG, phoneNumber);
            Log.i(LOGTAG, firstName);
            Log.i(LOGTAG, lastName);

            Contact newContact = new Contact(firstName, lastName, phoneNumber);
            AddContactTask addContactTask = new AddContactTask();
            addContactTask.execute(newContact);
        }
    }

    public class DeleteContactTask extends AsyncTask<Contact, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Contact... params) {

            Log.i(LOGTAG, "doInBackground");

            boolean contactDeleted = NetworkUtils.deleteContactByUser(BottomNavActivity.user, params[0]);

            return contactDeleted;
        }

        @Override
        protected void onPostExecute(Boolean contactDeleted) {
            Log.i(LOGTAG, "onPostExecute");

            if (contactDeleted) {
                GetContactsTask getContactsTask = new GetContactsTask();
                getContactsTask.execute(BottomNavActivity.user.getId());
            } else {
                ToastService.toast(getContext(), getString(R.string.no_pudo_borrar_contacto), Toast.LENGTH_SHORT);
            }
        }
    }

    public class AddContactTask extends AsyncTask<Contact, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Contact... params) {

            Log.i(LOGTAG, "doInBackground");

            boolean contactCreated = NetworkUtils.addContactForUser(BottomNavActivity.user, params[0]);

            return contactCreated;
        }

        @Override
        protected void onPostExecute(Boolean contactCreated) {
            Log.i(LOGTAG, "onPostExecute");

            if (contactCreated) {
                GetContactsTask getContactsTask = new GetContactsTask();
                getContactsTask.execute(BottomNavActivity.user.getId());
            } else {
                ToastService.toast(getContext(), getString(R.string.no_pudo_crear_contacto), Toast.LENGTH_SHORT);
            }
        }
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