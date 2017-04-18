package com.pr.carjoin.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.pr.carjoin.Constants;
import com.pr.carjoin.R;
import com.pr.carjoin.Util;
import com.pr.carjoin.activities.MyTripsActivity;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.TripQueueMap;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by vishnu on 17/4/17.
 */

public class MyTripRecyclerAdapter extends RecyclerView.Adapter<MyTripRecyclerAdapter.ViewHolder> {
    private static final String DATE_TIME_FORMAT = "dd-MM-yy HH:mm";
    private final WeakReference<Activity> activityWeakReference;
    private final HashMap<Trip, TripQueueMap> tripTripQueueMapHashMap;
    private final ArrayList<Trip> trips;

    public MyTripRecyclerAdapter(Activity activity, HashMap<Trip, TripQueueMap> tripTripQueueMapHashMap) {
        this.activityWeakReference = new WeakReference<Activity>(activity);
        this.tripTripQueueMapHashMap = tripTripQueueMapHashMap;
        this.trips = new ArrayList<>(tripTripQueueMapHashMap.keySet());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_single_trip_view, parent, false);
        return new MyTripRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (trips.size() <= 0) {
            MyTripsActivity myTripsActivity = (MyTripsActivity) activityWeakReference.get();
            myTripsActivity.findViewById(R.id.trips_list).setVisibility(View.GONE);
            myTripsActivity.findViewById(R.id.text_view_no_trips_found).setVisibility(View.VISIBLE);
            return;
        }
        final Trip trip = trips.get(holder.getAdapterPosition());
        holder.getSource().append("         : " + trip.sourceAddress);
        holder.getDestination().append(" : " + trip.destAddress);
        holder.getStartDate().append(" : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.beginDateTimeMills)));
        holder.getEndDate().append("   : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.endDateTimeMills)));

        switch (trip.status) {
            case Constants.CREATED:
                holder.getPositive().setText("START");
                break;
            case Constants.STARTED:
                holder.getPositive().setText("STOP");
                break;
        }
        holder.getPositive().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                holder.getNegative().setEnabled(false);
                holder.getPositive().setEnabled(false);
                String url = Util.TRIPS + "/" + trip.id + "/status";
                final String value;
                if (button.getText().equals("START")) {
                    value = Constants.STARTED;
                } else {
                    value = Constants.STOPPED;
                }
                FirebaseDatabase.getInstance().getReference(url).setValue(value)
                        .addOnCompleteListener(activityWeakReference.get(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (Objects.equals(value, Constants.STOPPED)) {
                                    trips.remove(trip);
                                    tripTripQueueMapHashMap.remove(trip);
                                    MyTripRecyclerAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                                } else {
                                    holder.getNegative().setEnabled(true);
                                    holder.getPositive().setEnabled(true);
                                    holder.getPositive().setText("STOP");
                                }
                            }
                        });
            }
        });
        holder.getNegative().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.getNegative().setEnabled(false);
                holder.getPositive().setEnabled(false);
                String url = Util.TRIPS + "/" + trip.id + "/status";
                FirebaseDatabase.getInstance().getReference(url).setValue(Constants.CANCELLED)
                        .addOnCompleteListener(activityWeakReference.get(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                trips.remove(trip);
                                tripTripQueueMapHashMap.remove(trip);
                                MyTripRecyclerAdapter.this.notifyItemRemoved(holder.getAdapterPosition());
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Button positive, negative;
        private TextView source, startDate, endDate, destination;

        public ViewHolder(View itemView) {
            super(itemView);

            positive = (Button) itemView.findViewById(R.id.request_button);
            negative = (Button) itemView.findViewById(R.id.ignore_button);
            source = (TextView) itemView.findViewById(R.id.source_address);
            startDate = (TextView) itemView.findViewById(R.id.start_date_text_view);
            endDate = (TextView) itemView.findViewById(R.id.end_date_text_view);
            destination = (TextView) itemView.findViewById(R.id.destination_address);
        }

        public Button getPositive() {
            return positive;
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

        public Button getNegative() {
            return negative;
        }
    }
}
