package com.pr.carjoin.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pr.carjoin.Constants;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.adapters.MyTripRecyclerAdapter;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.TripQueue;
import com.pr.carjoin.pojos.TripQueueMap;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by vishnu on 17/4/17.
 */

public class MyTripsActivity extends Activity {
    private static final String LOG_LABEL = "activities.MyTripsActivity";
    private DatabaseReference tripsDatabaseReference, tripQueueDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_trip_layout);
        tripsDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Util.TRIPS);
        tripQueueDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Util.TRIP_QUEUE);
        findTrips();
    }

    private void findTrips() {
        tripsDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Trip> tripHashMap = new HashMap<>();
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot child : dataSnapshots) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    Trip trip = child.getValue(Trip.class);
                    if (firebaseUser != null && (trip.status.equals(Constants.CREATED)
                            || trip.status.equals(Constants.STARTED))
                            && trip.owner.id.equals(firebaseUser.getUid())
                            && trip.endDateTimeMills > new Date().getTime()
                            && trip.beginDateTimeMills < trip.endDateTimeMills) {
                        trip.id = child.getKey();
                        tripHashMap.put(trip.id, trip);
                    }
                }
                onTripRequestComplete(this, tripHashMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onTripRequestComplete(ValueEventListener valueEventListener, final HashMap<String, Trip> tripHashMap) {
        tripsDatabaseReference.removeEventListener(valueEventListener);
        Log.i(Util.TAG, LOG_LABEL + " Filtered TripsDBModel Size :: " + tripHashMap.size());
        final Set<String> tripIds = tripHashMap.keySet();

        tripQueueDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<Trip, TripQueueMap> tripTripQueueMapHashMap = new HashMap<>();
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot children : dataSnapshots) {
                    if (tripIds.contains(children.getKey())) {
                        TripQueueMap tripQueueMap = new TripQueueMap();
                        for (DataSnapshot child : children.getChildren()) {
                            tripQueueMap.put(child.getKey(), child.getValue(TripQueue.class));
                        }
                        tripTripQueueMapHashMap.put(tripHashMap.get(children.getKey()), tripQueueMap);
                    }
                }
                onReadComplete(this, tripTripQueueMapHashMap);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onReadComplete(ValueEventListener valueEventListener, HashMap<Trip, TripQueueMap> tripTripQueueMapHashMap) {
        tripQueueDatabaseReference.removeEventListener(valueEventListener);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.list_trip_layout_progressBar);
        progressBar.setVisibility(View.GONE);
        if (tripTripQueueMapHashMap.size() > 0) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.trips_list);
            recyclerView.setAdapter(new MyTripRecyclerAdapter(this, tripTripQueueMapHashMap));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = (TextView) findViewById(R.id.text_view_no_trips_found);
            textView.setVisibility(View.VISIBLE);
        }
    }
}
