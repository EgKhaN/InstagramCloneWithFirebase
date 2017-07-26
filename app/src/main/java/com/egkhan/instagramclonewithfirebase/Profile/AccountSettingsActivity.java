package com.egkhan.instagramclonewithfirebase.Profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.egkhan.instagramclonewithfirebase.R;
import com.egkhan.instagramclonewithfirebase.Utils.SectionsStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by EgK on 7/26/2017.
 */

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";
    private Context mContext;
    SectionsStatePagerAdapter pagerAdapter;
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

    void setupFragments() {
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment));//fragment 0
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment));//fragment 1
    }
    void setViewPager(int fragmentNumber)
    {
        relativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigation to fragment number"+fragmentNumber);
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
                Log.d(TAG, "onItemClick: navigating to fragment"+position);
                setViewPager(position);
            }
        });
    }
}
