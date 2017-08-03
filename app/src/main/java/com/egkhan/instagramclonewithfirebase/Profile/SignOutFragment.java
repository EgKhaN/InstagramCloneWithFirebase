package com.egkhan.instagramclonewithfirebase.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.egkhan.instagramclonewithfirebase.Login.LoginActivity;
import com.egkhan.instagramclonewithfirebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by EgK on 7/26/2017.
 */

public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    ProgressBar mProgressBar;
    TextView signOutTV, signintOutTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);
        signOutTV = (TextView) view.findViewById(R.id.tvConfirmSignOut);
        signintOutTV = (TextView) view.findViewById(R.id.signingOutTV);
        mProgressBar = (ProgressBar) view.findViewById(R.id.signOutProgressBar);
        Button confirmSignOutBtn = (Button) view.findViewById(R.id.confirmSignOutBtn);
        mProgressBar.setVisibility(View.GONE);
        signintOutTV.setVisibility(View.GONE);
        setupFirebaseAuth();

        confirmSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                signintOutTV.setVisibility(View.VISIBLE);

                mAuth.signOut();
                getActivity().finish();
            }
        });
        return view;
    }
    /*
* ****************************FİREBASE*****************************************
* */

    void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                    Log.d(TAG, "onAuthStateChanged: navigating back to login screen");
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                // ...
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
