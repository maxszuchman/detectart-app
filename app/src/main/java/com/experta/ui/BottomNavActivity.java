package com.experta.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.experta.R;
import com.experta.com.experta.model.User;
import com.experta.services.ToastService;
import com.experta.utilities.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class BottomNavActivity extends AppCompatActivity {

    public static final String LOGTAG = BottomNavActivity.class.getSimpleName();
    public static final String GOOGLE_ACCOUNT = "google";

    public static User user;

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_nav_activity);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        final BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dispositivos, R.id.navigation_contactos)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_logout:
                        logout();
                        Menu menuNav = navView.getMenu();
                        MenuItem menuItem = menuNav.findItem(R.id.navigation_logout);
                        // Disable a tint color
                        menuItem.setCheckable(false);
                        break;
                    case R.id.navigation_dispositivos:
                        navController.navigate(R.id.navigation_dispositivos);
                        break;
                    case R.id.navigation_contactos:
                        navController.navigate(R.id.navigation_contactos);
                        break;
                }

                return true;
            }
        });

//        populateUserData();

        ToastService.toast(this
                            , getString(R.string.bienvenidoa) + " "
            + (user.getFullName() == null || user.getFullName().isEmpty()?
                        user.getId() : user.getFullName())
                            , Toast.LENGTH_SHORT);
    }

    private void populateUserData() {

        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        // TODO Tomar el token de Firebase y enviarlo
        user = new User(googleSignInAccount.getEmail(), googleSignInAccount.getDisplayName(), "");
    }

    private void logout() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        AlertDialog.Builder builder = new AlertDialog.Builder(BottomNavActivity.this);
        builder.setMessage(R.string.dialog_confirmacion)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // El usuario confirma cerrar sesión
                        googleSignInClient.signOut();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // El usuario cancela cerrar sesión (se mantiene el fragment anterior)
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
