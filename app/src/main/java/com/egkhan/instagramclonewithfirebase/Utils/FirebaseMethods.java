package com.egkhan.instagramclonewithfirebase.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.egkhan.instagramclonewithfirebase.Home.HomeActivity;
import com.egkhan.instagramclonewithfirebase.Models.Photo;
import com.egkhan.instagramclonewithfirebase.Models.User;
import com.egkhan.instagramclonewithfirebase.Models.UserAccountSettings;
import com.egkhan.instagramclonewithfirebase.Models.UserSettings;
import com.egkhan.instagramclonewithfirebase.Profile.AccountSettingsActivity;
import com.egkhan.instagramclonewithfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by EgK on 8/1/2017.
 */

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference mStorageReference;

    private String userID;
    private Context mContext;
    private double mPhotoUploadProgress;

    public FirebaseMethods(Context context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhoto(String photoType, final String caption, final int imageCount, final String imageUrl, Bitmap bm) {
        Log.d(TAG, "uploadNewPhoto: attempting to upload new photo");
        FilePaths filePaths = new FilePaths();
        //new photo or new profile_photo
        if (photoType.equals(mContext.getString(R.string.new_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading new photo");
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (imageCount + 1));

            //convert image url to bitmap
            if (bm == null) {
                bm = ImageManager.getBitmap(imageUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photo' node and 'user_photos' node
                    addPhotoToDatabase(caption, firebaseUrl.toString());
                    //navigate to the main feed so the user can see their photo.
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: photo upload failed");
                    Toast.makeText(mContext, "photo upload failed", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "Photo upload progress " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress " + progress);
                }
            });

        } else if (photoType.equals(mContext.getString(R.string.profile_photo))) {
            Log.d(TAG, "uploadNewPhoto: uploading new profile photo");

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");

            //convert image url to bitmap
            if (bm == null) {
                bm = ImageManager.getBitmap(imageUrl);
            }
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into 'user_account_settings' node
                    setProfilePhoto(firebaseUrl.toString());

                    ((AccountSettingsActivity) mContext).setViewPager(
                            ((AccountSettingsActivity) mContext).pagerAdapter.getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: photo upload failed");
                    Toast.makeText(mContext, "photo upload failed", Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if (progress - 15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "Photo upload progress " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: upload progress " + progress);
                }
            });
        }

    }

    private void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image = " + url);

        databaseReference.child(mContext.getString(R.string.dbname_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo))
                .setValue(url);
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pasific"));
        return sdf.format(new java.util.Date());
    }

    private void addPhotoToDatabase(String caption, String firebaseUrl) {
        Log.d(TAG, "addPhotoToDatabase: adding photo to database");

        String newPhotoKey = databaseReference.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        String tags = StringManipulation.getTags(caption);
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(firebaseUrl);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        databaseReference.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);
        databaseReference.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);
    }

    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for (DataSnapshot ds : dataSnapshot.child(mContext.getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getChildren()) {
            count++;
        }
        return count;
    }
//    public boolean checkIfUsernameExists(String username, DataSnapshot dataSnapshot) {
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " aldready exits");
//
//        User user = new User();
//        for (DataSnapshot ds : dataSnapshot.child(userID).getChildren()) {
//            user.setUsername(ds.getValue(User.class).getUsername());
//            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
//                return true;
//            }
//        }
//        return false;
//    }

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

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(mContext, "couldnt send verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Add information to user nodes and acount settings
     *
     * @param email
     * @param username
     * @param description
     * @param website
     * @param profile_photo
     */
    public void AddNewUser(String email, String username, String description, String website, String profile_photo) {
        User user = new User(userID, 1, email, StringManipulation.condenseUsername(username));

        databaseReference.child(mContext.getString(R.string.dbname_users)).child(userID).setValue(user);

        UserAccountSettings userAccountSettings = new UserAccountSettings(description, username, (long) 0, (long) 0, (long) 0, profile_photo, StringManipulation.condenseUsername(username), website);
        databaseReference.child(mContext.getString(R.string.dbname_account_settings)).child(userID).setValue(userAccountSettings);
    }

    /**
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserAccountSettings: retreiving user account settings from database");
        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            //user_account_settings node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);
                try {
                    settings.setDisplay_name(ds.child(userID).getValue(UserAccountSettings.class).getDisplay_name());
                    settings.setUsername(ds.child(userID).getValue(UserAccountSettings.class).getUsername());
                    settings.setWebsite(ds.child(userID).getValue(UserAccountSettings.class).getWebsite());
                    settings.setDescription(ds.child(userID).getValue(UserAccountSettings.class).getDescription());
                    settings.setProfile_photo(ds.child(userID).getValue(UserAccountSettings.class).getProfile_photo());
                    settings.setPosts(ds.child(userID).getValue(UserAccountSettings.class).getPosts());
                    settings.setFollowers(ds.child(userID).getValue(UserAccountSettings.class).getFollowers());
                    settings.setFollowing(ds.child(userID).getValue(UserAccountSettings.class).getFollowing());
                    Log.d(TAG, "getUserAccountSettings: retieved userSetting information: " + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointer exception =" + e.getMessage());
                }
            }
            //users node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);
                try {
                    user.setUsername(ds.child(userID).getValue(User.class).getUsername());
                    user.setPhone_number(ds.child(userID).getValue(User.class).getPhone_number());
                    user.setEmail(ds.child(userID).getValue(User.class).getEmail());
                    user.setUser_id(ds.child(userID).getValue(User.class).getUser_id());
                    Log.d(TAG, "getUser: retieved userinformation: " + user.toString());

                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointer exception =" + e.getMessage());
                }
            }
        }
        return new UserSettings(user, settings);
    }

    /**
     * @param displayName
     * @param website
     * @param description
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber) {
        Log.d(TAG, "updateUserAccountSettings: updating user account settings");
        if (displayName != null)
            databaseReference.child(mContext.getString(R.string.dbname_account_settings)).child(userID).child(mContext.getString(R.string.field_display_name)).setValue(displayName);
        if (description != null)
            databaseReference.child(mContext.getString(R.string.dbname_account_settings)).child(userID).child(mContext.getString(R.string.field_description)).setValue(description);
        if (website != null)
            databaseReference.child(mContext.getString(R.string.dbname_account_settings)).child(userID).child(mContext.getString(R.string.field_website)).setValue(website);
        if (phoneNumber != 0)
            databaseReference.child(mContext.getString(R.string.dbname_account_settings)).child(userID).child(mContext.getString(R.string.field_phone_number)).setValue(phoneNumber);
    }

    /*
    ***
     */
    public void updateUsername(String userName) {
        Log.d(TAG, "updateUsername: updating userName to" + userName);
        databaseReference.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.field_username)).setValue(userName);
        databaseReference.child(mContext.getString(R.string.dbname_account_settings)).child(userID).child(mContext.getString(R.string.field_username)).setValue(userName);
    }

    public void updateEmail(String email) {
        Log.d(TAG, "updateUsername: updating email to" + email);
        databaseReference.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.field_email)).setValue(email);
    }


}
