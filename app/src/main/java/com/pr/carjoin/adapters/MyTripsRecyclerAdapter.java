package com.pr.carjoin.adapters;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.pr.carjoin.R;
import com.pr.carjoin.pojos.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vishnu on 17/4/17.
 */

public class MyTripsRecyclerAdapter extends FirebaseRecyclerAdapter<Trip, MyTripsRecyclerAdapter.TripHolder> {
    private static final String DATE_TIME_FORMAT = "dd-MM-yy HH:mm";

    public MyTripsRecyclerAdapter(Class<Trip> modelClass, @LayoutRes int modelLayout, Class<TripHolder> viewHolderClass, Query query) {
        super(modelClass, modelLayout, viewHolderClass, query);
    }

    @Override
    protected void populateViewHolder(MyTripsRecyclerAdapter.TripHolder tripHolder, Trip trip, int position) {
        tripHolder.getSource().append("         : " + trip.sourceAddress);
        tripHolder.getDestination().append(" : " + trip.destAddress);

        tripHolder.getStartDate().append(" : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.beginDateTimeMills)));
        tripHolder.getEndDate().append("   : " + new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(new Date(trip.endDateTimeMills)));
        tripHolder.getStatus().setText(trip.status);
    }

    public static class TripHolder extends RecyclerView.ViewHolder {

        private Button status;
        private TextView source, startDate, endDate, destination;

        public TripHolder(View itemView) {
            super(itemView);

            status = (Button) itemView.findViewById(R.id.status);
            source = (TextView) itemView.findViewById(R.id.source_address);
            startDate = (TextView) itemView.findViewById(R.id.start_date_text_view);
            endDate = (TextView) itemView.findViewById(R.id.end_date_text_view);
            destination = (TextView) itemView.findViewById(R.id.destination_address);
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
    }
}
