package com.egkhan.instagramclonewithfirebase.Login;

import android.content.Context;
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

import com.egkhan.instagramclonewithfirebase.Models.User;
import com.egkhan.instagramclonewithfirebase.R;
import com.egkhan.instagramclonewithfirebase.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by EgK on 7/31/2017.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseMethods firebaseMethods;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Context mContext;
    String email, username, password;
    EditText mEmail, mPassword, mUsername;
    TextView loadingPleaseWait;
    Button btnRegister;
    ProgressBar mProgressBar;

    String append;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: started");

        firebaseMethods = new FirebaseMethods(this);

        initWidgets();
        setupFirebaseAuth();
        init();
    }

    /*
    * Initialize the activity widgets
    * */
    void initWidgets() {
        Log.d(TAG, "initWidgets: initializng widgets");
        mContext = RegisterActivity.this;
        mEmail = (EditText) findViewById(R.id.inputEmail);
        mUsername = (EditText) findViewById(R.id.inputusername);
        mPassword = (EditText) findViewById(R.id.inputPassword);
        mProgressBar = (ProgressBar) findViewById(R.id.registerRequestLoadingProgressBar);
        loadingPleaseWait = (TextView) findViewById(R.id.pleaseWaitTV);
        btnRegister = (Button) findViewById(R.id.registerBtn);

        mProgressBar.setVisibility(View.GONE);
        loadingPleaseWait.setVisibility(View.GONE);

    }

    private void init() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                username = mUsername.getText().toString();

                if (checkInputs(email, password, username)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    loadingPleaseWait.setVisibility(View.VISIBLE);

                    firebaseMethods.registerNewEmail(email, password, username);
                }
            }
        });
    }

    private boolean checkInputs(String email, String username, String password) {
        if (email.equals("") || username.equals("") || password.equals("")) {
            Toast.makeText(mContext, "All field must be filled out", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private void checkIfUserNameExists(final String userName) {
        Log.d(TAG, "checkIfUserNameExists: checking if the userName is exists in the database ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users)).orderByChild(getString(R.string.field_username)).equalTo(userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "onDataChange: found a match" + singleSnapshot.getValue(User.class).getUsername());
                        append = databaseReference.push().getKey().substring(3, 10);
                        Log.d(TAG, "onDataChange: username already exits.Appending random string to name: " + append);
                    }
                }
                String mUsername = "";
                mUsername = userName + append;
                //add new user to database
                firebaseMethods.AddNewUser(email, mUsername, "My description", "", "");
                Toast.makeText(mContext, "Signup successful.Sending verification email", Toast.LENGTH_SHORT).show();

                mAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*
* ****************************FİREBASE*****************************************
* */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        append = "";

        //authentication eğer register olursa user login olarak olmazsa signout olarak değişecek
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //sürekli dinlemeyecek,bir kerelik dinleyecek
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            checkIfUserNameExists(username);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    finish();
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
