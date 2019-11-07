package com.experta.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.experta.R;
import com.experta.com.experta.model.User;
import com.experta.services.ToastService;
import com.experta.utilities.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class SignInActivity extends Activity {

    private static final String LOGTAG = "SignInActivity";
    private static int SPLASH_TIME_OUT = 1000;

    private GoogleSignInClient googleSignInClient;
    private SignInButton googleSignInButton;

    private String applicationToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        checkForInternetConnection();

        FirebaseApp.initializeApp(this);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                if (alreadyloggedAccount != null) {
                    onLoggedIn(alreadyloggedAccount);
                } else {
                    loadAndShowGoogleButton();
                }

            }
        }, SPLASH_TIME_OUT);
    }

    private void checkForInternetConnection() {

        if ( !NetworkUtils.isInternetAvailable(this) ) {
            ToastService.toastBottom(this, getString(R.string.se_requiere_internet), Toast.LENGTH_LONG);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    System.exit(0);
                }
            }, SPLASH_TIME_OUT - 500);
        }
    }

    private void loadAndShowGoogleButton() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO validar si tengo internet aca tener un try por si no hay...
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        googleSignInButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(LOGTAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {

        //Se obtiene el token actualizado
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(LOGTAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        applicationToken = task.getResult().getToken();

                        // Log and toast
                        Log.i(LOGTAG, "Token: " + applicationToken);
                    }
                });

        CheckIfUserExistsCreateItOtherwise ciuecio = new CheckIfUserExistsCreateItOtherwise();
        ciuecio.execute(googleSignInAccount);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public class CheckIfUserExistsCreateItOtherwise extends AsyncTask<GoogleSignInAccount, Void, Boolean> {

        @Override
        protected Boolean doInBackground(GoogleSignInAccount... googleSignInAccount) {

            Log.i(LOGTAG, "doInBackground");

            User user = NetworkUtils.getUserByEmail(googleSignInAccount[0].getEmail());

            if (user == null) {
                Log.i(LOGTAG, "Inexistent user. Creating it...");

                user = new User(googleSignInAccount[0].getEmail()
                                , googleSignInAccount[0].getDisplayName()
                                , applicationToken);
                if (NetworkUtils.createUser(user)) {

                    Log.i(LOGTAG, "User created.");

                } else {

                    Log.i(LOGTAG, "Unable to create user.");

                    return false;
                }

            } else {
                Log.i(LOGTAG, "Existent user.");

                // Chequear si el application token fue actualizado
                if (!user.getApplicationToken().equals(applicationToken)) {
                    user.setApplicationToken(applicationToken);
                    NetworkUtils.createUser(user);
                }
            }

            BottomNavActivity.user = user;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean couldCreateUser) {

            if (!couldCreateUser) {
                ToastService.toast(getApplicationContext(), getString(R.string.no_pudo_crear_usuario), Toast.LENGTH_LONG);
            } else {
                Intent intent = new Intent(getApplicationContext(), BottomNavActivity.class);
                startActivity(intent);
                finish();
            }

        }

    }

}
