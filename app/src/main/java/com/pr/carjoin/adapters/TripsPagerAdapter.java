package com.pr.carjoin.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pr.carjoin.customViews.PastTripsFragment;
import com.pr.carjoin.customViews.UpcomingTripsFragment;

/**
 * Created by rams on 6/25/2017.
 */

public class TripsPagerAdapter extends FragmentStatePagerAdapter {
    private final int numOfTabs;

    public TripsPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PastTripsFragment();
            case 1:
                return new UpcomingTripsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
