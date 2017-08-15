package com.egkhan.instagramclonewithfirebase.Profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.egkhan.instagramclonewithfirebase.Dialogs.ConfirmPasswordDialog;
import com.egkhan.instagramclonewithfirebase.Models.User;
import com.egkhan.instagramclonewithfirebase.Models.UserAccountSettings;
import com.egkhan.instagramclonewithfirebase.Models.UserSettings;
import com.egkhan.instagramclonewithfirebase.R;
import com.egkhan.instagramclonewithfirebase.Utils.FirebaseMethods;
import com.egkhan.instagramclonewithfirebase.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by EgK on 7/26/2017.
 */

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener {
    private static final String TAG = "EditProfileFragment";
    ImageView profileImageView;
    String userID;
    UserSettings mUserSettings;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseMethods firebaseMethods;

    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    TextView mChangeProfilePhoto;
    CircleImageView mProfilePhoto;

    @Override
    public void onConfirmPassword(String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

// ///////////////////////////////Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User re-authenticated.");
                            final String email = mEmail.getText().toString();
                            //////////////////////////check to see if the email is not alrady present in the database
                            mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            if (task.getResult().getProviders().size() == 1) {
                                                Log.d(TAG, "onComplete: that email is already in use");
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.d(TAG, "onComplete: that email is available");

                                                //////////////////////the email is available so update it
                                                mAuth.getCurrentUser().updateEmail(email)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    Toast.makeText(getActivity(), "Email updated", Toast.LENGTH_SHORT).show();
                                                                    firebaseMethods.updateEmail(email);
                                                                }
                                                            }
                                                        });
                                            }
                                        } catch (NullPointerException e) {
                                            Log.d(TAG, "onComplete: NullPointerException " + e.getMessage());
                                        }
                                    }
                                }
                            });
                        } else
                            Log.d(TAG, "onComplete: re-authetication failed");
                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);


        initWidgets(view);
        setupFirebaseAuth();
        //setProfileImage();
        //back arrow for navigating back to ProfileActivity
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkMark = (ImageView) view.findViewById(R.id.saveChanges);
        checkMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileSettings();
            }
        });
        return view;
    }

    void initWidgets(View view) {
        mDisplayName = (EditText) view.findViewById(R.id.displayNameET);
        mUsername = (EditText) view.findViewById(R.id.usernameET);
        mWebsite = (EditText) view.findViewById(R.id.websiteEt);
        mDescription = (EditText) view.findViewById(R.id.descriptionEt);
        mEmail = (EditText) view.findViewById(R.id.emailEt);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumberEt);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhotoText);
        profileImageView = (ImageView) view.findViewById(R.id.profile_photo);
        firebaseMethods = new FirebaseMethods(getActivity());

    }

    void saveProfileSettings() {
        final String displayName = mDisplayName.getText().toString();
        final String userName = mUsername.getText().toString();
        final String webSite = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());


        Log.d(TAG, "onDataChange: CURRENT USERNAME :");

        //case1 : the user did not change their username
        if (!mUserSettings.getUser().getUsername().equals(userName)) {
            checkIfUserNameExists(userName);
        }
        //case2 : if  user did change his email
        //email değişince authentication'da değişiklik lazım
        if (!mUserSettings.getUser().getEmail().equals(email)) {
            //step 1- reauthenticate
            // confirm the password and email
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            //dialog kapandığında hedefin EditProfileFragment olduğunu bilecek,bu sayede arada değişken gönderebilicez
            dialog.setTargetFragment(EditProfileFragment.this, 1);
            //step 2- check if the email already is registered
            //fetProvidersForEmail(String email)
            //step 2- change the email
            //submit the new email to the database and authentication
        }
    }

    /*
    * check if userName is exists in the database
    * */
    private void checkIfUserNameExists(final String userName) {
        Log.d(TAG, "checkIfUserNameExists: checking if the userName is exists in the database ");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_users)).orderByChild(getString(R.string.field_username)).equalTo(userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //add the username
                    firebaseMethods.updateUsername(userName);
                    Toast.makeText(getActivity(), "Saved username", Toast.LENGTH_SHORT).show();
                }

                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.exists()) {
                        Log.d(TAG, "onDataChange: found a match" + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username  already exists", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void setProfileWidgets(UserSettings userSettings) {
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database");
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        mUserSettings = userSettings;
        UniversalImageLoader.setImage(settings.getProfile_photo(), profileImageView, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(user.getEmail());
        mPhoneNumber.setText(String.valueOf(user.getPhone_number()));
    }

    /*
* ****************************FİREBASE*****************************************
* */
    void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();

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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieve user information from database
                setProfileWidgets(firebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user in the question
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
