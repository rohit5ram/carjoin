package com.pr.carjoin.customViews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.common.base.Function;
import com.google.firebase.database.DataSnapshot;
import com.pr.carjoin.pojos.Trip;

import java.lang.ref.WeakReference;

/**
 * Created by rams on 6/25/2017.
 */

public class UpcomingTripsFragment extends MyTripsFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        new FetchTripsAsync("futureTrips", new WeakReference<>(this)).execute();
        return view;
    }
}
