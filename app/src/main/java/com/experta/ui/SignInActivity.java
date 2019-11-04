package com.experta.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.experta.R;
import com.experta.services.ToastService;
import com.experta.utilities.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

public class SignInActivity extends Activity {

    private static final String TAG = "SignInActivity";
    private static int SPLASH_TIME_OUT = 3000;

    private GoogleSignInClient googleSignInClient;
    private SignInButton googleSignInButton;

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

        if ( !NetworkUtils.isNetworkConnected(this) ) {
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
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, BottomNavActivity.class);
        intent.putExtra(BottomNavActivity.GOOGLE_ACCOUNT, googleSignInAccount);

        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
