package com.egkhan.instagramclonewithfirebase.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by EgK on 7/26/2017.
 */

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    final List<Fragment> fragmentList = new ArrayList<>();
    final HashMap<Fragment,Integer> fragments = new HashMap<>();
    final HashMap<String,Integer> fragmentNumbers = new HashMap<>();
    final HashMap<Integer,String> fragmentNames = new HashMap<>();

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
    public void addFragment(Fragment fragment,String fragmentName)
    {
        fragmentList.add(fragment);
        fragments.put(fragment,fragmentList.size()-1);
        fragmentNumbers.put(fragmentName,fragmentList.size()-1);
        fragmentNames.put(fragmentList.size()-1,fragmentName);
    }
    public Integer getFragmentNumber(String fragmentName)
    {
        if(fragmentNumbers.containsKey(fragmentName))
            return fragmentNumbers.get(fragmentName);
        else
            return null;
    }
    public Integer getFragmentNumber(Fragment fragment)
    {
        if(fragmentNumbers.containsKey(fragment))
            return fragmentNumbers.get(fragment);
        else
            return null;
    }
    public String getFragmentName(Integer fragmentNumber)
    {
        if(fragmentNames.containsKey(fragmentNumber))
            return fragmentNames.get(fragmentNumber);
        else
            return null;
    }
}
