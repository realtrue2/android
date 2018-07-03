package com.alex.rssreaderel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.content.Intent;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 18.01.2017.
 */
public class CustomPagerAdapter extends FragmentStatePagerAdapter {

    private Map<Integer, String> mFragmentTags;
    List<String> data;
    private FragmentManager mFragmentManager;
    public CustomPagerAdapter(FragmentManager fm, List<String> data) {
        super(fm);
        this.data = data;
        mFragmentTags = new HashMap<Integer, String>();
        mFragmentManager = fm;
    }

    @Override
    public Fragment getItem(int i) {



        Bundle arguments = new Bundle();

        arguments.putString("ID", data.get(i));
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public int getCount() {
        return data.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof android.app.Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Item " + (position + 1);
    }
    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }
}
