package com.pr.carjoin.customViews;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.adapters.MyTripsRecyclerAdapter;
import com.pr.carjoin.pojos.Trip;

/**
 * Created by rams on 6/25/2017.
 */

public abstract class MyTripsFragment extends Fragment {
    private MyTripsRecyclerAdapter tripsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trips_fragment, container, false);
        RecyclerView trips = (RecyclerView) view.findViewById(R.id.trips_list);
        tripsAdapter = new MyTripsRecyclerAdapter(Trip.class, R.layout.my_single_trip_view,
                MyTripsRecyclerAdapter.TripHolder.class,
                getQuery());
        trips.setLayoutManager(new LinearLayoutManager(getContext()));
        trips.setItemAnimator(new DefaultItemAnimator());
        trips.setVisibility(View.VISIBLE);
        trips.setAdapter(tripsAdapter);
        return view;
    }

    protected final DatabaseReference getTripRef() {
        return FirebaseDatabase.getInstance().getReference().child(Util.TRIPS);
    }

    protected final FirebaseUser getCurrentUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tripsAdapter.cleanup();
    }

    protected Iterable<Trip> getTrips(Iterable<DataSnapshot> children) {
        return FluentIterable.from(children)
                .transform(getFunction());
    }

    protected abstract Function<? super DataSnapshot, Trip> getFunction();

    protected Query getQuery() {
        return getTripRef();
    }
}
