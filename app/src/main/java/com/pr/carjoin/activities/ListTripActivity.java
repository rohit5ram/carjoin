package com.pr.carjoin.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.adapters.TripsRecyclerAdapter;
import com.pr.carjoin.pojos.FirebaseTrip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vishnu on 29/3/17.
 */

public class ListTripActivity extends AppCompatActivity {
    private static final String LOG_LABEL = "activities.ListTripActivity";
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_trip_layout);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Util.TRIPS);
        findTrips();
    }

    private void findTrips() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<FirebaseTrip> tripsList = new ArrayList<>();
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot child : dataSnapshots) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    FirebaseTrip trip = child.getValue(FirebaseTrip.class);
                    if (firebaseUser != null && trip.published && !trip.started && !trip.completed
                            && !trip.userDetails.id.equals(firebaseUser.getUid()) && trip.endDate > new Date().getTime()
                            && trip.beginDate < trip.endDate) {
                        trip.id = child.getKey();
                        tripsList.add(trip);
                    }
                }
                onComplete(this, tripsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void onComplete(ValueEventListener valueEventListener, List<FirebaseTrip> tripsList) {
        databaseReference.removeEventListener(valueEventListener);
        Log.i(Util.TAG, LOG_LABEL + " Filtered Trips Size :: " + tripsList.size());
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.list_trip_layout_progressBar);
        progressBar.setVisibility(View.GONE);
        if (tripsList.size() > 0) {
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.trips_list);
            recyclerView.setAdapter(new TripsRecyclerAdapter(tripsList, this));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = (TextView) findViewById(R.id.text_view_no_trips_found);
            textView.setVisibility(View.VISIBLE);
        }
    }

}
