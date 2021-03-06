package com.egkhan.instagramclonewithfirebase.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.egkhan.instagramclonewithfirebase.R;
import com.egkhan.instagramclonewithfirebase.Utils.BottomNavigationViewHelper;
import com.egkhan.instagramclonewithfirebase.Utils.FirebaseMethods;
import com.egkhan.instagramclonewithfirebase.Utils.SectionsStatePagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

/**
 * Created by EgK on 7/26/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";
    public static final int ACTIVITY_NUM = 4;

    private Context mContext;
    public SectionsStatePagerAdapter pagerAdapter;
    ViewPager viewPager;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        Log.d(TAG, "onCreate: started");
        mContext = AccountSettingsActivity.this;
        viewPager = (ViewPager) findViewById(R.id.container);
        relativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);
        setupSettingsList();
        setupFragments();
        setupBottomNavigationView();
        getIncomingIntent();

        //setup backarrow for navigating back to profileACtivity
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to profile activity");
                finish();
            }
        });
    }

    void getIncomingIntent() {
        Intent intent = getIntent();

        //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
        if (intent.hasExtra(getString(R.string.selected_image)) || intent.hasExtra(getString(R.string.selected_bitmap))) {
            Log.d(TAG, "getIncomingIntent: new incoming imgURL");
            if (intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))) {
                if (intent.hasExtra(getString(R.string.selected_image))) {
                    //set the new profile picutre
                    FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0, intent.getStringExtra(getString(R.string.selected_image)), null);
                } else if (intent.hasExtra(getString(R.string.selected_bitmap))) {
                    //set the new profile picutre
                    FirebaseMethods firebaseMethods = new FirebaseMethods(mContext);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0, null, (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }

            }
        }

        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    void setupFragments() {
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));//fragment 0
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment));//fragment 1
    }

    public void setViewPager(int fragmentNumber) {
        relativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigation to fragment number" + fragmentNumber);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(fragmentNumber);
    }

    void setupSettingsList() {
        Log.d(TAG, "setupSettingsList: initialazing Account settings list");
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment));//fragment 0
        options.add(getString(R.string.sign_out_fragment));//fragment 1

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment" + position);
                setViewPager(position);
            }
        });
    }

    /**
     * Bottom NavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomnavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(this, bottomNavigationViewEx);
        //activity değişince ,alakalı menu de seçili olsun diye
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


}
