package com.pr.carjoin.adapters;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pr.carjoin.R;
import com.pr.carjoin.activities.CarJoin;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.Trips;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TripHistoryRecyclerAdapter extends RecyclerView.Adapter<TripHistoryRecyclerAdapter.ViewHolder> {
    private static final String LOG_LABEL = "adapters.TripsRecyclerAdapter";
    private static final String DATE_TIME_FORMAT = "dd-MM-yy HH:mm";
    private final Trips trips;
    private final int tripsType;
    private final String userId;

    public TripHistoryRecyclerAdapter(Trips trips, int tripsType, String userId) {
        this.trips = trips;
        this.tripsType = tripsType;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_single_trip_view, parent, false);
        return new TripHistoryRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Trip trip = trips.get(holder.getAdapterPosition());

        holder.getSource().append("         : " + trip.sourceAddress);
        holder.getDestination().append(" : " + trip.destAddress);

        holder.getStartDate().append(" : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.beginDateTimeMills)));
        holder.getEndDate().append("   : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.endDateTimeMills)));


        holder.getStatus().setOnClickListener(v -> {
            holder.getProgressBar().setVisibility(View.VISIBLE);
            holder.getStatus().setVisibility(View.GONE);
            Button button = (Button) v;
            String path = "";
            switch (button.getText().toString()) {
                case "START TRIP":
                    path = "startTrip";
                    break;
                case "END TRIP":
                    path = "endTrip";
                    break;
            }
            new UpdateTrip(path, trip.id, new WeakReference<>(TripHistoryRecyclerAdapter.this), new WeakReference<>(holder)).execute();
        });

        if (tripsType == 0) {
            holder.getLayout().setVisibility(View.GONE);
        } else {
            if (!userId.equals(trip.owner.id)) {
                holder.getLayout().setVisibility(View.GONE);
            } else {
                switch (trip.status) {
                    case "CREATED":
                        holder.getStatus().setText("START TRIP");
                        break;
                    case "STARTED":
                        holder.getStatus().setText("END TRIP");
                        break;
                    default:
                        holder.getLayout().setVisibility(View.GONE);
                }
            }
        }
    }

    void updateUI(ViewHolder viewHolder, Boolean aBoolean, String path) {
        if (!aBoolean) {
            viewHolder.getProgressBar().setVisibility(View.GONE);
            viewHolder.getStatus().setVisibility(View.VISIBLE);
        } else {
            switch (path) {
                case "endTrip":
                    viewHolder.getLayout().setVisibility(View.GONE);
                    break;
                case "startTrip":
                    viewHolder.getProgressBar().setVisibility(View.GONE);
                    viewHolder.getStatus().setText("END TRIP");
                    viewHolder.getStatus().setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Button status;
        private TextView source, startDate, endDate, destination;
        private ProgressBar progressBar;
        private RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.status);
            source = itemView.findViewById(R.id.source_address);
            startDate = itemView.findViewById(R.id.start_date_text_view);
            endDate = itemView.findViewById(R.id.end_date_text_view);
            destination = itemView.findViewById(R.id.destination_address);
            progressBar = itemView.findViewById(R.id.loading);
            layout = itemView.findViewById(R.id.layout_button);
        }

        public Button getStatus() {
            return status;
        }

        public TextView getSource() {
            return source;
        }

        public TextView getStartDate() {
            return startDate;
        }

        public TextView getEndDate() {
            return endDate;
        }

        public TextView getDestination() {
            return destination;
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }

        public RelativeLayout getLayout() {
            return layout;
        }
    }

    static class UpdateTrip extends AsyncTask<Void, Void, Boolean> {

        private final String path;
        private final String tripId;
        private final WeakReference<TripHistoryRecyclerAdapter> recyclerAdapterWeakReference;
        private final WeakReference<ViewHolder> viewHolderWeakReference;

        UpdateTrip(String path, String tripId, WeakReference<TripHistoryRecyclerAdapter> recyclerAdapterWeakReference, WeakReference<ViewHolder> viewHolderWeakReference) {
            this.path = path;
            this.tripId = tripId;
            this.recyclerAdapterWeakReference = recyclerAdapterWeakReference;
            this.viewHolderWeakReference = viewHolderWeakReference;
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("https")
                    .host("us-central1-carjoin-5429b.cloudfunctions.net")
                    .addPathSegment("carjoin")
                    .addPathSegment(path)
                    .addPathSegment(tripId)
                    .build();
            Log.i(LOG_LABEL, "URL :: " + url.toString());
            try {
                Response response = CarJoin.client.newBuilder().build().newCall(new Request.Builder().url(url).build()).execute();
                ResponseBody body = response.body();
                if (response.isSuccessful() && body != null) {
                    try {
                        String responseString = body.string();
                        Log.i(LOG_LABEL, "RESPONSE ::" + responseString);
                        Result result = new Gson().fromJson(responseString, Result.class);
                        return result != null && result.result == 1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            recyclerAdapterWeakReference.get().updateUI(viewHolderWeakReference.get(), aBoolean, path);
        }
    }

    static class Result {
        int result;
    }
}
