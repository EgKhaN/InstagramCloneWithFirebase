package com.egkhan.instagramclonewithfirebase.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.egkhan.instagramclonewithfirebase.Home.HomeActivity;
import com.egkhan.instagramclonewithfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by EgK on 7/31/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Context mContext;
    ProgressBar progressBar;
    EditText mEmail, mPassword;
    TextView pleaseWaitTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started");

        progressBar = (ProgressBar) findViewById(R.id.loginRequestLoadingProgressBar);
        pleaseWaitTV = (TextView) findViewById(R.id.pleaseWaitTV);
        mEmail = (EditText) findViewById(R.id.inputEmail);
        mPassword = (EditText) findViewById(R.id.inputPassword);
        mContext = LoginActivity.this;

        progressBar.setVisibility(View.GONE);
        pleaseWaitTV.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();
    }

    boolean isStringNull(String text) {
        if (text.equals(""))
            return true;
        else
            return false;
    }

    void init() {
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (!isStringNull(email) && !isStringNull(password)) {
                    progressBar.setVisibility(View.VISIBLE);
                    pleaseWaitTV.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed),
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                pleaseWaitTV.setVisibility(View.GONE);
                            } else {
                                try {
                                    if(user.isEmailVerified())
                                    {
                                        Log.d(TAG, "onComplete: success.Email is verified");
                                        Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                                        startActivity(i);
                                    }
                                    else{
                                        Toast.makeText(mContext, "Email is not verified.Check your email inbox", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        pleaseWaitTV.setVisibility(View.GONE);
                                    }
                                }
                                catch (NullPointerException e)
                                {
                                    Log.e(TAG, "onComplete: NullPointerException"+e.getMessage() );
                                }
                            }
                        }
                    });

                    /*
                    * If the user is logged in navigate to Home activity*/
                    if (mAuth.getCurrentUser() != null) {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
        TextView linkSignUp = (TextView) findViewById(R.id.linkSingUp);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
* ****************************FİREBASE*****************************************
* */
    void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
