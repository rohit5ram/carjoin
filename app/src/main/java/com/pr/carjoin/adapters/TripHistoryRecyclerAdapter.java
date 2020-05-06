package com.pr.carjoin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pr.carjoin.R;
import com.pr.carjoin.pojos.Trip;
import com.pr.carjoin.pojos.Trips;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripHistoryRecyclerAdapter extends RecyclerView.Adapter<TripHistoryRecyclerAdapter.ViewHolder> {
    private static final String LOG_LABEL = "adapters.TripsRecyclerAdapter";
    private static final String DATE_TIME_FORMAT = "dd-MM-yy HH:mm";
    private final Trips trips;

    public TripHistoryRecyclerAdapter(Trips trips) {
        this.trips = trips;
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
        holder.getStatus().setText(trip.status);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Button status;
        private TextView source, startDate, endDate, destination;

        public ViewHolder(View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.status);
            source = itemView.findViewById(R.id.source_address);
            startDate = itemView.findViewById(R.id.start_date_text_view);
            endDate = itemView.findViewById(R.id.end_date_text_view);
            destination = itemView.findViewById(R.id.destination_address);
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
