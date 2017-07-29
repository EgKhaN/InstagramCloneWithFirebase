package com.egkhan.instagramclonewithfirebase.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.egkhan.instagramclonewithfirebase.R;
import com.egkhan.instagramclonewithfirebase.Utils.BottomNavigationViewHelper;
import com.egkhan.instagramclonewithfirebase.Utils.GridImageAdapter;
import com.egkhan.instagramclonewithfirebase.Utils.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

/**
 * Created by EgK on 7/24/2017.
 */

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    public static final int ACTIVITY_NUM =4;
    private static final int NUM_GRID_COLUMNS = 3;
    ImageView profilePhoto;
private ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started");

        setupActivityWidgets();
        setupBottomNavigationView();
        setupToolbar();
        setProfileImage();

        tempGridSetup();
    }
    private void tempGridSetup()
    {
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRR8MNChOqd4xNXffm3ObmMhzHs6UHeu4jCuR8VLxojMgnXg27k4g");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSoqcRgJA_J9b5JH_EcZFCGCIQTNswUSQT7UKDtQu2DUylIBbQQAw");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0XiK_uGlKT-0Rcg77qJFyr-0WMriM4nuNG89sF-YgVQYsS5ii");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRx0exMTjMtAg8lgfPmunBCLwsDub8YJStqlOiABMCI6plsTYrvrA");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSDmMB7ywUF3w1KNwPP5bWfOsektL11jIoG0SoafndrQYLLqyneFg");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQuCmwpZzoai8m6oaaSN_RJLtAb6xxt3MS2k1uXxExRV84EZwdZog");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQfHmWzhdeWGG2Hof99VAUaIVRmUAqVD-9Dflgn016nGiab0KYk");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT6bhgO_L23ZtYzlxqHqJL0gjkxPoiz6-UcI8al6ta8Me0RFWZ5");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT9zDNEvERjluo6Gl0O6ypO8u0V1wkAVMUW6JL_BXDhVTbhL5PAGA");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRvBkuPL1Sg3vBxHom63VII20sT0JkqteKBiBZeaw4QdT57aVzWbg");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSpgqCCRMeZdlIOYQH7SYCC3Oidi9tKwHj48P-53ea-2yG7Cc2iKw");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTTQghK1vOI24o-XVFEV7g3YW_vwfqYAm0DoQXKdBhPLy85lz9Tmg");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQb0b63D4yaPiseWBc2kIEUkwOPJ0zvhLrIdBabA78zLEkSwb6h");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyL9DXZQvpz2t0FqWSnbk3Bx5XjuBMFCtu2_aAF5Ps1lsr140BLw");
        imageUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQyQ7QG6P-N69tIhC7m0zqvFPOP8iRIuOxBMARTXa413cKNJeMr");
        setUpImageGrid(imageUrls);
    }
    private void setUpImageGrid(ArrayList<String> imageURLs)
    {
        GridView gridview = (GridView)findViewById(R.id.gridview);

        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridview.setColumnWidth(imageWidth);
        GridImageAdapter adapter = new GridImageAdapter(this, R.layout.layout_grid_imageview,"",imageURLs);
        gridview.setAdapter(adapter);
    }
    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile photo");
        String imageUrl = "www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72IeLf";
        UniversalImageLoader.setImage(imageUrl,profilePhoto,progressBar,"https://");
    }
    private void setupActivityWidgets(){
        progressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
        progressBar.setVisibility(View.GONE);
        profilePhoto = (ImageView)findViewById(R.id.profile_photo);
    }
    private void setupToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent intent = new Intent(ProfileActivity.this,AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomnavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this,bottomNavigationViewEx);
        //activity değişince ,alakalı menu de seçili olsun diye
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
