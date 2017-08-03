package com.egkhan.instagramclonewithfirebase.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.egkhan.instagramclonewithfirebase.Models.User;
import com.egkhan.instagramclonewithfirebase.Models.UserAccountSettings;
import com.egkhan.instagramclonewithfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by EgK on 8/1/2017.
 */

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private String userID;
    private Context mContext;

    public FirebaseMethods(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " aldready exits");

        User user = new User();
        for (DataSnapshot ds : dataSnapshot.child(userID).getChildren()) {
            user.setUsername(ds.getValue(User.class).getUsername());
            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Register new email and password to Firebase Authentication
     *
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else if (task.isSuccessful()) {
                            //send verification email
                            sendVerificationEmail();
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed " + userID);
                        }

                        // ...
                    }
                });
    }
public void sendVerificationEmail(){
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if(user!=null)
    {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful())
            {

            }
            else
            {
                Toast.makeText(mContext, "couldnt send verification email", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }
}
    /**
     * Add information to user nodes and acount settings
     * @param email
     * @param username
     * @param description
     * @param website
     * @param profile_photo
     */
    public void AddNewUser(String email,String username,String description, String website, String profile_photo)
    {
        User user = new User(userID,1,email,StringManipulation.condenseUsername(username));

        databaseReference.child(mContext.getString(R.string.dbname_users)).child(userID).setValue(user);

        UserAccountSettings userAccountSettings = new UserAccountSettings(description,username,(long) 0,(long)0,(long)0,profile_photo,StringManipulation.condenseUsername(username),website);
        databaseReference.child(mContext.getString(R.string.dbname_account_settings)).child(userID).setValue(userAccountSettings);
    }
}
