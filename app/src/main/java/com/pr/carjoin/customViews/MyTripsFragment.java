package com.pr.carjoin.customViews;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.pr.carjoin.R;
import com.pr.carjoin.activities.CarJoin;
import com.pr.carjoin.adapters.TripHistoryRecyclerAdapter;
import com.pr.carjoin.pojos.Trips;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by rams on 6/25/2017.
 */

public abstract class MyTripsFragment extends Fragment {
    private static final String LOG_LABEL = "MyTripsFragment";
    private RecyclerView tripsRV;
    private ProgressBar progressBar;
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trips_fragment, container, false);
        tripsRV = view.findViewById(R.id.trips_list);
        progressBar = view.findViewById(R.id.loading);
        textView = view.findViewById(R.id.error_message);
        return view;
    }

    private void listTrips(Trips trips) {
        progressBar.setVisibility(View.GONE);
        if (trips != null && trips.size() > 0) {
            TripHistoryRecyclerAdapter tripsAdapter = new TripHistoryRecyclerAdapter(trips);
            tripsRV.setLayoutManager(new LinearLayoutManager(getContext()));
            tripsRV.setItemAnimator(new DefaultItemAnimator());
            tripsRV.setVisibility(View.VISIBLE);
            tripsRV.setAdapter(tripsAdapter);
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    static class FetchTripsAsync extends AsyncTask<Void, Void, Trips> {
        private final String path;
        private final WeakReference<MyTripsFragment> activityWeakReference;

        FetchTripsAsync(String path, WeakReference<MyTripsFragment> activityWeakReference) {
            this.path = path;
            this.activityWeakReference = activityWeakReference;
        }

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
}
