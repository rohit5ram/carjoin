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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.pr.carjoin.R;
import com.pr.carjoin.adapters.TripsRecyclerAdapter;
import com.pr.carjoin.pojos.Trips;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by vishnu on 29/3/17.
 */

public class ListTripActivity extends AppCompatActivity {
    private static final String LOG_LABEL = "activities.ListTripActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_trip_layout);

        double pLat = getIntent().getDoubleExtra("pLat", 0.0);
        double pLon = getIntent().getDoubleExtra("pLon", 0.0);
        double dLat = getIntent().getDoubleExtra("dLat", 0.0);
        double dLon = getIntent().getDoubleExtra("dLon", 0.0);

        new FetchTripsAsync("findTrips", new WeakReference<>(this), new LatLng(pLat, pLon), new LatLng(dLat, dLon)).execute();
    }

    static class FetchTripsAsync extends AsyncTask<Void, Void, Data> {
        private final String path;
        private final WeakReference<ListTripActivity> activityWeakReference;
        private final LatLng pickup;
        private final LatLng destination;


        FetchTripsAsync(String path, WeakReference<ListTripActivity> activityWeakReference, LatLng pickup, LatLng destination) {
            this.path = path;
            this.activityWeakReference = activityWeakReference;
            this.pickup = pickup;
            this.destination = destination;
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Data doInBackground(Void... voids) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                return null;
            }

            try {
                JSONObject source = new JSONObject();
                source.put("lat", pickup.latitude);
                source.put("lon", pickup.longitude);

                JSONObject dest = new JSONObject();
                dest.put("lat", destination.latitude);
                dest.put("lon", destination.longitude);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", user.getUid());
                jsonObject.put("source", source);
                jsonObject.put("destination", dest);

                Log.i(LOG_LABEL, "REQUEST BODY :: " + jsonObject.toString());
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("https")
                        .host("us-central1-carjoin-5429b.cloudfunctions.net")
                        .addPathSegment("carjoin")
                        .addPathSegment(path)
                        .build();
                Log.i(LOG_LABEL, "URL :: " + url.toString());

                Response response = CarJoin.client.newBuilder().build().newCall(new Request.Builder().url(url).post(requestBody).build()).execute();
                ResponseBody body = response.body();
                if (response.isSuccessful() && body != null) {
                    try {
                        String responseString = body.string();
                        Log.i(LOG_LABEL, "RESPONSE ::" + responseString);
                        Trips trips = new Gson().fromJson(responseString, Trips.class);
                        if (trips != null) {
                            return new Data(trips, user.getUid(), user.getEmail());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Data data) {
            super.onPostExecute(data);
            activityWeakReference.get().listTrips(data);
        }
    }

    private void listTrips(Data data) {
        ProgressBar progressBar = findViewById(R.id.list_trip_layout_progressBar);
        progressBar.setVisibility(View.GONE);
        if (data != null && data.trips != null && data.trips.size() > 0) {
            RecyclerView recyclerView = findViewById(R.id.trips_list);
            recyclerView.setAdapter(new TripsRecyclerAdapter(data.trips, data.userId, data.email));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            TextView textView = findViewById(R.id.text_view_no_trips_found);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private static class Data {
        final Trips trips;
        final String userId;
        final String email;

        private Data(Trips trips, String userId, String email) {
            this.trips = trips;
            this.userId = userId;
            this.email = email;
        }
    }
}
