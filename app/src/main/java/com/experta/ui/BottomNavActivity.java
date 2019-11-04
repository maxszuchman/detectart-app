package com.experta.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.experta.R;
import com.experta.com.experta.model.User;
import com.experta.services.ToastService;
import com.experta.utilities.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_nav_activity);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_logout, R.id.navigation_dispositivos, R.id.navigation_contactos)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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

}
