package com.pr.carjoin.activities;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.google.gson.Gson;
import com.pr.carjoin.Constants;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.adapters.TripsRecyclerAdapter;
import com.pr.carjoin.customViews.MyTripsFragment;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.TripQueue;
import com.pr.carjoin.pojos.TripQueueMap;
import com.pr.carjoin.pojos.Trips;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by vishnu on 29/3/17.
 */

public class ListTripActivity extends AppCompatActivity {
    private static final String LOG_LABEL = "activities.ListTripActivity";
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
                    if (firebaseUser != null && trip.published && trip.status.equals(Constants.CREATED)
                            && !trip.owner.id.equals(firebaseUser.getUid()) && trip.endDateTimeMills > new Date().getTime()
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
        ProgressBar progressBar = findViewById(R.id.list_trip_layout_progressBar);
        progressBar.setVisibility(View.GONE);
        if (tripTripQueueMapHashMap.size() > 0) {
            RecyclerView recyclerView = findViewById(R.id.trips_list);
            recyclerView.setAdapter(new TripsRecyclerAdapter(this, tripTripQueueMapHashMap));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = findViewById(R.id.text_view_no_trips_found);
            textView.setVisibility(View.VISIBLE);
        }
    }

    static class FetchTripsAsync extends AsyncTask<Void, Void, Trips> {
        private final String path;
        private final WeakReference<ListTripActivity> activityWeakReference;

        FetchTripsAsync(String path, WeakReference<ListTripActivity> activityWeakReference) {
            this.path = path;
            this.activityWeakReference = activityWeakReference;
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Trips doInBackground(Void... voids) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                return null;
            }

            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("us-central1-carjoin-5429b.cloudfunctions.net")
                    .addPathSegment("carjoin")
                    .addPathSegment(path)
                    .addPathSegment(user.getUid())
                    .build();
            Log.i(LOG_LABEL, "URL :: " + url.toString());
            try {
                Response response = CarJoin.client.newBuilder().build().newCall(new Request.Builder().url(url).build()).execute();
                ResponseBody body = response.body();
                if (response.isSuccessful() && body != null) {
                    try {
                        String responseString = body.string();
                        Log.i(LOG_LABEL, "RESPONSE ::" + responseString);
                        return new Gson().fromJson(responseString, Trips.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Trips trips) {
            super.onPostExecute(trips);
            activityWeakReference.get().listTrips(trips);
        }
    }

    private void listTrips(Trips trips) {

    }

}
